package io.github.luversof.boot.test.autoconfigure.restdocs;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Setter;
import lombok.SneakyThrows;

@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTest {

	private static final String DEFAULT_MOCK_RESPONSE_PATH = "src/test/resources/mockResponse/";

	@Setter(onMethod_ = { @Autowired })
	private ObjectMapper objectMapper;

	protected MockMvc mockMvc;

	protected String getMockResponsePath() {
		return DEFAULT_MOCK_RESPONSE_PATH;
	}

	@BeforeEach
	public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		var mockBuilder = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(documentationConfiguration(restDocumentation).operationPreprocessors()
						.withRequestDefaults(prettyPrint()).withResponseDefaults(prettyPrint()))
				.alwaysDo(MockMvcResultHandlers.print()).addFilter(new CharacterEncodingFilter("UTF-8", true));

		this.mockMvc = mockBuilder.build();
	}

	@SneakyThrows
	protected String getMockString(String path) {
		return Files.readString(Paths.get(getMockResponsePath() + path));
	}

	@SneakyThrows
	protected <T> T getMock(String path, Class<T> valueType) {
		return objectMapper.readValue(getMockString(path), valueType);
	}

	@SuppressWarnings("unchecked")
	@SneakyThrows
	protected <T> List<T> getMockList(String path, Class<T> valueType) {
		return objectMapper.readValue(getMockString(path), List.class);
	}

	@SneakyThrows
	protected <T> Optional<T> getMockOptional(String path, Class<T> valueType) {
		return Optional.of(objectMapper.readValue(getMockString(path), valueType));
	}

}
