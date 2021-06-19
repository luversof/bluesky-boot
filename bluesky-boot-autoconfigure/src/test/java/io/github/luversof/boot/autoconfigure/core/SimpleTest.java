package io.github.luversof.boot.autoconfigure.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Stream;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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
		Expression exp = parser.parseExpression("T(io.github.luversof.boot.autoconfigure.core.constant.TestCoreModuleInfo).TEST");
//		Expression exp = parser.parseExpression("new String('hello world').toUpperCase()");
		var value = exp.getValue();
		log.debug("result : {}", value);
		assertThat(value).isNotNull();
	}
	
	@Test
	void spelTest2() {
		SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, this.getClass().getClassLoader());
		SpelExpressionParser parser = new SpelExpressionParser(config);
		Expression exp = parser.parseExpression("T(io.github.luversof.boot.autoconfigure.core.constant.TestCoreModuleInfo).TEST");
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
	
	@ParameterizedTest
	@ValueSource(ints = { 1, 3, 5, -3, 15, Integer.MAX_VALUE }) // six numbers
	void isOdd_ShouldReturnTrueForOddNumbers(int number) {
		assertThat(number).isOdd();
	}
	
	@ParameterizedTest
	@ValueSource(strings = { "", "  " })
	void isBlank_ShouldReturnTrueForNullOrBlankStrings(String input) {
		assertThat(input).isBlank();
	}
	
	@ParameterizedTest
	@NullAndEmptySource
	void isBlank_ShouldReturnTrueForNullAndEmptyStrings(String input) {
		assertThat(input).isBlank();
	}

	@ParameterizedTest
	@MethodSource("provideStringsForIsBlank") // needs to match an existing method.
//	  @MethodSource("com.baeldung.parameterized.StringParams#blankStrings") // 클래스 외부의 source method 	  
	void isBlank_ShouldReturnTrueForNullOrBlankStrings(String input, boolean expected) {
		assertThat(input.isBlank()).isEqualTo(expected);
	}
	  
	// a static method that returns a Stream of Arguments
	private static Stream<Arguments> provideStringsForIsBlank() { // argument source method
		return Stream.of(
				// Arguments.of(null, true),
				Arguments.of("", true), 
				Arguments.of("  ", true), 
				Arguments.of("not blank", false));
	}
	
	@Test
	public void locale() {
		var locale = new Locale("zh-hant", "KR");
//		var locale = Locale.KOREA;
		log.debug("test : {}, language :{}, country : {}", locale, locale.getLanguage(), locale.getCountry());
		
	}
	
}
