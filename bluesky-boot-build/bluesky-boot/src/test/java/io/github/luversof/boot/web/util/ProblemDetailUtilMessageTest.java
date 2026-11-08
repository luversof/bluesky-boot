package io.github.luversof.boot.web.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.core.CoreBaseProperties;
import io.github.luversof.boot.exception.BlueskyErrorMessage;

/** 5xx 의 예외 원문은 SQL · 경로 같은 내부 구조를 담을 수 있어 응답에 싣지 않는다. 4xx 원문과 등록된 메시지는 싣는다. */
class ProblemDetailUtilMessageTest {

  private ApplicationContext previousContext;

  @BeforeEach
  void setUp() {
    previousContext = ApplicationContextUtil.getApplicationContext();
    var messageSource = new StaticMessageSource();
    messageSource.addMessage("UnsupportedOperationException", Locale.ROOT, "지원하지 않는 요청");
    var context = new GenericApplicationContext();
    context.getBeanFactory().registerSingleton("coreBaseProperties", new CoreBaseProperties());
    context
        .getBeanFactory()
        .registerSingleton(
            "messageSourceAccessor", new MessageSourceAccessor(messageSource, Locale.ROOT));
    context.refresh();
    ApplicationContextUtil.setApplicationContext(context);
    resetCachedMessageSourceAccessor();
  }

  @AfterEach
  void tearDown() {
    ApplicationContextUtil.setApplicationContext(previousContext);
    resetCachedMessageSourceAccessor();
  }

  /** ProblemDetailUtil 은 accessor 를 static 으로 기억하므로 시험마다 비운다. */
  private static void resetCachedMessageSourceAccessor() {
    ReflectionTestUtils.setField(ProblemDetailUtil.class, "messageSourceAccessor", null);
  }

  private static BlueskyErrorMessage errorMessage(Throwable exception, HttpStatus status) {
    return (BlueskyErrorMessage)
        ProblemDetailUtil.getProblemDetail(exception, status).getProperties().get("result");
  }

  @Test
  void serverErrorHidesRawMessage() {
    var message =
        errorMessage(new IllegalStateException("bad SQL grammar [select"), HttpStatus.BAD_GATEWAY);

    assertThat(message.getMessage()).isNull();
    assertThat(message.isDisplayableMessage()).isFalse();
  }

  @Test
  void clientErrorKeepsRawMessage() {
    var message = errorMessage(new IllegalArgumentException("id 누락"), HttpStatus.BAD_REQUEST);

    assertThat(message.getMessage()).isEqualTo("id 누락");
    assertThat(message.isDisplayableMessage()).isFalse();
  }

  @Test
  void registeredMessageIsShownEvenForServerError() {
    var message =
        errorMessage(
            new UnsupportedOperationException("internal"), HttpStatus.INTERNAL_SERVER_ERROR);

    assertThat(message.getMessage()).isEqualTo("지원하지 않는 요청");
    assertThat(message.isDisplayableMessage()).isTrue();
  }
}
