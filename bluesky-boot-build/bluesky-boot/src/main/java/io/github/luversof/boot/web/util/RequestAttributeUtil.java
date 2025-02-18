package io.github.luversof.boot.web.util;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.cache.support.NullValue;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * HttpServlet RequestAttribute을 사용하기 위한 유틸
 * @author bluesky
 *
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class RequestAttributeUtil {
	
	public static void setRequestAttribute(String name, Object value) {
		var requestAttributes = RequestContextHolder.currentRequestAttributes();
		Assert.notNull(requestAttributes, "requestAttributes must exist");
		requestAttributes.setAttribute(name, value, RequestAttributes.SCOPE_REQUEST);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getRequestAttribute(String name, Supplier<T> supplier) {
		var requestAttributes = RequestContextHolder.currentRequestAttributes();
		Assert.notNull(requestAttributes, "requestAttributes must exist");
		var attribute = (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
		if (attribute != null) {
			return attribute;
		}
		return supplier.get();
	}
	
	public static <T> T getRequestAttribute(String name) {
		return getRequestAttribute(name, () -> null);
	}

	public static String getAttributeName(String pattern, Object ... arguments) {
		return MessageFormat.format(pattern, arguments);
	}
	
	public static <T> T getObject(String attributeName, Supplier<T> supplier) {
		Optional<T> optional = getRequestAttribute(attributeName, Optional::empty);
		
		if (optional.isPresent()) {
			var value = optional.get();
			if (value instanceof NullValue) {
				return null;
			}
			return value;
		}
		
		T object = supplier.get();
		setRequestAttribute(attributeName, Optional.of(object == null ? NullValue.INSTANCE : object));
		
		return object;		
	}
	
	public static <T> List<T> getList(String attributeName, Supplier<List<T>> supplier) {
		List<T> list = getRequestAttribute(attributeName);
		
		if (list != null) {
			return list;
		}
		list = supplier.get();
		if (list == null) {
			list = Collections.emptyList();
		}
		setRequestAttribute(attributeName, list);
		
		return list;
	}

}
