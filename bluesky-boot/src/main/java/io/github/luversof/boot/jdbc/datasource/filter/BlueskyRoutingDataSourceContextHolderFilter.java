package io.github.luversof.boot.jdbc.datasource.filter;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.luversof.boot.jdbc.datasource.context.BlueskyRoutingDataSourceContextHolder;
import io.github.luversof.boot.jdbc.datasource.support.BlueskyRoutingDataSourceLookupKeyResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Order(-103)
public class BlueskyRoutingDataSourceContextHolderFilter extends OncePerRequestFilter {
	
	private BlueskyRoutingDataSourceLookupKeyResolver blueskyRoutingDataSourceLookupKeyResolver;
	
	public BlueskyRoutingDataSourceContextHolderFilter(@Nullable BlueskyRoutingDataSourceLookupKeyResolver blueskyRoutingDataSourceLookupKeyResolver) {
		this.blueskyRoutingDataSourceLookupKeyResolver = blueskyRoutingDataSourceLookupKeyResolver;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			if (blueskyRoutingDataSourceLookupKeyResolver != null) {
				BlueskyRoutingDataSourceContextHolder.setContext(() -> blueskyRoutingDataSourceLookupKeyResolver.getLookupKey());
			}
			filterChain.doFilter(request, response);
		} finally {
			BlueskyRoutingDataSourceContextHolder.clearContext();
		}
	}

}
