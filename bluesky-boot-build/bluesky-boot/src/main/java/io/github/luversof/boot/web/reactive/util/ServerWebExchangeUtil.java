package io.github.luversof.boot.web.reactive.util;

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
import io.github.luversof.boot.core.CoreResolveType;
import io.github.luversof.boot.support.ModuleNameResolver;
import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;
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
	

	/**
	 * 2개 이상 매칭되는 경우 requestPath가 더 긴 경우를 우선함
	 */
	private static final Comparator<Entry<String, DomainProperties>> CONPARATOR = (Entry<String, DomainProperties> o1, Entry<String, DomainProperties> o2) -> {
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
	
	public static Entry<String, DomainProperties> getModulePropertiesEntry(ServerWebExchange exchange) {
		var applicationContext = exchange.getApplicationContext();
		Assert.notNull(applicationContext, APPLICATION_CONTEXT_MUST_EXIST);
		DomainModuleProperties domainModuleProperties = applicationContext.getBean(DomainModuleProperties.class);
		CoreBaseProperties coreBaseProperties = applicationContext.getBean(CoreBaseProperties.class);
		Assert.notEmpty(domainModuleProperties.getModules(), "domainModuleProperties is not set");
		
		var modules = domainModuleProperties.getModules();
		if (modules.size() == 1) {
			return modules.entrySet().stream().findAny().orElse(null);
		}
		
		var resolveType = coreBaseProperties.getResolveType();
		
		Entry<String, DomainProperties> module;
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
	
	private static Entry<String, DomainProperties> getModuleEntryByAddPathPattern(ServerWebExchange exchange) {
		var applicationContext = exchange.getApplicationContext();
		Assert.notNull(applicationContext, APPLICATION_CONTEXT_MUST_EXIST);
		DomainModuleProperties domainModuleProperties = applicationContext.getBean(DomainModuleProperties.class);
		return domainModuleProperties.getModules().entrySet().stream().filter(moduleEntry -> Arrays.asList(moduleEntry.getValue().getAddPathPatterns()).stream().anyMatch(addPathPattern -> pathMatcher.match(addPathPattern, exchange.getRequest().getURI().getPath()))).findAny().orElse(null);
	}
	
	private static Entry<String, DomainProperties> getModuleEntryByDomain(ServerWebExchange exchange) {
		var applicationContext = exchange.getApplicationContext();
		Assert.notNull(applicationContext, APPLICATION_CONTEXT_MUST_EXIST);
		DomainModuleProperties domainModuleProperties = applicationContext.getBean(DomainModuleProperties.class);
		// 해당 도메인에 해당하는 모듈 entry list 확인
		List<Entry<String, DomainProperties>> moduleEntryList = domainModuleProperties.getModules().entrySet().stream().filter(moduleEntry -> checkDomain(exchange, moduleEntry.getValue())).toList();
		
		if (moduleEntryList.isEmpty()) {
			return null;
		}
		
		// 대상 entry list가 2개 이상인 경우 path 까지 체크
		if (moduleEntryList.size() > 1) {
			moduleEntryList = moduleEntryList.stream().filter(moduleEntry -> checkDomainWithPath(exchange, moduleEntry.getValue())).toList();
		}
		
		if (moduleEntryList.isEmpty()) {
			return null;
		}
		
		if (moduleEntryList.size() == 1) {
			return moduleEntryList.get(0);
		}
		
		return moduleEntryList.stream().sorted(CONPARATOR.reversed()).findFirst().orElseGet(() -> null);
	}
	
	private static boolean checkDomain(ServerWebExchange exchange, DomainProperties domainProperties) {
		return domainProperties != null && (
				checkDomain(exchange, domainProperties.getWebList())
				|| checkDomain(exchange, domainProperties.getMobileWebList())
				|| checkDomain(exchange, domainProperties.getDevDomainList())
				);
	}
	
	private static boolean checkDomain(ServerWebExchange exchange, List<URI> uriList) {
		if (exchange == null) {
			return false;
		}
		return uriList.stream().anyMatch(uri -> uri.getHost().equals(exchange.getRequest().getHeaders().getHost().getHostName()));
	}
	
	private static boolean checkDomainWithPath(ServerWebExchange exchange, DomainProperties domainProperties) {
		return domainProperties != null && (
				checkDomainWithPath(exchange, domainProperties.getWebList())
				|| checkDomainWithPath(exchange, domainProperties.getMobileWebList())
				|| checkDomainWithPath(exchange, domainProperties.getDevDomainList())
				);
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
	
	private static Entry<String, DomainProperties> getModuleEntryByModuleNameResolver(ServerWebExchange exchange) {
		var applicationContext = exchange.getApplicationContext();
		Assert.notNull(applicationContext, APPLICATION_CONTEXT_MUST_EXIST);
		var moduleNameResolver = applicationContext.getBean(ModuleNameResolver.class);
		DomainModuleProperties domainModuleProperties = applicationContext.getBean(DomainModuleProperties.class);
		return domainModuleProperties.getModules().entrySet().stream().filter(moduleEntry -> moduleEntry.getKey().equals(moduleNameResolver.resolve())).findAny().orElse(null);
	}
}
