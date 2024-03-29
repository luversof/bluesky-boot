package io.github.luversof.boot.filter;

import java.io.IOException;
import java.util.Map.Entry;

import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.core.BlueskyCoreModuleProperties;
import io.github.luversof.boot.util.ServletRequestUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * single module 이 아닌 경우 요청에 대해 moduleName을 ContextHolder에 설정
 * OrderedRequestContextFilter보다 후순위로 동작하기 위해 -104로 순서 지정
 * @author bluesky
 *
 */
@Order(-104)
public class BlueskyContextHolderFilter extends OncePerRequestFilter {
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Entry<String, BlueskyCoreModuleProperties> modulePropertiesEntry = ServletRequestUtil.getModulePropertiesEntry(request);
		BlueskyContextHolder.setContext(modulePropertiesEntry.getKey());
		try {
			filterChain.doFilter(request, response);
		} finally {
			BlueskyContextHolder.clearContext();
		}
	}

}
