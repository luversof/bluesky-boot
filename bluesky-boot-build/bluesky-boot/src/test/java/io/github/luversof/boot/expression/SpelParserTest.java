package io.github.luversof.boot.expression;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** htmx 응답 헤더가 쓰는 SpEL 해석 - 일반 식과 #{} 템플릿을 둘 다 받는다. */
class SpelParserTest {

  private static final Map<String, String> SOURCE =
      Map.of("keyA", "a값", "keyB", "b값", "isEnable", "true");

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      quoteCharacter = '"', // textBlock 에서 # 으로 시작하는 줄은 주석이라 따옴표로 감싼다
      textBlock =
          """
          '테스트해봄'                                       | 테스트해봄
          '테스트' + '해봄'                                  | 테스트해봄
          'abc'.substring(1, 3)                             | bc
          2 < -5.0                                          | false
          "#{keyA}"                                           | a값
          "#{keyA}을 호출해 봄"                                | a값을 호출해 봄
          "#{keyA}, #{keyB} 을 호출해 봄"                      | a값, b값 을 호출해 봄
          "#{keyA} + ':' + #{keyB}"                           | a값 + ':' + b값
          "#keyA + ':' + #keyB"                               | a값:b값
          "#{keyA}을 호출해 보면서 '쉼표'를 사용하면"            | a값을 호출해 보면서 '쉼표'를 사용하면
          'Hello World'.concat('!')                         | Hello World!
          "#isEnable ? 'A' : 'B'"                             | A
          "#{keyA == keyB}"                                   | false
          "#keyA == keyB"                                     | false
          "#{T(org.springframework.util.StringUtils).hasText(#keyA)}" | true
          new String('hello world').toUpperCase()           | HELLO WORLD
          """)
  void parse(String expression, String expected) {
    assertThat(SpelParserUtil.parse(expression, SOURCE)).isEqualTo(expected);
  }
}
