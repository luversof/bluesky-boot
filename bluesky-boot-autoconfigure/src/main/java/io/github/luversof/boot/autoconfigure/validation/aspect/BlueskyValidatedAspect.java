package io.github.luversof.boot.autoconfigure.validation.aspect;

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import io.github.luversof.boot.autoconfigure.validation.annotation.BlueskyValidated;


@Aspect
public class BlueskyValidatedAspect {
	
	private final Validator validator;
	
	public BlueskyValidatedAspect(Validator validator) {
		this.validator = validator;
	}
	
	@Around(value = "execution(* *(.., @io.github.luversof.boot.autoconfigure.validation.annotation.BlueskyValidated (*), ..))")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        
        var parameters = method.getParameters();
        var targetIndex = -1;
        BlueskyValidated targetAnnotation = null;
        for (int i = 0; i < method.getParameters().length ; i++) {
        	var annotation = parameters[i].getAnnotation(BlueskyValidated.class);
        	if (annotation != null) {
        		targetIndex = i;
        		targetAnnotation = annotation;
        		break;
        	}
        }
        var targetObject = joinPoint.getArgs()[targetIndex];
        
		Set<ConstraintViolation<Object>> result = validator.validate(targetObject, targetAnnotation.value());
		
		if (!result.isEmpty()) {
			throw new ConstraintViolationException(result);
		}
		return joinPoint.proceed();
	}

}
