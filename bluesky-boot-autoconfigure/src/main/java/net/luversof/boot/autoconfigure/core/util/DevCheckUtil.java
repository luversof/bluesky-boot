package net.luversof.boot.autoconfigure.core.util;

import java.lang.reflect.Method;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.luversof.boot.config.BlueskyCoreModuleProperties.PathForwardProperties;


/**
 * _check 페이지 구성을 위헤 제공하는 유틸
 * @author bluesky
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DevCheckUtil {
	
	private static final DefaultParameterNameDiscoverer DISCOVERER = new DefaultParameterNameDiscoverer();
	
	public static String[] getParameterNames(Method method) {
		return DISCOVERER.getParameterNames(method);
	}

	/**
	 * 테스트 url 주소의 경우 requestPath가 설정되어 있는 경우 해당 path를 추가해준다.
	 * @param url
	 * @param method
	 * @return
	 */
	public static String getUrlWithParameter(PathForwardProperties pathForwardProperties, String url, Method method) {
		StringBuilder stringBuilder = new StringBuilder();
		String requestPath = pathForwardProperties == null ? null : pathForwardProperties.getRequestPath();
		if (requestPath != null && requestPath.length() > 1 ) {
			stringBuilder.append(requestPath);
		}
		stringBuilder.append(url);
		appendParameter(stringBuilder, method);
		return stringBuilder.toString().replace("//", "/");
	}
	
	private static void appendParameter(StringBuilder stringBuilder, Method method) {
		
		String[] parameterNames = getParameterNames(method);
		if (parameterNames.length == 0) {
			return;
		}
		
		for (int i = 0 ; i < parameterNames.length ; i++) {
			if (method.getParameters()[i].isAnnotationPresent(PathVariable.class)) {
				continue;
			}
			stringBuilder.append(i == 0 ? "?" : "&").append(parameterNames[i]).append("={").append(parameterNames[i]).append("}");
		}
	}
}
