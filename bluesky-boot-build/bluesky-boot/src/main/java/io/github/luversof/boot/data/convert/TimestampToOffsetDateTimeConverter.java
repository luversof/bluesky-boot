package io.github.luversof.boot.data.convert;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * PostgreSQL의 경우 읽을 때 Timestamp -> OffsetDateTime 변환이 필요함
 * (그런데 쓸 때는 변환이 필요없음)
 */
@ReadingConverter
public class TimestampToOffsetDateTimeConverter implements Converter<Timestamp, OffsetDateTime> {

	@Override
	public OffsetDateTime convert(Timestamp source) {
		return source == null ? null : source.toInstant().atOffset(OffsetDateTime.now().getOffset());
	}

}
