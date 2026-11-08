package io.github.luversof.boot.web.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.RestClientResponseException;

import io.github.luversof.boot.exception.BlueskyException;
import tools.jackson.databind.json.JsonMapper;

/** 오류 응답 본문이 bluesky 형식이 아니어도 상태 코드를 잃지 않는다. */
class BlueskyClientResponseErrorHandlerTest {

  private final BlueskyClientResponseErrorHandler handler =
      new BlueskyClientResponseErrorHandler(JsonMapper.builder().build());

  private final URI url = URI.create("http://api.local/item");

  private void handle(String body, HttpStatus status) throws Exception {
    handler.handleError(
        url,
        HttpMethod.GET,
        new MockClientHttpResponse(body.getBytes(StandardCharsets.UTF_8), status));
  }

  @Test
  void htmlBodyKeepsStatusAndBody() {
    assertThatThrownBy(() -> handle("<html>Bad Gateway</html>", HttpStatus.BAD_GATEWAY))
        .isInstanceOfSatisfying(
            RestClientResponseException.class,
            e -> {
              assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
              assertThat(e.getResponseBodyAsString()).contains("Bad Gateway");
            });
  }

  @Test
  void emptyBodyKeepsStatus() {
    assertThatThrownBy(() -> handle("", HttpStatus.NOT_FOUND))
        .isInstanceOf(RestClientResponseException.class);
  }

  @Test
  void blueskyResultBecomesBlueskyException() {
    assertThatThrownBy(() -> handle("{\"result\":{\"errorCode\":\"E1\"}}", HttpStatus.BAD_REQUEST))
        .isInstanceOf(BlueskyException.class);
  }
}
