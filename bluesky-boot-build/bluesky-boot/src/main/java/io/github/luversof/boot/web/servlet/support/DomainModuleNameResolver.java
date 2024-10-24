package io.github.luversof.boot.web.servlet.support;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;
import jakarta.servlet.http.HttpServletRequest;

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
	protected Entry<String, DomainProperties> getModulePropertiesEntry(HttpServletRequest request, DomainModuleProperties domainModuleProperties) {
		// 해당 도메인에 해당하는 모듈 entry list 확인
		var moduleEntryList = domainModuleProperties.getModules().entrySet().stream().filter(moduleEntry -> checkDomain(request, moduleEntry.getValue())).toList();
		
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
		
		var moduleEntry = moduleEntryList.stream().sorted(comparator.reversed()).findFirst();
		if (moduleEntry.isPresent()) {
			return moduleEntry.get();
		}
		return null;
	}
	
	private boolean checkDomain(HttpServletRequest request, DomainProperties domainProperties) {
		if (request == null) {
			return false;
		}
		
		return domainProperties != null && (
				checkDomain(request, domainProperties.getWebList())
				|| checkDomain(request, domainProperties.getMobileWebList())
				|| checkDomain(request, domainProperties.getDevDomainList()));
	}
	
	private boolean checkDomain(HttpServletRequest request, List<URI> uriList) {
		if (request == null) {
			return false;
		}
		return uriList.stream().anyMatch(uri -> uri.getHost().equals(request.getServerName()));
	}
	
	private boolean checkDomainWithPath(HttpServletRequest request, DomainProperties domainProperties) {
		if (request == null) {
			return false;
		}
		
		return domainProperties != null && (
				checkDomainWithPath(request, domainProperties.getWebList())
				|| checkDomainWithPath(request, domainProperties.getMobileWebList())
				|| checkDomainWithPath(request, domainProperties.getDevDomainList()));
	}
	
	private boolean checkDomainWithPath(HttpServletRequest request, List<URI> uriList) {
		if (request == null) {
			return false;
		}
		
		return uriList.stream().anyMatch(uri -> uri.getHost().equals(request.getServerName()) && (
			request.getServletPath().startsWith(uri.getPath())
			|| (request.getServletPath().length() + 1 == uri.getPath().length() && (request.getServletPath() + "/").equals(uri.getPath())) // request sevletPath가 설정된 path보다 length 가 1 짧은 경우 / 를 추가하여 동일 여부 체크
		));
	}

}
