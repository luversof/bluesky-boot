package net.luversof.boot.autoconfigure.core;

import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleTest {

	@Test
	public void stringTest() {
		
		String a = "bluesky-modules.mongo.connection-map.test2.host";
		String replace = a.replace("bluesky-modules.mongo.connection-map", "");
		log.debug("result : {}", replace);
		String[] split = replace.split("\\.");
		log.debug("result : {}", split[1]);
	}
	
	@Test
	public void spelTest() {
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("T(net.luversof.boot.autoconfigure.core.constant.TestCoreModuleInfo).TEST");
//		Expression exp = parser.parseExpression("new String('hello world').toUpperCase()");
		var value = exp.getValue();
		log.debug("result : {}", value);
	}
	
	@Test
	public void spelTest2() {
		SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, this.getClass().getClassLoader());
		SpelExpressionParser parser = new SpelExpressionParser(config);
		Expression exp = parser.parseExpression("T(net.luversof.boot.autoconfigure.core.constant.TestCoreModuleInfo).TEST");
//		Expression exp = parser.parseExpression("new String('hello world').toUpperCase()");
		var value = exp.getValue();
		log.debug("result : {}", value);
	}
}
