package io.github.luversof.boot.util;

import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.reactive.ServerWebExchangeContextFilter;
import org.springframework.web.server.ServerWebExchange;

import io.github.luversof.boot.core.CoreBaseProperties;
import io.github.luversof.boot.core.CoreModuleProperties;
import io.github.luversof.boot.core.CoreProperties;
import io.github.luversof.boot.core.CoreResolveType;
import io.github.luversof.boot.support.ModuleNameResolver;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerWebExchangeUtil {
	
	private static final String APPLICATION_CONTEXT_MUST_EXIST = "ApplicationContext must exist";
	private static final PathMatcher pathMatcher = new AntPathMatcher();
	private static final String IPV4_PATTERN = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	
	public static final String EXCHANGE_CONTEXT_ATTRIBUTE = ServerWebExchangeContextFilter.class.getName()	+ ".EXCHANGE_CONTEXT";

	public static Mono<ServerWebExchange> getServerWebExchange() {
		return Mono.deferContextual(Mono::just)
				.cast(Context.class)
				.filter(c -> c.hasKey(EXCHANGE_CONTEXT_ATTRIBUTE))
				.flatMap(c -> Mono.just(c.get(EXCHANGE_CONTEXT_ATTRIBUTE)));
	}
	
	public static boolean isInternalRequest(ServerWebExchange exchange) {
		var headers = exchange.getRequest().getHeaders();
		Assert.notNull(headers, "header value must exist");
		var host = headers.getHost();
		Assert.notNull(host, "header host value must exist");
		var hostName = host.getHostName();
		if (hostName.equals("localhost")) {
			return true;
		}
		return hostName.matches(IPV4_PATTERN);
	}
	
	public static Entry<String, CoreProperties> getModulePropertiesEntry(ServerWebExchange exchange) {
		var applicationContext = exchange.getApplicationContext();
		Assert.notNull(applicationContext, APPLICATION_CONTEXT_MUST_EXIST);
		CoreModuleProperties coreModuleProperties = applicationContext.getBean(CoreModuleProperties.class);
		CoreBaseProperties coreBaseProperties = applicationContext.getBean(CoreBaseProperties.class);
		Assert.notEmpty(coreModuleProperties.getModules(), "coreProperties is not set");
		
		var modules = coreModuleProperties.getModules();
		if (modules.size() == 1) {
			return modules.entrySet().stream().findAny().orElse(null);
		}
		
		var resolveType = coreBaseProperties.getResolveType();
		
		Entry<String, CoreProperties> module;
		if (modules.size() > 1 && isInternalRequest(exchange)) {
			module = modules.entrySet().stream().findFirst().orElse(null);
		} else if (CoreResolveType.ADD_PATH_PATTERN == resolveType) {
			module = getModuleEntryByAddPathPattern(exchange);
		} else if (CoreResolveType.DOMAIN == resolveType) {
			module = getModuleEntryByDomain(exchange);
		} else {
			module = getModuleEntryByModuleNameResolver(exchange);
		}
		
		if (module == null) {
			module= modules.entrySet().stream().findFirst().orElse(null);
		}
		return module;
	}
	
	private static Entry<String, CoreProperties> getModuleEntryByAddPathPattern(ServerWebExchange exchange) {
		var applicationContext = exchange.getApplicationContext();
		Assert.notNull(applicationContext, APPLICATION_CONTEXT_MUST_EXIST);
		CoreModuleProperties coreModuleProperties = applicationContext.getBean(CoreModuleProperties.class);
		return coreModuleProperties.getModules().entrySet().stream().filter(moduleEntry -> Arrays.asList(moduleEntry.getValue().getAddPathPatterns()).stream().anyMatch(addPathPattern -> pathMatcher.match(addPathPattern, exchange.getRequest().getURI().getPath()))).findAny().orElse(null);
	}
	
	private static Entry<String, CoreProperties> getModuleEntryByDomain(ServerWebExchange exchange) {
		var applicationContext = exchange.getApplicationContext();
		Assert.notNull(applicationContext, APPLICATION_CONTEXT_MUST_EXIST);
		CoreModuleProperties coreModuleProperties = applicationContext.getBean(CoreModuleProperties.class);
		// 해당 도메인에 해당하는 모듈 entry list 확인
		List<Entry<String, CoreProperties>> moduleEntryList = coreModuleProperties.getModules().entrySet().stream().filter(moduleEntry ->
			moduleEntry.getValue().getDomain() != null && (
				checkDomain(exchange, moduleEntry.getValue().getDomain().getWebList())
				|| checkDomain(exchange, moduleEntry.getValue().getDomain().getMobileWebList())
				|| checkDomain(exchange, moduleEntry.getValue().getDomain().getDevDomainList())
			)
		).toList();
		
		if (moduleEntryList.isEmpty()) {
			return null;
		}
		
		// 대상 entry list가 2개 이상인 경우 path 까지 체크
		if (moduleEntryList.size() > 1) {
			moduleEntryList = moduleEntryList.stream().filter(moduleEntry ->
				checkDomainWithPath(exchange, moduleEntry.getValue().getDomain().getWebList())
				|| checkDomainWithPath(exchange, moduleEntry.getValue().getDomain().getMobileWebList())
				|| checkDomainWithPath(exchange, moduleEntry.getValue().getDomain().getDevDomainList())
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
	
	private static boolean checkDomain(ServerWebExchange exchange, List<URI> uriList) {
		if (exchange == null) {
			return false;
		}
		return uriList.stream().anyMatch(uri -> uri.getHost().equals(exchange.getRequest().getHeaders().getHost().getHostName()));
	}
	
	private static boolean checkDomainWithPath(ServerWebExchange exchange, List<URI> uriList) {
		if (exchange == null) {
			return false;
		}
		
		return uriList.stream().anyMatch(uri -> uri.getHost().equals(exchange.getRequest().getHeaders().getHost().getHostName()) && (
				exchange.getRequest().getURI().getPath().startsWith(uri.getPath())
			|| (exchange.getRequest().getURI().getPath().length() + 1 == uri.getPath().length() && (exchange.getRequest().getURI().getPath() + "/").equals(uri.getPath())) // request sevletPath가 설정된 path보다 length 가 1 짧은 경우 / 를 추가하여 동일 여부 체크
		));
	}
	
	private static Entry<String, CoreProperties> getModuleEntryByModuleNameResolver(ServerWebExchange exchange) {
		var applicationContext = exchange.getApplicationContext();
		Assert.notNull(applicationContext, APPLICATION_CONTEXT_MUST_EXIST);
		var moduleNameResolver = applicationContext.getBean(ModuleNameResolver.class);
		CoreModuleProperties coreModuleProperties = applicationContext.getBean(CoreModuleProperties.class);
		return coreModuleProperties.getModules().entrySet().stream().filter(moduleEntry -> moduleEntry.getKey().equals(moduleNameResolver.resolve())).findAny().orElse(null);
	}
}
