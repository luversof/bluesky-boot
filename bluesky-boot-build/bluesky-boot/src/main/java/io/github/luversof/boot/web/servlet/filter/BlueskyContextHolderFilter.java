package io.github.luversof.boot.web.servlet.filter;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.web.servlet.support.ModuleNameResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * single module 이 아닌 경우 요청에 대해 moduleName을 ContextHolder에 설정
 * OrderedRequestContextFilter보다 후순위로 동작하기 위해 -104로 순서 지정
 * 
 * @author bluesky
 *
 */
@Order(-104)
public class BlueskyContextHolderFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		ModuleNameInfo moduleNameInfo = new ModuleNameInfo();
		BlueskyContextHolder.setContext(() -> {
			if (moduleNameInfo.getModuleName() == null) {
				var resolvedModuleName = ApplicationContextUtil.getApplicationContext()
						.getBean(ModuleNameResolver.class).resolve(request);
				if (resolvedModuleName == null) {
					return null;
				}
				moduleNameInfo.setModuleName(resolvedModuleName);
			}
			return moduleNameInfo.getModuleName();
		});

		try {
			filterChain.doFilter(request, response);
		} finally {
			BlueskyContextHolder.clearContext();
		}
	}

	public static class ModuleNameInfo {
		private String moduleName;

		public String getModuleName() {
			return moduleName;
		}

		public void setModuleName(String moduleName) {
			this.moduleName = moduleName;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			ModuleNameInfo that = (ModuleNameInfo) o;
			return moduleName != null ? moduleName.equals(that.moduleName) : that.moduleName == null;
		}

		@Override
		public int hashCode() {
			return moduleName != null ? moduleName.hashCode() : 0;
		}

		@Override
		public String toString() {
			return "ModuleNameInfo{" +
					"moduleName='" + moduleName + '\'' +
					'}';
		}
	}

}
