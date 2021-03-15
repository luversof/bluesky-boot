package net.luversof.boot.autoconfigure.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.validation.DefaultMessageCodesResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class SimpleTest {

	@Test
	void stringTest() {
		
		String a = "bluesky-modules.mongo.connection-map.test2.host";
		String replace = a.replace("bluesky-modules.mongo.connection-map", "");
		log.debug("result : {}", replace);
		String[] split = replace.split("\\.");
		log.debug("result : {}", split[1]);
		assertThat(split).isNotNull();
	}
	
	@Test
	void spelTest() {
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("T(net.luversof.boot.autoconfigure.core.constant.TestCoreModuleInfo).TEST");
//		Expression exp = parser.parseExpression("new String('hello world').toUpperCase()");
		var value = exp.getValue();
		log.debug("result : {}", value);
		assertThat(value).isNotNull();
	}
	
	@Test
	void spelTest2() {
		SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, this.getClass().getClassLoader());
		SpelExpressionParser parser = new SpelExpressionParser(config);
		Expression exp = parser.parseExpression("T(net.luversof.boot.autoconfigure.core.constant.TestCoreModuleInfo).TEST");
//		Expression exp = parser.parseExpression("new String('hello world').toUpperCase()");
		var value = exp.getValue();
		log.debug("result : {}", value);
		assertThat(value).isNotNull();
	}
	
	@Test
	void messageCodeResolveTest() {
		
		var codeResolver = new DefaultMessageCodesResolver();
		
		String[] codes = codeResolver.resolveMessageCodes("NotBlank", "someObjectName", "someField", null);
		log.debug("resulit : {}", Arrays.asList(codes));
		assertThat(codes).isNotNull();
	}
	
	@Test
	void mapTest() {
		var map = new HashMap<String, String>();
		map.put("key1", "value1");
		
		var target = map.entrySet().iterator().next().getValue();
		log.debug("target : {}", target);
		assertThat(target).isEqualTo("value1");
	}
}
