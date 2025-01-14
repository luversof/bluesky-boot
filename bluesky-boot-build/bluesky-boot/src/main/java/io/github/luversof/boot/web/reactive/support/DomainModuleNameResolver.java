package io.github.luversof.boot.web.reactive.support;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;

import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;

/**
 * Resolver that resolves moduleName based on domain
 */
public class DomainModuleNameResolver extends AbstractModuleNameResolver {
	
	/**
	 * 2개 이상 매칭되는 경우 requestPath가 더 긴 경우를 우선함
	 */
	private final Comparator<Entry<String, DomainProperties>> comparator = (Entry<String, DomainProperties> o1, Entry<String, DomainProperties> o2) -> {
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
	
	@Override
	protected Entry<String, DomainProperties> getModulePropertiesEntry(ServerWebExchange exchange, DomainModuleProperties domainModuleProperties) {
		var applicationContext = exchange.getApplicationContext();
		Assert.notNull(applicationContext, ERROR_MESSAGE_NOT_NULL_APPLICATION_CONTEXT);
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
		
		return moduleEntryList.stream().sorted(comparator.reversed()).findFirst().orElseGet(() -> null);
	}

	private boolean checkDomain(ServerWebExchange exchange, DomainProperties domainProperties) {
		return domainProperties != null && (
				checkDomain(exchange, domainProperties.getWebList())
				|| checkDomain(exchange, domainProperties.getMobileWebList())
				|| checkDomain(exchange, domainProperties.getDevDomainList())
				);
	}
	
	private boolean checkDomain(ServerWebExchange exchange, List<URI> uriList) {
		if (exchange == null) {
			return false;
		}
		return uriList.stream().anyMatch(uri -> uri.getHost().equals(exchange.getRequest().getHeaders().getHost().getHostName()));
	}
	
	private boolean checkDomainWithPath(ServerWebExchange exchange, DomainProperties domainProperties) {
		return domainProperties != null && (
				checkDomainWithPath(exchange, domainProperties.getWebList())
				|| checkDomainWithPath(exchange, domainProperties.getMobileWebList())
				|| checkDomainWithPath(exchange, domainProperties.getDevDomainList())
				);
	}
	
	private boolean checkDomainWithPath(ServerWebExchange exchange, List<URI> uriList) {
		if (exchange == null) {
			return false;
		}
		
		return uriList.stream().anyMatch(uri -> uri.getHost().equals(exchange.getRequest().getHeaders().getHost().getHostName()) && (
				exchange.getRequest().getURI().getPath().startsWith(uri.getPath())
			|| (exchange.getRequest().getURI().getPath().length() + 1 == uri.getPath().length() && (exchange.getRequest().getURI().getPath() + "/").equals(uri.getPath())) // request sevletPath가 설정된 path보다 length 가 1 짧은 경우 / 를 추가하여 동일 여부 체크
		));
	}
}
