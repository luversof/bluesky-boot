package net.luversof.boot.autoconfigure.core.filter;

import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import net.luversof.boot.autoconfigure.core.config.CoreProperties;
import net.luversof.boot.autoconfigure.core.config.CoreProperties.CoreModuleProperties;
import net.luversof.boot.autoconfigure.core.config.CoreProperties.CoreModulePropertiesResolveType;
import net.luversof.boot.autoconfigure.core.config.CoreProperties.PathForwardProperties;
import net.luversof.boot.autoconfigure.core.context.BlueskyContext;
import net.luversof.boot.autoconfigure.core.context.BlueskyContextImpl;
import net.luversof.boot.autoconfigure.core.context.ReactiveBlueskyContextHolder;
import net.luversof.boot.autoconfigure.core.support.ModuleNameResolver;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ReactiveBlueskyContextHolderFilter implements WebFilter {

	private static final PathMatcher pathMatcher = new AntPathMatcher();

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return chain.filter(exchange).subscriberContext(c -> c.hasKey(BlueskyContext.class) ? c : withBlueskyContext(c, exchange));
	}
	
	private Context withBlueskyContext(Context mainContext, ServerWebExchange exchange) {
		return mainContext.putAll(load(exchange).as(ReactiveBlueskyContextHolder::withBlueskyContext));
	}
	
	private Mono<BlueskyContext> load(ServerWebExchange exchange) {
		return exchange.getSession().flatMap(attrs -> {
			CoreProperties coreProperties = exchange.getApplicationContext().getBean(CoreProperties.class);
			Assert.notEmpty(coreProperties.getModules(), "coreProperties is not set");
			
			if (coreProperties.getModules().size() == 1) {
				return Mono.just(new BlueskyContextImpl(coreProperties.getModules().keySet().stream().findAny().get()));
			}
			
			CoreModulePropertiesResolveType resolveType = coreProperties.getResolveType();
			
			Entry<String, CoreModuleProperties> module;
			if (CoreModulePropertiesResolveType.ADD_PATH_PATTERN == resolveType) {
				module = getModuleEntryByAddPathPattern(exchange);
			} else if (CoreModulePropertiesResolveType.DOMAIN == resolveType) {
				module = getModuleEntryByDomain(exchange);
			} else {
				module = getModuleEntryByModuleNameResolver(exchange);
			}
			
			return Mono.just(new BlueskyContextImpl(module.getKey()));
		});
	}
	
	private static Entry<String, CoreModuleProperties> getModuleEntryByAddPathPattern(ServerWebExchange exchange) {
		CoreProperties coreProperties = exchange.getApplicationContext().getBean(CoreProperties.class);
		return coreProperties.getModules().entrySet().stream().filter(moduleEntry -> Arrays.asList(moduleEntry.getValue().getAddPathPatterns()).stream().anyMatch(addPathPattern -> pathMatcher.match(addPathPattern, exchange.getRequest().getURI().getPath()))).findAny().orElse(null);
	}
	
	private static Entry<String, CoreModuleProperties> getModuleEntryByDomain(ServerWebExchange exchange) {
		CoreProperties coreProperties = exchange.getApplicationContext().getBean(CoreProperties.class);
		// 해당 도메인에 해당하는 모듈 entry list 확인
		List<Entry<String, CoreModuleProperties>> moduleEntryList = coreProperties.getModules().entrySet().stream().filter(moduleEntry ->
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
		
		if (moduleEntryList.size() == 1) {
			return moduleEntryList.get(0);
		}
		
		/**
		 * 2개 이상 매칭되는 경우 requestPath가 더 긴 경우를 우선함
		 */
		Comparator<Entry<String, CoreModuleProperties>> comparator = new Comparator<Entry<String, CoreModuleProperties>>() {
			@Override
			public int compare(Entry<String, CoreModuleProperties> o1, Entry<String, CoreModuleProperties> o2) {
				PathForwardProperties pathForward1 = o1.getValue().getDomain().getPathForward();
				PathForwardProperties pathForward2 = o2.getValue().getDomain().getPathForward();
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
		return uriList.stream().anyMatch(uri -> uri.getHost().equals(exchange.getRequest().getHeaders().getHost().getHostName()));
	}
	
	private static boolean checkDomainWithPath(ServerWebExchange exchange, List<URI> uriList) {
		
		return uriList.stream().anyMatch(uri -> uri.getHost().equals(exchange.getRequest().getHeaders().getHost().getHostName()) && (
				exchange.getRequest().getURI().getPath().startsWith(uri.getPath())
			|| (exchange.getRequest().getURI().getPath().length() + 1 == uri.getPath().length() && (exchange.getRequest().getURI().getPath() + "/").equals(uri.getPath())) // request sevletPath가 설정된 path보다 length 가 1 짧은 경우 / 를 추가하여 동일 여부 체크
		));
	}
	
	public static Entry<String, CoreModuleProperties> getModuleEntryByModuleNameResolver(ServerWebExchange exchange) {
		ModuleNameResolver moduleNameResolver = exchange.getApplicationContext().getBean(ModuleNameResolver.class);
		CoreProperties coreProperties = exchange.getApplicationContext().getBean(CoreProperties.class);
		return coreProperties.getModules().entrySet().stream().filter(moduleEntry -> moduleEntry.getKey().equals(moduleNameResolver.resolve())).findAny().orElse(null);
	}
}
