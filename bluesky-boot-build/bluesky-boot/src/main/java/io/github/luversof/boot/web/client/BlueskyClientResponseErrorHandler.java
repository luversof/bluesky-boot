package io.github.luversof.boot.web.client;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientResponseException;

import io.github.luversof.boot.exception.BlueskyErrorMessage;
import io.github.luversof.boot.exception.BlueskyException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

public class BlueskyClientResponseErrorHandler implements ResponseErrorHandler {

  private static final Logger log =
      LoggerFactory.getLogger(BlueskyClientResponseErrorHandler.class);

  private JsonMapper jsonMapper;

  public BlueskyClientResponseErrorHandler(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return response.getStatusCode().isError();
  }

  @Override
  public void handleError(URI url, HttpMethod method, ClientHttpResponse response)
      throws IOException {

    var responseBody = response.getBody().readAllBytes();
    var result = readResult(responseBody);

    // 빈 본문 · 프록시의 HTML 오류 페이지처럼 result 가 없으면 상태 코드와 본문을 그대로 넘긴다
    if (result == null) {
      log.debug("api client response has no result: {} {}", method, url);
      throw new RestClientResponseException(
          method + " " + url + " failed with " + response.getStatusCode(),
          response.getStatusCode(),
          response.getStatusText(),
          response.getHeaders(),
          responseBody,
          StandardCharsets.UTF_8);
    }

    if (result instanceof List) {
      var errorMessages = jsonMapper.convertValue(result, BlueskyErrorMessage[].class);
      throw new BlueskyException(Arrays.asList(errorMessages));
    } else {
      var errorMessage = jsonMapper.convertValue(result, BlueskyErrorMessage.class);
      throw new BlueskyException(errorMessage);
    }
  }

  private Object readResult(byte[] responseBody) {
    try {
      return jsonMapper.readValue(responseBody, Map.class) instanceof Map<?, ?> resultMap
          ? resultMap.get("result")
          : null;
    } catch (JacksonException e) {
      return null;
    }
  }
}
