package io.github.luversof.boot.web.servlet.util;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.PathContainer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPattern.PathMatchInfo;

import io.github.luversof.boot.context.ApplicationContextUtil;
import jakarta.servlet.http.HttpServletRequest;

public final class ServletRequestUtil {

	private static final Logger log = LoggerFactory.getLogger(ServletRequestUtil.class);

	private static final String IPV4_PATTERN = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

	private ServletRequestUtil() {
	}

	public static String getRemoteAddr() {
		var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		log.debug("ServletRequestUtil.getRemoteAddr() : remoteAddr : {},x-forwarded-for : {}", request.getRemoteAddr(),
				request.getHeader("X-Forwarded-For"));
		var xForwardedForHeaders = request.getHeader("X-Forwarded-For");
		String remoteAddr = null;
		if (xForwardedForHeaders == null || xForwardedForHeaders.length() == 0) {
			remoteAddr = request.getRemoteAddr();
		} else {
			remoteAddr = xForwardedForHeaders.split(",")[0];
		}
		return remoteAddr;
	}

	public static String getUserAgent() {
		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()
				.getHeader(HttpHeaders.USER_AGENT);
	}

	/**
	 * 내부 요청인 경우 체크
	 * serverName이 도메인이 아닌 ip 형태인 경우 내부 효출로 판단함
	 * 
	 * @return
	 */
	public static boolean isInternalRequest(HttpServletRequest request) {
		var serverName = request.getServerName();
		if (serverName.equals("localhost")) {
			return true;
		}
		return serverName.matches(IPV4_PATTERN);
	}

	/**
	 * url에 등록된 pathVariable을 조회하는 기능
	 * 일반적으로 interceptor 이후엔 requestAttribute에서 직접 꺼내 사용하면 되며
	 * requestAttribute 값이 설정되기 이전인 filter에서 pathVariable을 사용할 수 있도록 제공함
	 * 
	 * @return
	 */
	public static Map<String, String> getUriVariableMap() {
		var applicationContext = ApplicationContextUtil.getApplicationContext();
		RequestMappingHandlerMapping mapping = applicationContext.getBean("requestMappingHandlerMapping",
				RequestMappingHandlerMapping.class);

		try {
			var requestAttributes = RequestContextHolder.getRequestAttributes();
			if (requestAttributes == null) {
				return Collections.emptyMap();
			}
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

			HandlerExecutionChain handler = mapping.getHandler(request);
			if (handler == null) {
				return Collections.emptyMap();
			}

			var handlerMethod = (HandlerMethod) handler.getHandler();
			Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();

			RequestMappingInfo requestMappingInfo = null;
			for (var entry : handlerMethods.entrySet()) {
				if (entry.getValue().getMethod() == handlerMethod.getMethod()) {
					requestMappingInfo = entry.getKey();
					break;
				}
			}

			if (requestMappingInfo == null) {
				return Collections.emptyMap();
			}

			var pathPatternsCondition = requestMappingInfo.getPathPatternsCondition();
			if (pathPatternsCondition == null) {
				return Collections.emptyMap();
			}

			PathContainer path = ServletRequestPathUtils.getParsedRequestPath(request).pathWithinApplication();
			PathPattern pathPattern = pathPatternsCondition.getFirstPattern();
			PathMatchInfo matchAndExtract = pathPattern.matchAndExtract(path);
			if (matchAndExtract == null) {
				return Collections.emptyMap();
			}

			return matchAndExtract.getUriVariables();
		} catch (Exception e) {
			log.debug("getUriVariableMap exception", e);
		}
		return Collections.emptyMap();
	}

}
