package io.github.luversof.boot.web.servlet.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.LocaleResolver;

import io.github.luversof.boot.context.BlueskyContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BlueskyLocaleResolver implements LocaleContextResolver {
	
	List<BlueskyLocaleResolverCustomizer> customizerList;

	@Override
	public LocaleContext resolveLocaleContext(HttpServletRequest request) {
		List<LocaleResolverResult> resultList = new ArrayList<>();
		customizerList.forEach(x -> resultList.add(x.resolveLocaleContext(request, resultList)));
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response,
			LocaleContext localeContext) {
		// TODO Auto-generated method stub
		
	}

}
