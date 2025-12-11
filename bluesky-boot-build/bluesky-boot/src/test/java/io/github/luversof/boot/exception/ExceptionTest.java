package io.github.luversof.boot.exception;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

class ExceptionTest {

	private static final Logger log = LoggerFactory.getLogger(ExceptionTest.class);

	@Test
	void messageResolverTest() {
		var exception = new BlueskyException("SOME_ERROR_CODE");
		String moduleName = "someModule";

		MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();

		String[] messageCodes = messageCodesResolver.resolveMessageCodes(
				exception.getClass().getSimpleName(),
				moduleName,
				exception.getErrorCode(),
				null);

		log.debug("messageCodes : {}", List.of(messageCodes));

		// fieldType null 인 경우
		// BlueskyException.someModule.SOME_ERROR_CODE
		// BlueskyException.SOME_ERROR_CODE
		// BlueskyException

		// fieldType exception.getClass() 인 경우
		// BlueskyException.someModule.SOME_ERROR_CODE
		// BlueskyException.SOME_ERROR_CODE
		// BlueskyException.io.github.luversof.boot.exception.BlueskyException
		// BlueskyException

	}

}
