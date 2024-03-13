package io.github.luversof.boot.context.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SpelParserCase {
	ONLY_STRING("테스트해봄"),
	ONLY_STRING2("'테스트해봄'"),
	ONLY_STRING3("'테스트' + '해봄'"),
	SINGLE_SPEL("#{keyA}"),
	COMPOSITE_SPEL("#{keyA}을 호출해 봄"),
	COMPOSITE_SPEL2("#{keyA}, #{keyB} 을 호출해 봄"),
	COMPOSITE_SPEL3("#{keyA} + ':' + #{keyB}"),
	COMPOSITE_SPEL4("#keyA + ':' + #keyB"),
	COMPOSITE_SPEL5("random number is #{T(java.lang.Math).random()}"),
	COMPOSITE_SPEL6("#{keyA}을 호출해 보면서 '쉼표'를 사용하면"),
	CONCAT("'Hello World'.concat('!')"),
	;
	@Getter String str;
}
