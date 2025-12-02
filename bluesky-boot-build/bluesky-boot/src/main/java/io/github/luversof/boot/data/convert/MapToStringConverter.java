package io.github.luversof.boot.data.convert;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * MariaDB JSON 컬럼 처리를 위한 Map to String Converter
 */
@WritingConverter
public class MapToStringConverter implements Converter<Map<String, Object>, String> {

	private final JsonMapper jsonMapper = new JsonMapper();

	@Override
	public String convert(Map<String, Object> source) {
		try {
			return jsonMapper.writeValueAsString(source);
		} catch (JacksonException e) {
			throw new IllegalArgumentException("Error converting Map to JSON", e);
		}
	}
}
