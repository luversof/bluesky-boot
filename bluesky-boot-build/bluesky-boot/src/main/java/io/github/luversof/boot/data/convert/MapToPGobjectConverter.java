package io.github.luversof.boot.data.convert;

import java.sql.SQLException;
import java.util.Map;

import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * PostgreSQL JSONB 컬럼 처리를 위한 Map to PGobject Converter
 */
@WritingConverter
public class MapToPGobjectConverter implements Converter<Map<String, Object>, PGobject> {

	private final JsonMapper jsonMapper = new JsonMapper();

	@Override
	public PGobject convert(Map<String, Object> source) {
		try {
			PGobject jsonObject = new PGobject();
			jsonObject.setType("jsonb");
			jsonObject.setValue(jsonMapper.writeValueAsString(source));
			return jsonObject;
		} catch (JacksonException | SQLException e) {
			throw new IllegalArgumentException("Error converting Map to JSONB", e);
		}
	}
}