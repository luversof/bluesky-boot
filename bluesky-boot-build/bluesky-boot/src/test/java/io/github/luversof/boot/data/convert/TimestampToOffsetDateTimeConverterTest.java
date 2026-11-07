package io.github.luversof.boot.data.convert;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.TimeZone;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TimestampToOffsetDateTimeConverterTest {

  private final TimeZone originalTimeZone = TimeZone.getDefault();

  private final TimestampToOffsetDateTimeConverter converter =
      new TimestampToOffsetDateTimeConverter();

  @AfterEach
  void restoreTimeZone() {
    TimeZone.setDefault(originalTimeZone);
  }

  /** 지금이 어느 계절이든 그 시각의 offset 을 쓴다 - 겨울과 여름을 함께 봐야 현재 offset 을 쓰는 오류가 잡힌다. */
  @Test
  void usesOffsetOfTheTimestampNotOfNow() {
    TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
    var winter = Instant.parse("2026-01-15T12:00:00Z");
    var summer = Instant.parse("2026-07-15T12:00:00Z");

    var convertedWinter = converter.convert(Timestamp.from(winter));
    var convertedSummer = converter.convert(Timestamp.from(summer));

    assertThat(convertedWinter.getOffset()).isEqualTo(ZoneOffset.ofHours(-5));
    assertThat(convertedWinter.toInstant()).isEqualTo(winter);
    assertThat(convertedSummer.getOffset()).isEqualTo(ZoneOffset.ofHours(-4));
    assertThat(convertedSummer.toInstant()).isEqualTo(summer);
  }
}
