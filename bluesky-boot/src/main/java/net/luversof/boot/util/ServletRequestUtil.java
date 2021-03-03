package net.luversof.boot.util;

import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.luversof.boot.config.BlueskyCoreModuleProperties;
import net.luversof.boot.config.BlueskyCoreProperties;
import net.luversof.boot.config.BlueskyCoreProperties.CoreModulePropertiesResolveType;
import net.luversof.boot.support.ModuleNameResolver;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServletRequestUtil {
	
	private static final PathMatcher pathMatcher = new AntPathMatcher();
	private static final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

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
		return serverName.matches(ipv4Pattern);
	}
	
	/**
	 * 현재 요청에 대한 액티브 모듈 조회
	 * @param request
	 * @return
	 */
	public static <T extends BlueskyCoreModuleProperties> Entry<String, T> getModulePropertiesEntry(HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		BlueskyCoreProperties<T> coreProperties = ApplicationContextUtil.getApplicationContext().getBean(BlueskyCoreProperties.class);
		Assert.notEmpty(coreProperties.getModules(), "coreProperties is not set");
		
		var modules = coreProperties.getModules();
		if (modules.size() == 1) {
			return modules.entrySet().stream().findAny().orElse(null);
		}
		
		var resolveType = coreProperties.getResolveType();
		
		Entry<String, T> module;
		// 내부 접근인 경우 임의 coreModuleProperties로 처리함
		if (CoreModulePropertiesResolveType.ADD_PATH_PATTERN == resolveType) {
			module = getModuleEntryByAddPathPattern(request, coreProperties);
		} else if (CoreModulePropertiesResolveType.MODULE_NAME_RESOLVER == resolveType) {
			module = getModuleEntryByModuleNameResolver(coreProperties);
		} else if(modules.size() > 1 && isInternalRequest(request)) { // 내부 접근의 별도 resolveType 설정이 없는 멀티 모듈의 경우 임의 coreModuleProperties로 처리함
			module = modules.entrySet().stream().findFirst().orElse(null);
		} else {
			module = getModuleEntryByDomain(request, coreProperties);
		}
		
		if (module == null) {
			module= modules.entrySet().stream().findFirst().orElse(null);
		}
		return module;
	}
	
	private static <T extends BlueskyCoreModuleProperties> Entry<String, T> getModuleEntryByAddPathPattern(HttpServletRequest request, BlueskyCoreProperties<T> coreProperties) {
		return coreProperties.getModules().entrySet().stream().filter(moduleEntry -> Arrays.asList(moduleEntry.getValue().getAddPathPatterns()).stream().anyMatch(addPathPattern -> pathMatcher.match(addPathPattern, request.getServletPath()))).findAny().orElse(null);
	}
	
	private static <T extends BlueskyCoreModuleProperties> Entry<String, T> getModuleEntryByDomain(HttpServletRequest request, BlueskyCoreProperties<T> coreProperties) {
		// 해당 도메인에 해당하는 모듈 entry list 확인
		List<Entry<String, T>> moduleEntryList = coreProperties.getModules().entrySet().stream().filter(moduleEntry ->
			moduleEntry.getValue().getDomain() != null && (
				checkDomain(request, moduleEntry.getValue().getDomain().getWebList())
				|| checkDomain(request, moduleEntry.getValue().getDomain().getMobileWebList())
				|| checkDomain(request, moduleEntry.getValue().getDomain().getDevDomainList())
			)
		).collect(Collectors.toList());
		
		if (moduleEntryList.isEmpty()) {
			return null;
		}
		
		// 대상 entry list가 2개 이상인 경우 path 까지 체크
		if (moduleEntryList.size() > 1) {
			moduleEntryList = moduleEntryList.stream().filter(moduleEntry -> 
				checkDomainWithPath(request, moduleEntry.getValue().getDomain().getWebList())
				|| checkDomainWithPath(request, moduleEntry.getValue().getDomain().getMobileWebList())
				|| checkDomainWithPath(request, moduleEntry.getValue().getDomain().getDevDomainList())
			).collect(Collectors.toList());
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
		var comparator = new Comparator<Entry<String, T>>() {
			@Override
			public int compare(Entry<String, T> o1, Entry<String, T> o2) {
				var pathForward1 = o1.getValue().getDomain().getPathForward();
				var pathForward2 = o2.getValue().getDomain().getPathForward();
				if (pathForward1 == null) {
					return 1;
				}
				if (pathForward1 != null && pathForward2 != null) {
					if (pathForward1.getRequestPath().length() > pathForward2.getRequestPath().length()) {
						return 1;
					} else if (pathForward1.getRequestPath().length() == pathForward2.getRequestPath().length()) {
						return 0;
					} else {
						return -1;
					}
				}
				return 0;
			}
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
	
	private static <T extends BlueskyCoreModuleProperties> Entry<String, T> getModuleEntryByModuleNameResolver(BlueskyCoreProperties<T> coreProperties) {
		var moduleNameResolver = ApplicationContextUtil.getApplicationContext().getBean(ModuleNameResolver.class);
		return coreProperties.getModules().entrySet().stream().filter(moduleEntry -> moduleEntry.getKey().equals(moduleNameResolver.resolve())).findAny().orElse(null);
	}

}
