package io.github.luversof.boot.util;

import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.PathContainer;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPattern.PathMatchInfo;

import io.github.luversof.boot.core.CoreBaseProperties;
import io.github.luversof.boot.core.CoreModuleProperties;
import io.github.luversof.boot.core.CoreProperties;
import io.github.luversof.boot.core.CoreResolveType;
import io.github.luversof.boot.support.ModuleNameResolver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServletRequestUtil {
	
	private static final PathMatcher pathMatcher = new AntPathMatcher();
	private static final String IPV4_PATTERN = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

	public static String getRemoteAddr() {
		var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.debug("ServletRequestUtil.getRemoteAddr() : remoteAddr : {},x-forwarded-for : {}", request.getRemoteAddr(), request.getHeader("X-Forwarded-For"));
		var xForwardedForHeaders = request.getHeader("X-Forwarded-For");
		String remoteAddr = null;
        if (xForwardedForHeaders == null || xForwardedForHeaders.length() == 0) {
            remoteAddr = request.getRemoteAddr();
        } else {
        	remoteAddr = xForwardedForHeaders.split(",")[0];
        }
		return remoteAddr;
	}
	
	public static String getUserAgent() {
		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getHeader(HttpHeaders.USER_AGENT);
	}
	
	/**
	 * 내부 요청인 경우 체크
	 * serverName이 도메인이 아닌 ip 형태인 경우 내부 효출로 판단함
	 * @return
	 */
	public static boolean isInternalRequest(HttpServletRequest request) {
		var serverName = request.getServerName();
		if (serverName.equals("localhost")) {
			return true;
		}
		return serverName.matches(IPV4_PATTERN);
	}
	
	/**
	 * 현재 요청에 대한 액티브 모듈 조회
	 * @param request
	 * @return
	 */
	public static Entry<String, CoreProperties> getModulePropertiesEntry(HttpServletRequest request) {
		CoreModuleProperties coreModuleProperties = ApplicationContextUtil.getApplicationContext().getBean(CoreModuleProperties.class);
		CoreBaseProperties coreBaseProperties = ApplicationContextUtil.getApplicationContext().getBean(CoreBaseProperties.class);
		Assert.notEmpty(coreModuleProperties.getModules(), "coreProperties is not set");
		
		var modules = coreModuleProperties.getModules();
		if (modules.size() == 1) {
			return modules.entrySet().stream().findAny().orElse(null);
		}
		
		var resolveType = coreBaseProperties.getResolveType();
		
		Entry<String, CoreProperties> module;
		// 내부 접근인 경우 임의 coreModuleProperties로 처리함
		if (CoreResolveType.ADD_PATH_PATTERN == resolveType) {
			module = getModuleEntryByAddPathPattern(request, coreModuleProperties);
		} else if (CoreResolveType.MODULE_NAME_RESOLVER == resolveType) {
			module = getModuleEntryByModuleNameResolver(coreModuleProperties);
		} else if(modules.size() > 1 && isInternalRequest(request)) { // 내부 접근의 별도 resolveType 설정이 없는 멀티 모듈의 경우 임의 coreModuleProperties로 처리함
			module = modules.entrySet().stream().findFirst().orElse(null);
		} else {
			module = getModuleEntryByDomain(request, coreModuleProperties);
		}
		
		if (module == null) {
			module= modules.entrySet().stream().findFirst().orElse(null);
		}
		return module;
	}
	
	private static Entry<String, CoreProperties> getModuleEntryByAddPathPattern(HttpServletRequest request, CoreModuleProperties coreModuleProperties) {
		return coreModuleProperties.getModules().entrySet().stream().filter(moduleEntry -> Arrays.asList(moduleEntry.getValue().getAddPathPatterns()).stream().anyMatch(addPathPattern -> pathMatcher.match(addPathPattern, request.getServletPath()))).findAny().orElse(null);
	}
	
	private static Entry<String, CoreProperties> getModuleEntryByDomain(HttpServletRequest request, CoreModuleProperties coreModuleProperties) {
		// 해당 도메인에 해당하는 모듈 entry list 확인
		List<Entry<String, CoreProperties>> moduleEntryList = coreModuleProperties.getModules().entrySet().stream().filter(moduleEntry ->
			moduleEntry.getValue().getDomain() != null && (
				checkDomain(request, moduleEntry.getValue().getDomain().getWebList())
				|| checkDomain(request, moduleEntry.getValue().getDomain().getMobileWebList())
				|| checkDomain(request, moduleEntry.getValue().getDomain().getDevDomainList())
			)
		).toList();
		
		if (moduleEntryList.isEmpty()) {
			return null;
		}
		
		// 대상 entry list가 2개 이상인 경우 path 까지 체크
		if (moduleEntryList.size() > 1) {
			moduleEntryList = moduleEntryList.stream().filter(moduleEntry -> 
				checkDomainWithPath(request, moduleEntry.getValue().getDomain().getWebList())
				|| checkDomainWithPath(request, moduleEntry.getValue().getDomain().getMobileWebList())
				|| checkDomainWithPath(request, moduleEntry.getValue().getDomain().getDevDomainList())
			).toList();
		}
		
		if (moduleEntryList.isEmpty()) {
			return null;
		}
		
		if (moduleEntryList.size() == 1) {
			return moduleEntryList.get(0);
		}
		
		
		/**
		 * 2개 이상 매칭되는 경우 requestPath가 더 긴 경우를 우선함
		 */
		Comparator<Entry<String, CoreProperties>> comparator = (Entry<String, CoreProperties> o1, Entry<String, CoreProperties> o2) -> {
			var path1 = o1.getValue().getDomain().getPath();
			var path2 = o2.getValue().getDomain().getPath();
			if (path1 == null) {
				return 1;
			}
			if (path2 != null) {
				if (path1.getRequestPath().length() > path2.getRequestPath().length()) {
					return 1;
				} else if (path1.getRequestPath().length() == path2.getRequestPath().length()) {
					return 0;
				} else {
					return -1;
				}
			}
			return 0;
		};
		
		var moduleEntry = moduleEntryList.stream().sorted(comparator.reversed()).findFirst();
		if (moduleEntry.isPresent()) {
			return moduleEntry.get();
		}
		return null;
	}
	
	private static boolean checkDomain(HttpServletRequest request, List<URI> uriList) {
		if (request == null) {
			return false;
		}
		return uriList.stream().anyMatch(uri -> uri.getHost().equals(request.getServerName()));
	}
	
	private static boolean checkDomainWithPath(HttpServletRequest request, List<URI> uriList) {
		if (request == null) {
			return false;
		}
		
		return uriList.stream().anyMatch(uri -> uri.getHost().equals(request.getServerName()) && (
			request.getServletPath().startsWith(uri.getPath())
			|| (request.getServletPath().length() + 1 == uri.getPath().length() && (request.getServletPath() + "/").equals(uri.getPath())) // request sevletPath가 설정된 path보다 length 가 1 짧은 경우 / 를 추가하여 동일 여부 체크
		));
	}
	
	private static Entry<String, CoreProperties> getModuleEntryByModuleNameResolver(CoreModuleProperties coreModuleProperties) {
		var moduleNameResolver = ApplicationContextUtil.getApplicationContext().getBean(ModuleNameResolver.class);
		return coreModuleProperties.getModules().entrySet().stream().filter(moduleEntry -> moduleEntry.getKey().equals(moduleNameResolver.resolve())).findAny().orElse(null);
	}

	
	/**
	 * url에 등록된 pathVariable을 조회하는 기능
	 * 일반적으로 interceptor 이후엔 requestAttribute에서 직접 꺼내 사용하면 되며
	 * requestAttribute 값이 설정되기 이전인 filter에서 pathVariable을 사용할 수 있도록 제공함
	 * @return
	 */
	public static Map<String, String> getUriVariableMap() {
		var applicationContext = ApplicationContextUtil.getApplicationContext();
		RequestMappingHandlerMapping mapping = applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);

		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			
			HandlerExecutionChain handler = mapping.getHandler(request);
			if(handler == null) return null;
			var handlerMethod = (HandlerMethod) handler.getHandler();
			Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
			
			RequestMappingInfo requestMappingInfo = null;
			for(var entry : handlerMethods.entrySet()) {
				if (entry.getValue().getMethod() == handlerMethod.getMethod()) {
					requestMappingInfo = entry.getKey();
				}
			}
			PathContainer path = ServletRequestPathUtils.getParsedRequestPath(request).pathWithinApplication();
			PathPattern pathPattern = requestMappingInfo.getPathPatternsCondition().getFirstPattern();
			PathMatchInfo matchAndExtract = pathPattern.matchAndExtract(path);
			return matchAndExtract.getUriVariables();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
