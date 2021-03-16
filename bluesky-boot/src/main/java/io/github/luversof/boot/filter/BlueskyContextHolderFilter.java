package io.github.luversof.boot.filter;

import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.luversof.boot.config.BlueskyCoreModuleProperties;
import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.util.ServletRequestUtil;

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
