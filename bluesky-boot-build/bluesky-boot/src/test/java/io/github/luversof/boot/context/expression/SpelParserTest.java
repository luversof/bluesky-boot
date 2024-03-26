package io.github.luversof.boot.context.expression;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import io.github.luversof.boot.context.expression.util.SpelParserUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpelParserTest {

	@ParameterizedTest
	@EnumSource(
				value = SpelParserCase.class,
				names = {
//						"COMPOSITE_SPEL4"
//						"CONCAT"
				}
	)
	void spelParserUtilTest(SpelParserCase spelParserCase) {
		var map = new HashMap<String, String>();
		map.put("keyA", "a값");
		map.put("keyB", "b값");
		map.put("keyC", "c값");
		map.put("isEnable", "true");
		
		{
			String result = SpelParserUtil.parse(spelParserCase.getStr(), map);
			log.debug("""
					대상 case : {}
					원본 값 : {}
					결과 값 : {}
					""", spelParserCase.name(), spelParserCase.getStr(), result);
		}

	}
	
	@Test
	void spelParserTest() {
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("'Hello World'.concat('!')"); 
		String message = (String) exp.getValue(); // Hello World!
		log.debug("result : {}", message);
	}
}
