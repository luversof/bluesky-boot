package io.github.luversof.boot.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

class ExceptionTest {

  /** 오류 메시지 키는 모듈 전용 → 공통 → 예외 이름 순으로 찾는다. */
  @Test
  void messageResolverTest() {
    var exception = new BlueskyException("SOME_ERROR_CODE");
    String moduleName = "someModule";

    MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();

    String[] messageCodes =
        messageCodesResolver.resolveMessageCodes(
            exception.getClass().getSimpleName(), moduleName, exception.getErrorCode(), null);

    assertThat(messageCodes)
        .containsExactly(
            "BlueskyException.someModule.SOME_ERROR_CODE",
            "BlueskyException.SOME_ERROR_CODE",
            "BlueskyException");
  }
}
