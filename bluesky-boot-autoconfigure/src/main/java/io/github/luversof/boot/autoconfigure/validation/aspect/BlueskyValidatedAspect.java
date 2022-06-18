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
import org.springframework.util.ClassUtils;

import io.github.luversof.boot.autoconfigure.validation.annotation.BlueskyValidated;
import io.github.luversof.boot.exception.BlueskyException;
import lombok.AllArgsConstructor;
import lombok.Data;


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
        
        var targetClass = joinPoint.getTarget().getClass();
        MethodParameterInfo methodParameterInfo = getMethofParameterInfoFromAll(targetClass, method.getName(), method.getParameterTypes());
        
        if (methodParameterInfo == null) {
        	throw new BlueskyException("can't find method parameter annotation.");
        }
        
        var targetObject = joinPoint.getArgs()[methodParameterInfo.getIndex()];
        
		Set<ConstraintViolation<Object>> result = validator.validate(targetObject, methodParameterInfo.getAnnotation().value());
		
		if (!result.isEmpty()) {
			throw new ConstraintViolationException(result);
		}
		return joinPoint.proceed();
	}
	
	private MethodParameterInfo getMethofParameterInfoFromAll(Class<?> clazz, String methodName, Class<?>... paramTypes) {
		var methodParameterInfo = getMethodParameterInfo(clazz, methodName, paramTypes);
		 
		if (methodParameterInfo != null) {
			return methodParameterInfo;
		}

    	// super class 검색
    	if (clazz.getSuperclass() != null) {
    		methodParameterInfo = getMethofParameterInfoFromAll(clazz.getSuperclass(), methodName, paramTypes);
    	}
    	
    	if (methodParameterInfo != null) {
			return methodParameterInfo;
		}
    	
	    // interface 검색
	    if (clazz.getInterfaces().length > 0) {
	    	for (Class<?> iClazz : clazz.getInterfaces()) {
	    		methodParameterInfo = getMethofParameterInfoFromAll(iClazz, methodName, paramTypes);
	    		if (methodParameterInfo != null) {
	    			break;
	    		}
	    	}
	    }
	    return methodParameterInfo;
	}
	
	private MethodParameterInfo getMethodParameterInfo(Class<?> clazz, String methodName, Class<?>... paramTypes) {
		var targetMethod = ClassUtils.getMethodIfAvailable(clazz, methodName, paramTypes);
		if (targetMethod != null) {
			for (int i = 0; i < targetMethod.getParameters().length;  i++ ) {
				var annotation = targetMethod.getParameters()[i].getAnnotation(BlueskyValidated.class);
				if (annotation != null) {
					return new MethodParameterInfo(i, annotation);
				}		
			}
		}
		return null;
	}
	
	@Data
	@AllArgsConstructor
	public static class MethodParameterInfo {
		private int index;
		private BlueskyValidated annotation;
	}

	// 해당 클래스의 
}
