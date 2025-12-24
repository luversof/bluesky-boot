package io.github.luversof.boot.web.service.invoker;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.service.invoker.HttpRequestValues;
import org.springframework.web.service.invoker.HttpServiceArgumentResolver;

public class PageableHttpServiceArgumentResolver implements HttpServiceArgumentResolver {

	@Override
	public boolean resolve(Object argument, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
		if (argument instanceof Pageable pageable) {
			if (pageable.isPaged()) {
				requestValues.addRequestParameter("page", String.valueOf(pageable.getPageNumber()));
				requestValues.addRequestParameter("size", String.valueOf(pageable.getPageSize()));
			}
			if (pageable.getSort().isSorted()) {
				for (Sort.Order order : pageable.getSort()) {
					requestValues.addRequestParameter("sort",
							order.getProperty() + "," + order.getDirection().name().toLowerCase());
				}
			}
			return true;
		}
		return false;
	}

}
