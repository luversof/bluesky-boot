package io.github.luversof.boot.data.convert;

import java.util.Map;

import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * PostgreSQL JSONB 컬럼 처리를 위한 PGobject to Map Converter
 */
@ReadingConverter
public class PGobjectToMapConverter implements Converter<PGobject, Map<String, Object>> {

	private final JsonMapper jsonMapper = new JsonMapper();

	@Override
	public Map<String, Object> convert(PGobject source) {
		if (source == null || source.getValue() == null) {
			return null;
		}
		try {
			return jsonMapper.readValue(source.getValue(), new TypeReference<Map<String, Object>>() {});
		} catch (Exception e) {
			throw new IllegalArgumentException("Error converting PGobject JSONB to Map", e);
		}
	}
}