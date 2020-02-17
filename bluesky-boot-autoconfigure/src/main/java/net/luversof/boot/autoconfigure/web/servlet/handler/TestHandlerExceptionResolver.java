package net.luversof.boot.autoconfigure.web.servlet.handler;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import net.luversof.boot.autoconfigure.context.MessageUtil;
import net.luversof.boot.exception.ErrorMessage;

@Slf4j
public class TestHandlerExceptionResolver implements HandlerExceptionResolver {
	
	public static final String RESULT = "result";

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		log.debug("TEST : ");
		Map<String, ErrorMessage> resultMap = new HashMap<>();
		ErrorMessage errorMessage = MessageUtil.getErrorMessage(ex);
		String errorPage = "/errorPage";
		
		resultMap.put(RESULT, errorMessage);
		return new ModelAndView(errorPage, resultMap);
	}

}
