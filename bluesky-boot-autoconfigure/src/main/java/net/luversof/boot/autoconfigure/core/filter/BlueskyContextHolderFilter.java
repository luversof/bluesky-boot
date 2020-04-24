package net.luversof.boot.autoconfigure.core.filter;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import net.luversof.boot.autoconfigure.core.config.CoreProperties;
import net.luversof.boot.autoconfigure.core.config.CoreProperties.CoreModuleProperties;
import net.luversof.boot.autoconfigure.core.config.CoreProperties.CoreModulePropertiesResolveType;
import net.luversof.boot.autoconfigure.core.context.BlueskyBootErrorCode;
import net.luversof.boot.autoconfigure.core.context.BlueskyContextHolder;
import net.luversof.boot.autoconfigure.core.support.ModuleNameResolver;
import net.luversof.boot.autoconfigure.core.util.ApplicationContextUtil;
import net.luversof.boot.exception.BlueskyException;

public class BlueskyContextHolderFilter extends OncePerRequestFilter {
	
	private static final PathMatcher pathMatcher = new AntPathMatcher();
	
	/**
	 * 2개 이상 매칭되는 경우 requestPath가 더 긴 경우를 우선함
	 */
	private static Comparator<Entry<String, CoreModuleProperties>> comparator = (var o1, var o2) -> {
		
			var o1PathForward = o1.getValue().getDomain().getPathForward();
			var o2PathForward = o2.getValue().getDomain().getPathForward();
			if (o1PathForward == null) {
				return 1;
			}
			
			if (o2PathForward == null) {
				return 0;
			}
			
			var o1RequestPathLength = o1PathForward.getRequestPath().length();
			var o2RequestPathLength = o2PathForward.getRequestPath().length();
			if (o1RequestPathLength > o2RequestPathLength) {
				return 1;
			} else if (o1RequestPathLength == o2RequestPathLength) {
				return 0;
			} else {
				return -1;
			}
			
	};

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		var coreProperties = ApplicationContextUtil.getApplicationContext().getBean(CoreProperties.class);
		
		Assert.notEmpty(coreProperties.getModules(), "coreProperties is not set");
		
		if (coreProperties.getModules().size() == 1) {
			filterChain.doFilter(request, response);
			return;
		}
		
		var resolveType = coreProperties.getResolveType();
		
		Entry<String, CoreModuleProperties> module;
		if (CoreModulePropertiesResolveType.ADD_PATH_PATTERN == resolveType) {
			module = getModuleEntryByAddPathPattern(request, coreProperties);
		} else if (CoreModulePropertiesResolveType.DOMAIN == resolveType) {
			module = getModuleEntryByDomain(request, coreProperties);
		} else {
			module = getModuleEntryByModuleNameResolver(coreProperties);
		}
		
		if (module == null) {
			throw new BlueskyException(BlueskyBootErrorCode.NOT_EXIST_MODULE);
		}
		
		BlueskyContextHolder.setContext(module.getKey());
		filterChain.doFilter(request, response);
		BlueskyContextHolder.clearContext();
		
	}
	
	private static Entry<String, CoreModuleProperties> getModuleEntryByAddPathPattern(HttpServletRequest request, CoreProperties coreProperties) {
		return coreProperties.getModules().entrySet().stream().filter(moduleEntry -> Arrays.asList(moduleEntry.getValue().getAddPathPatterns()).stream().anyMatch(addPathPattern -> pathMatcher.match(addPathPattern, request.getServletPath()))).findAny().orElse(null);
	}
	
	private static Entry<String, CoreModuleProperties> getModuleEntryByDomain(HttpServletRequest request, CoreProperties coreProperties) {
		// 해당 도메인에 해당하는 모듈 entry list 확인
		var moduleEntryList = coreProperties.getModules().entrySet().stream().filter(moduleEntry ->
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
		
		if (moduleEntryList.size() == 1) {
			return moduleEntryList.get(0);
		}
		
		return moduleEntryList.stream().sorted(comparator.reversed()).findFirst().get();
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
	
	public static Entry<String, CoreModuleProperties> getModuleEntryByModuleNameResolver(CoreProperties coreProperties) {
		var moduleNameResolver = ApplicationContextUtil.getApplicationContext().getBean(ModuleNameResolver.class);
		return coreProperties.getModules().entrySet().stream().filter(moduleEntry -> moduleEntry.getKey().equals(moduleNameResolver.resolve())).findAny().orElse(null);
	}

}
