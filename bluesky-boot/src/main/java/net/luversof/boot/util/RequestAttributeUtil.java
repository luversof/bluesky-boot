package net.luversof.boot.util;

import java.text.MessageFormat;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * HttpServlet RequestAttribute을 사용하기 위한 유틸
 * @author bluesky
 *
 */
public class RequestAttributeUtil {
	
	public static void setRequestAttribute(String name, Object value) {
		RequestContextHolder.getRequestAttributes().setAttribute(name, value, RequestAttributes.SCOPE_REQUEST);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getRequestAttribute(String name) {
		if (RequestContextHolder.getRequestAttributes() == null) {
			return null;
		}
		return (T) RequestContextHolder.getRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_REQUEST);
	}

	public static String getAttributeName(String pattern, Object ... arguments) {
		return MessageFormat.format(pattern, arguments);
	}

}
