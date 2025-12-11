package io.github.luversof.boot.web.servlet.util;

import java.io.IOException;

import org.springframework.beans.BeanUtils;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.validation.ValidationUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

/**
 * 요청받은 request 의 parameter를 기반으로 modelAttribute object를 호출하는 유틸.
 * servletRequest를 기반으로 하기 때문에 되도록 controller 에서 사용해야함
 * 
 * @author bluesky
 *
 */
public final class ServletRequestDataBinderUtil {

	private static final Logger log = LoggerFactory.getLogger(ServletRequestDataBinderUtil.class);

	private ServletRequestDataBinderUtil() {
	}

	public static <T> T getObject(String objectName, Class<T> clazz, Object... validationHints) {
		var requestAttributes = RequestContextHolder.currentRequestAttributes();
		Assert.notNull(requestAttributes, "requestAttributes must exist");
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

		T instantiateClass = BeanUtils.instantiateClass(clazz);

		ServletRequestDataBinder servletRequestDataBinder = new ServletRequestDataBinder(instantiateClass, objectName);
		servletRequestDataBinder.setConversionService(
				ApplicationContextUtil.getApplicationContext().getBean(FormattingConversionService.class));
		servletRequestDataBinder.bind(request);

		@SuppressWarnings("unchecked")
		T target = (T) servletRequestDataBinder.getTarget();

		if (validationHints != null) {
			ValidationUtil.validate(target, validationHints);
		}

		return target;
	}

	public static <T> T getObject(Class<T> clazz) {
		return getObject(null, clazz, (Object[]) null);
	}

	public static <T> T getObject(String objectName, Class<T> clazz) {
		return getObject(objectName, clazz, (Object[]) null);
	}

	public static <T> T getObject(Class<T> clazz, Object... validationHints) {
		return getObject(null, clazz, validationHints);
	}

	public static <T> T getRequestBodyObject(JsonMapper jsonMapper, Class<T> clazz, Object... validationHints) {
		try {
			var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
			var inputMessage = new ServletServerHttpRequest(request);
			var inputStream = StreamUtils.nonClosing(inputMessage.getBody());

			if (jsonMapper == null) {
				jsonMapper = ApplicationContextUtil.getApplicationContext().getBean(JsonMapper.class);
			}

			T target = jsonMapper.readValue(inputStream, clazz);

			if (validationHints != null) {
				ValidationUtil.validate(target, validationHints);
			}
			return target;
		} catch (IOException e) {
			log.error("failed to read request body", e);
		}

		return null;
	}

	public static <T> T getRequestBodyObject(Class<T> clazz, Object... validationHints) {
		return getRequestBodyObject(null, clazz, validationHints);
	}

	public static <T> T getRequestBodyObject(JsonMapper jsonMapper, Class<T> clazz) {
		return getRequestBodyObject(jsonMapper, clazz, (Object[]) null);
	}

	public static <T> T getRequestBodyObject(Class<T> clazz) {
		return getRequestBodyObject(clazz, (Object[]) null);
	}
}