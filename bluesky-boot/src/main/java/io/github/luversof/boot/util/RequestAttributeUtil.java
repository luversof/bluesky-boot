package io.github.luversof.boot.util;

import java.text.MessageFormat;

import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * HttpServlet RequestAttribute을 사용하기 위한 유틸
 * @author bluesky
 *
 */
public class RequestAttributeUtil {
	
	public static void setRequestAttribute(String name, Object value) {
		var requestAttributes = RequestContextHolder.currentRequestAttributes();
		Assert.notNull(requestAttributes, "requestAttributes must exist");
		requestAttributes.setAttribute(name, value, RequestAttributes.SCOPE_REQUEST);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getRequestAttribute(String name) {
		var requestAttributes = RequestContextHolder.currentRequestAttributes();
		Assert.notNull(requestAttributes, "requestAttributes must exist");
		return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
	}

	public static String getAttributeName(String pattern, Object ... arguments) {
		return MessageFormat.format(pattern, arguments);
	}

}
