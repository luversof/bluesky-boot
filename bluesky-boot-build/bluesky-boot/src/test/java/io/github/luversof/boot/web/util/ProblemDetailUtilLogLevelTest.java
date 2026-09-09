package io.github.luversof.boot.web.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.luversof.boot.exception.BlueskyException;

/**
 * 예외 로그 수준은 상태 코드를 따른다: 4xx 는 WARN(스택은 DEBUG), 5xx 는 ERROR(스택 포함).
 *
 * <p>실측 2026-09-09: api-stock 의 당일 ERROR 4 건이 전부 의도된 400(남의 계좌 조회, userId 누락)이었다. 클라이언트 잘못이 서버 장애와
 * 같은 수준으로 쌓이면 진짜 장애를 가려낼 수 없다.
 */
class ProblemDetailUtilLogLevelTest {

  private final Logger logger = (Logger) LoggerFactory.getLogger(ProblemDetailUtil.class);
  private final ListAppender<ILoggingEvent> events = new ListAppender<>();
  private Level previousLevel;

  @BeforeEach
  void attach() {
    previousLevel = logger.getLevel();
    logger.setLevel(Level.DEBUG);
    events.start();
    logger.addAppender(events);
  }

  @AfterEach
  void detach() {
    logger.detachAppender(events);
    logger.setLevel(previousLevel);
  }

  private List<ILoggingEvent> ofLevel(Level level) {
    return events.list.stream().filter(e -> e.getLevel() == level).toList();
  }

  @Test
  void 클라이언트_오류는_WARN_한_줄이고_스택은_DEBUG_에만() {
    var exception = new BlueskyException("StockErrorCode.INVALID_USER_ID", 400);

    ProblemDetailUtil.logException("BlueskyException occurred", exception, exception.getStatus());

    assertThat(ofLevel(Level.ERROR)).as("400 이 ERROR 로 남으면 장애와 섞인다").isEmpty();
    List<ILoggingEvent> warns = ofLevel(Level.WARN);
    assertThat(warns).hasSize(1);
    assertThat(warns.get(0).getFormattedMessage())
        .contains("client error 400")
        .contains("INVALID_USER_ID");
    assertThat(warns.get(0).getThrowableProxy()).as("WARN 줄에는 스택을 붙이지 않는다").isNull();
    List<ILoggingEvent> debugs = ofLevel(Level.DEBUG);
    assertThat(debugs).hasSize(1);
    assertThat(debugs.get(0).getThrowableProxy()).as("스택은 DEBUG 로는 남긴다").isNotNull();
  }

  @Test
  void 서버_오류는_그대로_ERROR_와_스택이다() {
    var exception = new BlueskyException("SomeErrorCode.FAILED", 500);

    ProblemDetailUtil.logException("BlueskyException occurred", exception, exception.getStatus());

    assertThat(ofLevel(Level.WARN)).isEmpty();
    List<ILoggingEvent> errors = ofLevel(Level.ERROR);
    assertThat(errors).hasSize(1);
    assertThat(errors.get(0).getThrowableProxy()).isNotNull();
  }

  /** 경계: 399 와 500 은 4xx 가 아니다. */
  @Test
  void 경계값() {
    ProblemDetailUtil.logException("x", new RuntimeException("a"), 499);
    ProblemDetailUtil.logException("x", new RuntimeException("b"), 500);
    ProblemDetailUtil.logException("x", new RuntimeException("c"), 399);

    assertThat(ofLevel(Level.WARN)).hasSize(1);
    assertThat(ofLevel(Level.ERROR)).hasSize(2);
  }
}
