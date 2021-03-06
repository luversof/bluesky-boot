package io.github.luversof.boot.autoconfigure.web.servlet.handler;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import io.github.luversof.boot.autoconfigure.context.MessageUtil;
import io.github.luversof.boot.exception.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * @ExceptionHandler 로 처리하지 못하는 에러 처리를 위한 방법 
 * @author luver
 *
 */
@Slf4j
public class TestHandlerExceptionResolver implements HandlerExceptionResolver {
	
	public static final String RESULT = "result";

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		
		// 특정 조건일 때만 수행하고 그 외에는 null 반환 처리
		var className = ex.getClass().getSimpleName();
		if (className != null && className.equals("test")) {
			return null;
		}
		
		log.debug("TEST : ");
		var resultMap = new HashMap<String, ErrorMessage>();
		var errorMessage = MessageUtil.getErrorMessage(ex);
		var errorPage = "/errorPage";
		
		resultMap.put(RESULT, errorMessage);
		return new ModelAndView(errorPage, resultMap);
	}

}
