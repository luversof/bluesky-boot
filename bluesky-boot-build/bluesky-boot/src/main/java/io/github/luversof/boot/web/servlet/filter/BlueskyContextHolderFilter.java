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
 * single module 이 아닌 경우 요청에 대해 moduleName을 ContextHolder에 설정 OrderedRequestContextFilter보다 후순위로
 * 동작하기 위해 -104로 순서 지정
 *
 * @author bluesky
 */
@Order(-104)
public class BlueskyContextHolderFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    ModuleNameInfo moduleNameInfo = new ModuleNameInfo();
    BlueskyContextHolder.setContext(
        () -> {
          // resolve 결과가 null(= 상위 properties 사용)인 경우에도 캐싱하여 요청당 1회만 resolve 한다.
          if (!moduleNameInfo.isResolved()) {
            moduleNameInfo.setModuleName(
                ApplicationContextUtil.getApplicationContext()
                    .getBean(ModuleNameResolver.class)
                    .resolve(request));
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

    /**
     * resolve 수행 여부. moduleName이 null(= 상위 properties 사용)인 경우와 아직 resolve 하지 않은 경우를 구분하기 위해 사용한다.
     */
    private boolean resolved;

    public String getModuleName() {
      return moduleName;
    }

    public void setModuleName(String moduleName) {
      this.moduleName = moduleName;
      this.resolved = true;
    }

    public boolean isResolved() {
      return this.resolved;
    }
  }
}
