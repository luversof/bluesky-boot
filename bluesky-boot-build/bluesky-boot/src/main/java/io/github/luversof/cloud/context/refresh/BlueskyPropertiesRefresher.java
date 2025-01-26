package io.github.luversof.cloud.context.refresh;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.config.ContextRefreshedWithApplicationEvent;
import org.springframework.cloud.context.refresh.ConfigDataContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.SerializationUtils;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import io.github.luversof.boot.core.BlueskyProperties;
import io.github.luversof.boot.core.BlueskyRefreshProperties;
import io.github.luversof.boot.core.CoreBaseProperties;
import io.github.luversof.boot.core.CoreModuleProperties;
import io.github.luversof.boot.core.CoreProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * actuator endpoint refresh를 약간 수정하여 Environment 변경 적용 후 BrickProperties 갱신 처리
 * refresh endpoint의 ContextRefresher 빈과 중복 선언되면 안되기 때문에 내부 변수로 새로 생성하여 사용
 */
@Slf4j
public class BlueskyPropertiesRefresher implements ApplicationListener<ContextRefreshedWithApplicationEvent> {
	
	private final ConfigurableApplicationContext context;
	
	private ContextRefreshedWithApplicationEvent event;
	
	private ConfigDataContextRefresher configDataContextRefresher;
	
	public BlueskyPropertiesRefresher(ConfigurableApplicationContext context, RefreshScope scope, RefreshAutoConfiguration.RefreshProperties properties) {
		
		this.context = context;
		
		this.configDataContextRefresher = new ConfigDataContextRefresher(context, scope, properties) {
			
			@Override
			public synchronized Set<String> refresh() {
				onApplicationEvent(getEvent());
				
				return refreshEnvironment();
			}

			@Override
			public synchronized Set<String> refreshEnvironment() {
				updateEnvironment();
				return new LinkedHashSet<>();
			}
			
		};
		
	}

	@Override
	public void onApplicationEvent(ContextRefreshedWithApplicationEvent event) {
		this.event = event;
	}
	
	private ContextRefreshedWithApplicationEvent getEvent() {
		return this.event;
	}
	
	public Set<String> refresh() {
		// 최초 properties 설정 복원
		BlueskyBootContextHolder.getContext().getInitialBlueskyResfreshPropertiesMap().forEach((key, value) -> 
			BeanUtils.copyProperties(SerializationUtils.clone(value), context.getBean(key, BlueskyRefreshProperties.class))
		);
		var keys = this.configDataContextRefresher.refresh();
		reloadPropertiesAll(keys);
		return keys;
	}

	/**
	 * blueskyProperties 갱신 처리
	 * 우선 갱신 해야 하는 3개를 먼저 갱신 처리한 후 나머지는 순차 갱신 처리한다.
	 * 만약 더 잘 구성하고 싶다면 bean의 dependency를 참조해서 순서를 구성해야 하지만 참조 빈이 갱신되지 않으므로 이는 추후 개선이 필요하다.
	 * @param keys
	 */
	private void reloadPropertiesAll(Set<String> keys) {
		reloadProperties(CoreBaseProperties.BEAN_NAME, keys);
    	reloadProperties(CoreProperties.BEAN_NAME, keys);
    	reloadProperties(CoreModuleProperties.BEAN_NAME, keys);
    	
    	reloadProperties(BlueskyProperties.class, keys);
    	reloadProperties(BlueskyModuleProperties.class, keys);
	}
	
	private <T extends BlueskyRefreshProperties> void reloadProperties(Class<T> type, Set<String> keys) {
		String[] blueskyPropertiesBeanNames = context.getBeanNamesForType(type);
		for(var beanName : blueskyPropertiesBeanNames) {
			if (beanName.equals(CoreBaseProperties.BEAN_NAME)
				|| beanName.equals(CoreProperties.BEAN_NAME) 
				|| beanName.equals(CoreModuleProperties.BEAN_NAME)) {
				continue;
			}
			reloadProperties(beanName, keys);
		}
	}
	
	@SneakyThrows
    private void reloadProperties(String beanName, Set<String> keys) {
    	var targetPropertiesBean = context.getBean(beanName, BlueskyRefreshProperties.class);
    	var beanFactory = context.getAutowireCapableBeanFactory();
//    	beanFactory.destroyBean(targetPropertiesBean);
    	beanFactory.autowireBean(targetPropertiesBean);
    	beanFactory.initializeBean(targetPropertiesBean, beanName);
    }
}
