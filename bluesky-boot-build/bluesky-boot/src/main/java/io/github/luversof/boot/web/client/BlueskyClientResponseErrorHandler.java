package io.github.luversof.boot.web.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import io.github.luversof.boot.exception.BlueskyErrorMessage;
import io.github.luversof.boot.exception.BlueskyException;
import tools.jackson.databind.json.JsonMapper;

public class BlueskyClientResponseErrorHandler implements ResponseErrorHandler {

	private static final Logger log = LoggerFactory.getLogger(BlueskyClientResponseErrorHandler.class);

	private JsonMapper jsonMapper;
	
	public BlueskyClientResponseErrorHandler(JsonMapper jsonMapper) {
		this.jsonMapper = jsonMapper;
	}

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return response.getStatusCode().isError();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {

		var responseBody = readFully(response.getBody());
		Map<String, Object> resultMap = jsonMapper.readValue(responseBody, Map.class);

		if (!resultMap.containsKey("result")) {
			log.debug("api client request has not result");
			throw new BlueskyException("NOT_EXIST_API_RESULT");
		}

		Object result = resultMap.get("result");

		if (result instanceof List) {
			var errorMessages = jsonMapper.convertValue(result, BlueskyErrorMessage[].class);
			throw new BlueskyException(Arrays.asList(errorMessages));
		} else {
			var errorMessage = jsonMapper.convertValue(result, BlueskyErrorMessage.class);
			throw new BlueskyException(errorMessage);
		}
	}

	private String readFully(InputStream inputStream) throws IOException {
		try (var buffer = new BufferedReader(new InputStreamReader(inputStream))) {
			return buffer.lines().collect(Collectors.joining("\n"));
		}
	}

}
