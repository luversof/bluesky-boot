package net.luversof.boot.util;

import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.reactive.ServerWebExchangeContextFilter;
import org.springframework.web.server.ServerWebExchange;

import net.luversof.boot.config.BlueskyCoreModuleProperties;
import net.luversof.boot.config.BlueskyCoreProperties;
import net.luversof.boot.config.BlueskyCoreProperties.CoreModulePropertiesResolveType;
import net.luversof.boot.support.ModuleNameResolver;
import reactor.core.publisher.Mono;

public class ServerWebExchangeUtil {
	
	private static final PathMatcher pathMatcher = new AntPathMatcher();
	private static final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	
	public static final String EXCHANGE_CONTEXT_ATTRIBUTE = ServerWebExchangeContextFilter.class.getName()
			+ ".EXCHANGE_CONTEXT";

	public static Mono<ServerWebExchange> getServerWebExchange() {
		return Mono.subscriberContext().filter(c -> c.hasKey(EXCHANGE_CONTEXT_ATTRIBUTE)).flatMap(c -> {
			return Mono.just(c.get(EXCHANGE_CONTEXT_ATTRIBUTE));
		});
	}
	
	public static boolean isInternalRequest(ServerWebExchange exchange) {
		String serverName = exchange.getRequest().getHeaders().getHost().getHostName();
		if (serverName.equals("localhost")) {
			return true;
		}
		return serverName.matches(ipv4Pattern);
	}
	
	public static <T extends BlueskyCoreModuleProperties> Entry<String, T> getModulePropertiesEntry(ServerWebExchange exchange) {
		@SuppressWarnings("unchecked")
		BlueskyCoreProperties<T> coreProperties = exchange.getApplicationContext().getBean(BlueskyCoreProperties.class);
		Assert.notEmpty(coreProperties.getModules(), "coreProperties is not set");
		
		if (coreProperties.getModules().size() == 1) {
			return coreProperties.getModules().entrySet().stream().findAny().get();
		}
		
		var resolveType = coreProperties.getResolveType();
		
		Entry<String, T> module;
		if (coreProperties.getModules().size() > 1 && isInternalRequest(exchange)) {
			module = coreProperties.getModules().entrySet().stream().findFirst().get();
		} else if (CoreModulePropertiesResolveType.ADD_PATH_PATTERN == resolveType) {
			module = getModuleEntryByAddPathPattern(exchange);
		} else if (CoreModulePropertiesResolveType.DOMAIN == resolveType) {
			module = getModuleEntryByDomain(exchange);
		} else {
			module = getModuleEntryByModuleNameResolver(exchange);
		}
		
		if (module == null) {
			module= coreProperties.getModules().entrySet().stream().findFirst().get();
		}
		return module;
	}
	
	private static <T extends BlueskyCoreModuleProperties> Entry<String, T> getModuleEntryByAddPathPattern(ServerWebExchange exchange) {
		@SuppressWarnings("unchecked")
		BlueskyCoreProperties<T> coreProperties = exchange.getApplicationContext().getBean(BlueskyCoreProperties.class);
		return coreProperties.getModules().entrySet().stream().filter(moduleEntry -> Arrays.asList(moduleEntry.getValue().getAddPathPatterns()).stream().anyMatch(addPathPattern -> pathMatcher.match(addPathPattern, exchange.getRequest().getURI().getPath()))).findAny().orElse(null);
	}
	
	private static <T extends BlueskyCoreModuleProperties> Entry<String, T> getModuleEntryByDomain(ServerWebExchange exchange) {
		@SuppressWarnings("unchecked")
		BlueskyCoreProperties<T> coreProperties = exchange.getApplicationContext().getBean(BlueskyCoreProperties.class);
		// 해당 도메인에 해당하는 모듈 entry list 확인
		List<Entry<String, T>> moduleEntryList = coreProperties.getModules().entrySet().stream().filter(moduleEntry ->
			moduleEntry.getValue().getDomain() != null && (
				checkDomain(exchange, moduleEntry.getValue().getDomain().getWebList())
				|| checkDomain(exchange, moduleEntry.getValue().getDomain().getMobileWebList())
				|| checkDomain(exchange, moduleEntry.getValue().getDomain().getDevDomainList())
			)
		).collect(Collectors.toList());
		
		if (moduleEntryList.isEmpty()) {
			return null;
		}
		
		// 대상 entry list가 2개 이상인 경우 path 까지 체크
		if (moduleEntryList.size() > 1) {
			moduleEntryList = moduleEntryList.stream().filter(moduleEntry ->
				checkDomainWithPath(exchange, moduleEntry.getValue().getDomain().getWebList())
				|| checkDomainWithPath(exchange, moduleEntry.getValue().getDomain().getMobileWebList())
				|| checkDomainWithPath(exchange, moduleEntry.getValue().getDomain().getDevDomainList())
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
		
		return moduleEntryList.stream().sorted(comparator.reversed()).findFirst().get();
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
	
	private static <T extends BlueskyCoreModuleProperties> Entry<String, T> getModuleEntryByModuleNameResolver(ServerWebExchange exchange) {
		var moduleNameResolver = exchange.getApplicationContext().getBean(ModuleNameResolver.class);
		@SuppressWarnings("unchecked")
		BlueskyCoreProperties<T> coreProperties = exchange.getApplicationContext().getBean(BlueskyCoreProperties.class);
		return coreProperties.getModules().entrySet().stream().filter(moduleEntry -> moduleEntry.getKey().equals(moduleNameResolver.resolve())).findAny().orElse(null);
	}
}
