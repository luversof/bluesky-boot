package io.github.luversof.boot.expression;

import java.util.List;
import java.util.Map;

import org.springframework.expression.spel.support.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public final class SpelParserUtil {

	private SpelParserUtil() {
	}

	private static final SpelExpressionParser PARSER = new SpelExpressionParser();

	private static final ParserContext CONTEXT = new TemplateParserContext();

	private static final List<PropertyAccessor> ACCESSOR_LIST = List.of(new MapAccessor());

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String parse(String expressionString, Map sourceMap) {
		Expression expression = PARSER.parseExpression(expressionString,
				expressionString.contains(CONTEXT.getExpressionPrefix()) ? CONTEXT : null);
		StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
		evaluationContext.setPropertyAccessors(ACCESSOR_LIST);
		evaluationContext.setRootObject(sourceMap);
		evaluationContext.setVariables(sourceMap);
		return String.valueOf(expression.getValue(evaluationContext));
	}
}
