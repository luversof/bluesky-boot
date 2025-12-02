package io.github.luversof.boot.data.convert;

import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * MariaDB JSON 컬럼 처리를 위한 String to Map Converter
 */
@ReadingConverter
public class StringToMapConverter implements Converter<String, Map<String, Object>> {
	
	private final JsonMapper jsonMapper = new JsonMapper();

	@Override
	public Map<String, Object> convert(String source) {
		try {
			return jsonMapper.readValue(source, new TypeReference<Map<String, Object>>() {});
		} catch (Exception e) {
			throw new IllegalArgumentException("Error converting JSON to Map", e);
		}
	}
}