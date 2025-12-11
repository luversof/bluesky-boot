package io.github.luversof.boot.web.servlet.support;

import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;
import jakarta.servlet.http.HttpServletRequest;

/**
 * A resolver that resolves moduleName based on a combination of Domain and
 * AddPathPattern.
 */
public class DomainAddPathPatternModuleNameResolver extends AbstractModuleNameResolver {

	private final PathMatcher pathMatcher = new AntPathMatcher();
	private final Comparator<Entry<String, DomainProperties>> addPathPatternComparator = (o1, o2) -> Integer.compare(
			o1.getValue().getAddPathPatternList().get(0).length(),
			o2.getValue().getAddPathPatternList().get(0).length());

	/**
	 * 도메인을 먼저 체크한 후 addPathPattern을 체크
	 * 그 반대의 경우로 쓰는 경우 현재 구현 상 domain은 체크하지 않고 addPathPattern만 체크함
	 * 이 부분은 추후 필요에 따라 개선 검토
	 */
	private boolean isDomainFirst = true;

	public DomainAddPathPatternModuleNameResolver() {
	}

	public DomainAddPathPatternModuleNameResolver(boolean isDomainFirst) {
		this.isDomainFirst = isDomainFirst;
	}

	@Override
	protected Entry<String, DomainProperties> getModulePropertiesEntry(HttpServletRequest request,
			DomainModuleProperties domainModuleProperties) {
		// 해당 도메인에 해당하는 모듈 entry list 확인

		var moduleEntryList = List.copyOf(domainModuleProperties.getModules().entrySet());

		moduleEntryList = isDomainFirst ? getEntryListFilterByDomain(request, moduleEntryList)
				: getEntryListFilterByAddPathPattern(request, moduleEntryList);

		if (moduleEntryList.isEmpty()) {
			return null;
		}

		// 대상 entry List가 2개 이상인 경우 다음 체크
		if (moduleEntryList.size() > 1) {
			moduleEntryList = !isDomainFirst ? getEntryListFilterByDomain(request, moduleEntryList)
					: getEntryListFilterByAddPathPattern(request, moduleEntryList);
		}

		if (moduleEntryList.isEmpty()) {
			return null;
		}

		if (moduleEntryList.size() == 1) {
			return moduleEntryList.get(0);
		}
		// addPathPattern은 단일 또는 빈 목록을 반환하므로 2개 이상이 있을 가능성은 없음
		return null;
	}

	private List<Entry<String, DomainProperties>> getEntryListFilterByDomain(HttpServletRequest request,
			List<Entry<String, DomainProperties>> moduleEntryList) {
		return moduleEntryList.stream().filter(moduleEntry -> checkDomain(request, moduleEntry.getValue())).toList();
	}

	private List<Entry<String, DomainProperties>> getEntryListFilterByAddPathPattern(HttpServletRequest request,
			List<Entry<String, DomainProperties>> moduleEntryList) {
		var moduleEntry = moduleEntryList.stream().filter(
				entry -> entry.getValue().getAddPathPatternList().stream()
						.anyMatch(addPathPattern -> pathMatcher.match(addPathPattern, request.getServletPath())))
				.sorted(addPathPatternComparator.reversed()).findFirst().orElse(null);
		return moduleEntry == null ? Collections.emptyList() : List.of(moduleEntry);
	}

	private boolean checkDomain(HttpServletRequest request, DomainProperties domainProperties) {
		if (request == null) {
			return false;
		}

		return domainProperties != null && (checkDomain(request, domainProperties.getWebList())
				|| checkDomain(request, domainProperties.getMobileWebList())
				|| checkDomain(request, domainProperties.getDevDomainList()));
	}

	private boolean checkDomain(HttpServletRequest request, List<URI> uriList) {
		if (request == null) {
			return false;
		}
		return uriList.stream().anyMatch(uri -> uri.getHost().equals(request.getServerName()));
	}

}
