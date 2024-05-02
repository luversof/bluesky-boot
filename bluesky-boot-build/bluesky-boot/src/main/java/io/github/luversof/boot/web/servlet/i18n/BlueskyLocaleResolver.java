package io.github.luversof.boot.web.servlet.i18n;

import java.util.Locale;

import org.springframework.web.servlet.LocaleResolver;

import io.github.luversof.boot.context.BlueskyContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BlueskyLocaleResolver implements LocaleResolver {
	
	// 초기 customizer를 전달 받는 형태가 아닌 Map에서 module 별 관리를 하는 식으로 해야 개별 설정처리가 가능함
	// 따라서 기본 customizer를 설정하고 module별 설정을 개별 구현한 경우 해당 설정을 사용하는 식의 분기 처리가 되어야 함
	
	
	
	public BlueskyLocaleResolver() {
		
	}

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		// TODO Auto-generated method stub
		
	}

	
	private String getModuleName() {
		return null;
	}
}
