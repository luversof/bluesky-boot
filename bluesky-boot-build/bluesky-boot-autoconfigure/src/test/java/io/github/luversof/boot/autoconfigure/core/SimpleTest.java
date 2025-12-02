package io.github.luversof.boot.autoconfigure.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.validation.DefaultMessageCodesResolver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

@Slf4j
class SimpleTest {

	@Test
	void stringTest() {
		
		String a = "bluesky-boot.mongo.connection-map.test2.host";
		String replace = a.replace("bluesky-boot.mongo.connection-map", "");
		log.debug("result : {}", replace);
		String[] split = replace.split("\\.");
		log.debug("result : {}", split[1]);
		assertThat(split).isNotNull();
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
		"T(io.github.luversof.boot.autoconfigure.core.constant.TestModuleInfo).TEST",
		"new String('hello world').toUpperCase()"
	})
	void spelTest(String expressionString) {
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression(expressionString);
		var value = exp.getValue();
		log.debug("result : {}", value);
		assertThat(value).isNotNull();
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
		"T(io.github.luversof.boot.autoconfigure.core.constant.TestModuleInfo).TEST",
		"new String('hello world').toUpperCase()"
	})
	void spelTest2(String expressionString) {
		SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, this.getClass().getClassLoader());
		SpelExpressionParser parser = new SpelExpressionParser(config);
		Expression exp = parser.parseExpression(expressionString);
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
		map.put("key2", null);
		
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
	
	public static interface TestSupplier<T> extends Supplier<T> {
		
	}
	
	@Test
	void supplierTest() {
		Supplier<String> a = () -> "test";
		
		log.debug("result : {}", a.get());
	}
	
	@Test
	void classNameTest() {
		log.debug("name : {}", Configuration.class.getName());
	}

	@Test
	void builderTest() {
		
		PropertyMapper propertyMapper = PropertyMapper.get();

		// 기본 호출
		var testPropeties = new TestProperties();
		var builder = TestProperties.builder();
		
		propertyMapper.from(testPropeties::isEnabled).to(builder::enabled);
		propertyMapper.from(testPropeties::isEnabled2).to(builder::enabled2);
		propertyMapper.from(testPropeties::getEnabled3).to(builder::enabled3);
		propertyMapper.from(testPropeties::getEnabled4).to(builder::enabled4);
		
		log.debug("result : {}", builder.build());
		
		//반대값을 설정한 경우
		var testPropeties2 = new TestProperties();
		testPropeties2.setEnabled(true);
		testPropeties2.setEnabled2(false);
		testPropeties2.setEnabled3(true);
		testPropeties2.setEnabled4(false);
		var builder2 = TestProperties.builder();
		
		propertyMapper.from(testPropeties2::isEnabled).to(builder2::enabled);
		propertyMapper.from(testPropeties2::isEnabled2).to(builder2::enabled2);
		propertyMapper.from(testPropeties2::getEnabled3).to(builder2::enabled3);
		propertyMapper.from(testPropeties2::getEnabled4).to(builder2::enabled4);
		
		log.debug("result2 : {}", builder2.build());
		
	}
	
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	static class TestProperties {
		
		@Builder.Default
		boolean enabled = false;
		
		@Builder.Default
		boolean enabled2 = true;
		
		@Builder.Default
		Boolean enabled3 = false;
		
		@Builder.Default
		Boolean enabled4 = true;
		
	}
	
	@Test
	void sqlParserTest() throws JSQLParserException {
		{
			String sql = "SELECT * FROM table ORDER BY column1 ASC, column2 DESC LIMIT 10";
			Statement statement = CCJSqlParserUtil.parse(sql);
			Select select = (Select) statement;
			PlainSelect plainSelect = select.getPlainSelect();
			Table table = (Table) plainSelect.getFromItem();
			log.debug("table Name : {}", table.getName());
			
			
			log.debug("getOrderByElements : {}", plainSelect.getOrderByElements().toString());
		}
		{
			var whereExpression = new EqualsTo().withLeftExpression(new Column("columnA")).withRightExpression(new Column("columnAValue"));
			
		
			List<OrderByElement> orderByElementList = new ArrayList<>();
			{
				var element = new OrderByElement();
				element.setExpression(new Column("columnA"));
				orderByElementList.add(element);
			}
			
		
			var select = new PlainSelect()
					.addSelectItems(new AllColumns())
					.withFromItem(new Table("testTable"))
					.withWhere(whereExpression)
					.withOrderByElements(orderByElementList);
			
			log.debug("TEST : {}", select.toString());
			
		}
		
	}
	
	@Test
	void propertyMapperTest() {
		var propertyMapper = PropertyMapper.get();
		
		
		A a = new A();
		a.setIntA(2);
		propertyMapper.from(-1).when(x -> x > 0).to(a::setIntA);
		
		log.debug("A : {}", a);
	}
	
	@Data
	static class A {
		int intA;
	}
}
