package net.luversof.boot.autoconfigure.core.devcheck.controller;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import lombok.Data;
import net.luversof.boot.annotation.DevCheckDescription;
import net.luversof.boot.autoconfigure.core.config.CoreProperties;
import net.luversof.boot.autoconfigure.core.util.DevCheckUtil;
import net.luversof.boot.config.BlueskyCoreModuleProperties.PathForwardProperties;
import net.luversof.boot.context.BlueskyContextHolder;
import net.luversof.boot.util.RequestAttributeUtil;

@Controller
@RequestMapping(value = "/_check")
public class DevCheckViewController {

	private ApplicationContext applicationContext;
	
	private Reflections reflections;
	
	public DevCheckViewController(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@GetMapping({"", "/index"})
	public String index(ModelMap modelMap) {
		PathForwardProperties pathForwardProperties = BlueskyContextHolder.getContext().getModule(CoreProperties.class).getDomain().getPathForward();
		
		Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods().entrySet().stream()
				.filter(handlerMapping -> 
					handlerMapping.getValue().getBean().toString().toLowerCase().contains("devcheckcontroller")
						&& handlerMapping.getKey().getPatternsCondition().getPatterns().stream().anyMatch(pattern -> pattern.startsWith("/_check")) 
						&& handlerMapping.getKey().getProducesCondition().getExpressions().stream().anyMatch(mediaTypeExpression -> mediaTypeExpression.getMediaType().equals(MediaType.APPLICATION_JSON)))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		
		List<DevCheckInfo> devCheckInfoList = new ArrayList<>();
		handlerMethodMap.entrySet().forEach(map -> {
			if ((!map.getValue().hasMethodAnnotation(DevCheckDescription.class) || (map.getValue().hasMethodAnnotation(DevCheckDescription.class) && map.getValue().getMethodAnnotation(DevCheckDescription.class).displayable())))
				devCheckInfoList.add(new DevCheckInfo(pathForwardProperties, map));
			});
		modelMap.addAttribute("devCheckInfoList", devCheckInfoList.stream().sorted(Comparator.comparing(DevCheckInfo::getBeanName).thenComparing(devCheckInfo-> devCheckInfo.getUrlList().get(0))).collect(Collectors.toList()));
		return "_check/index";
	}
	
	
	@Data
	public static class DevCheckInfo {
		
		public DevCheckInfo(PathForwardProperties pathForwardProperties, Entry<RequestMappingInfo, HandlerMethod> handlerMethodMap) {
			this.beanName = handlerMethodMap.getValue().getBean().toString().replace("DevCheckController", "");
			this.urlList = new ArrayList<>();
			for (String url : handlerMethodMap.getKey().getPatternsCondition().getPatterns()) {
				urlList.add(DevCheckUtil.getUrlWithParameter(pathForwardProperties, url, handlerMethodMap.getValue().getMethod()));
			}
			this.handlerMethodMap = handlerMethodMap;
			if (handlerMethodMap.getValue().hasMethodAnnotation(DevCheckDescription.class))	{
				this.description = handlerMethodMap.getValue().getMethodAnnotation(DevCheckDescription.class).value();
			}
		}
		
		private String beanName;
		private List<String> urlList;
		private String description;
		Entry<RequestMappingInfo, HandlerMethod> handlerMethodMap;
	}
	
	@GetMapping("/util")
	public String util(ModelMap modelMap) {
		if (reflections == null) {
			reflections = new Reflections("com.luversof", "com.bluesky");	// 이 부분 때문에 util 리스트를 쓰는 건 고민을 해봐야 함. 사용 패키지를 세팅할 수 있을지 고민 필요
		}
		
		Set<Class<? extends RequestAttributeUtil>> utilSet = reflections.getSubTypesOf(RequestAttributeUtil.class);
		modelMap.addAttribute("utilSet", utilSet);
		
		List<DevCheckUtilInfo> devCheckUtilInfoList = new ArrayList<>();
		utilSet.stream().forEach(util -> devCheckUtilInfoList.add(new DevCheckUtilInfo(util)));
		modelMap.addAttribute("devCheckUtilInfoList", devCheckUtilInfoList);
		for (Class<? extends RequestAttributeUtil> util : utilSet) {
			Method[] declaredMethods = util.getDeclaredMethods();
			
			for (Method method : declaredMethods) {
				method.getName();
			}
			
		}
		return "_check/util";
	}
	
	@Data
	public static class DevCheckUtilInfo {
		
		public DevCheckUtilInfo(Class<? extends RequestAttributeUtil> util) {
			this.methodName = util.getSimpleName();
			this.methodInfoList = new ArrayList<>();
			Arrays.asList(util.getDeclaredMethods()).stream().filter(method -> Modifier.isPublic(method.getModifiers()) && (!AnnotatedElementUtils.hasAnnotation(method, DevCheckDescription.class) || (AnnotatedElementUtils.hasAnnotation(method, DevCheckDescription.class) && AnnotatedElementUtils.findMergedAnnotation(method, DevCheckDescription.class).displayable()))).forEach(method -> {
				this.methodInfoList.add(new DevCheckUtilMethodInfo(method));
			});
			
		}
		
		private String methodName;
		private List<DevCheckUtilMethodInfo> methodInfoList;
	}
	
	@Data
	public static class DevCheckUtilMethodInfo {
		
		public DevCheckUtilMethodInfo(Method method) {
			this.method = method.getName();
			this.parameterNames = DevCheckUtil.getParameterNames(method);
			this.returnType =  method.getReturnType().getSimpleName(); 
			
			if (AnnotatedElementUtils.hasAnnotation(method, DevCheckDescription.class)) {
				this.description = AnnotatedElementUtils.findMergedAnnotation(method, DevCheckDescription.class).value();
			}
		}
		private String description;
		private String returnType;
		private String method;
		private String[] parameterNames;
		
	}
}
