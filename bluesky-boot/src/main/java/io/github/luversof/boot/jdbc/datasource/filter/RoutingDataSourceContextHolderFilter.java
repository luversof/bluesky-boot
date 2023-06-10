package io.github.luversof.boot.jdbc.datasource.filter;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.luversof.boot.jdbc.datasource.context.RoutingDataSourceContextHolder;
import io.github.luversof.boot.jdbc.datasource.support.RoutingDataSourceLookupKeyResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Order(-103)
public class RoutingDataSourceContextHolderFilter extends OncePerRequestFilter {
	
	private RoutingDataSourceLookupKeyResolver routingDataSourceLookupKeyResolver;
	
	public RoutingDataSourceContextHolderFilter(@Nullable RoutingDataSourceLookupKeyResolver routingDataSourceLookupKeyResolver) {
		this.routingDataSourceLookupKeyResolver = routingDataSourceLookupKeyResolver;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			if (routingDataSourceLookupKeyResolver != null) {
				RoutingDataSourceContextHolder.setContext(() -> routingDataSourceLookupKeyResolver.getLookupKey());
			}
			filterChain.doFilter(request, response);
		} finally {
			RoutingDataSourceContextHolder.clearContext();
		}
	}

}
