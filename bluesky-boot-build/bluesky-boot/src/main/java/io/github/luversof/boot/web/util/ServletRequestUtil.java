package io.github.luversof.boot.web.util;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.PathContainer;
import org.springframework.util.AntPathMatcher;
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

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.core.CoreBaseProperties;
import io.github.luversof.boot.core.CoreResolveType;
import io.github.luversof.boot.support.ModuleNameResolver;
import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServletRequestUtil {
	
	private static final PathMatcher pathMatcher = new AntPathMatcher();
	private static final String IPV4_PATTERN = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	
	/**
	 * 2개 이상 매칭되는 경우 requestPath가 더 긴 경우를 우선함
	 */
	private static final Comparator<Entry<String, DomainProperties>> COMPARATOR = (Entry<String, DomainProperties> o1, Entry<String, DomainProperties> o2) -> {
		var path1 = o1.getValue().getRequestPath();
		var path2 = o2.getValue().getRequestPath();
		if (path1 == null) {
			return 1;
		}
		if (path2 != null) {
			if (path1.length() > path2.length()) {
				return 1;
			} else if (path1.length() == path2.length()) {
				return 0;
			} else {
				return -1;
			}
		}
		return 0;
	};

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
	public static Entry<String, DomainProperties> getModulePropertiesEntry(HttpServletRequest request) {
		DomainModuleProperties domainModuleProperties = ApplicationContextUtil.getApplicationContext().getBean(DomainModuleProperties.class);
		CoreBaseProperties coreBaseProperties = ApplicationContextUtil.getApplicationContext().getBean(CoreBaseProperties.class);
		
		var modules = domainModuleProperties.getModules();
		if (modules.isEmpty()) {
			return null;
		} else if (modules.size() == 1) {
			return modules.entrySet().stream().findAny().orElse(null);
		}
		
		var resolveType = coreBaseProperties.getResolveType();
		
		Entry<String, DomainProperties> module;
		// 내부 접근인 경우 임의 coreModuleProperties로 처리함
		if (CoreResolveType.ADD_PATH_PATTERN == resolveType) {
			module = getModuleEntryByAddPathPattern(request, domainModuleProperties);
		} else if (CoreResolveType.MODULE_NAME_RESOLVER == resolveType) {
			module = getModuleEntryByModuleNameResolver(domainModuleProperties);
		} else if(modules.size() > 1 && isInternalRequest(request)) { // 내부 접근의 별도 resolveType 설정이 없는 멀티 모듈의 경우 임의 coreModuleProperties로 처리함
			module = modules.entrySet().stream().findFirst().orElse(null);
		} else {
			module = getModuleEntryByDomain(request, domainModuleProperties);
		}
		
		if (module == null) {
			module= modules.entrySet().stream().findFirst().orElse(null);
		}
		return module;
	}
	
	private static Entry<String, DomainProperties> getModuleEntryByAddPathPattern(HttpServletRequest request, DomainModuleProperties domainModuleProperties) {
		return domainModuleProperties.getModules().entrySet().stream().filter(moduleEntry -> Arrays.asList(moduleEntry.getValue().getAddPathPatterns()).stream().anyMatch(addPathPattern -> pathMatcher.match(addPathPattern, request.getServletPath()))).findAny().orElse(null);
	}
	
	private static Entry<String, DomainProperties> getModuleEntryByDomain(HttpServletRequest request, DomainModuleProperties domainModuleProperties) {
		// 해당 도메인에 해당하는 모듈 entry list 확인
		List<Entry<String, DomainProperties>> moduleEntryList = domainModuleProperties.getModules().entrySet().stream().filter(moduleEntry -> checkDomain(request, moduleEntry.getValue())).toList();
		
		if (moduleEntryList.isEmpty()) {
			return null;
		}
		
		// 대상 entry list가 2개 이상인 경우 path 까지 체크
		if (moduleEntryList.size() > 1) {
			moduleEntryList = moduleEntryList.stream().filter(moduleEntry -> checkDomainWithPath(request, moduleEntry.getValue())).toList();
		}
		
		if (moduleEntryList.isEmpty()) {
			return null;
		}
		
		if (moduleEntryList.size() == 1) {
			return moduleEntryList.get(0);
		}
		
		var moduleEntry = moduleEntryList.stream().sorted(COMPARATOR.reversed()).findFirst();
		if (moduleEntry.isPresent()) {
			return moduleEntry.get();
		}
		return null;
	}
	
	private static boolean checkDomain(HttpServletRequest request, DomainProperties domainProperties) {
		if (request == null) {
			return false;
		}
		
		return domainProperties != null && (
				checkDomain(request, domainProperties.getWebList())
				|| checkDomain(request, domainProperties.getMobileWebList())
				|| checkDomain(request, domainProperties.getDevDomainList()));
	}
	
	private static boolean checkDomain(HttpServletRequest request, List<URI> uriList) {
		if (request == null) {
			return false;
		}
		return uriList.stream().anyMatch(uri -> uri.getHost().equals(request.getServerName()));
	}
	
	private static boolean checkDomainWithPath(HttpServletRequest request, DomainProperties domainProperties) {
		if (request == null) {
			return false;
		}
		
		return domainProperties != null && (
				checkDomainWithPath(request, domainProperties.getWebList())
				|| checkDomainWithPath(request, domainProperties.getMobileWebList())
				|| checkDomainWithPath(request, domainProperties.getDevDomainList()));
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
	
	private static Entry<String, DomainProperties> getModuleEntryByModuleNameResolver(DomainModuleProperties domainModuleProperties) {
		var moduleNameResolver = ApplicationContextUtil.getApplicationContext().getBean(ModuleNameResolver.class);
		var targetModuleName = moduleNameResolver.resolve();
		return domainModuleProperties.getModules().entrySet().stream().filter(moduleEntry -> moduleEntry.getKey().equals(targetModuleName)).findAny().orElse(null);
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
			var requestAttributes = RequestContextHolder.getRequestAttributes();
			if (requestAttributes == null) {
				return Collections.emptyMap();
			}
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			
			HandlerExecutionChain handler = mapping.getHandler(request);
			if(handler == null) {
				return Collections.emptyMap();
			}
			
			var handlerMethod = (HandlerMethod) handler.getHandler();
			Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
			
			RequestMappingInfo requestMappingInfo = null;
			for(var entry : handlerMethods.entrySet()) {
				if (entry.getValue().getMethod() == handlerMethod.getMethod()) {
					requestMappingInfo = entry.getKey();
					break;
				}
			}
			
			if (requestMappingInfo == null) {
				return Collections.emptyMap();
			}
			
			var pathPatternsCondition = requestMappingInfo.getPathPatternsCondition();
			if (pathPatternsCondition == null) {
				return Collections.emptyMap();	
			}
			
			PathContainer path = ServletRequestPathUtils.getParsedRequestPath(request).pathWithinApplication();
			PathPattern pathPattern = pathPatternsCondition.getFirstPattern();
			PathMatchInfo matchAndExtract = pathPattern.matchAndExtract(path);
			if (matchAndExtract == null) {
				return Collections.emptyMap();
			}
			
			return matchAndExtract.getUriVariables();
		} catch (Exception e) {
			log.debug("getUriVariableMap exception", e);
		}
		return Collections.emptyMap();
	}

}
