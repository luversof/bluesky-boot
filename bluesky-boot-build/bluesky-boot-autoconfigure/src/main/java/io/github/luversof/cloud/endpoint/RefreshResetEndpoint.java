package io.github.luversof.cloud.endpoint;

import java.util.Collection;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.util.SerializationUtils;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyRefreshProperties;
import io.github.luversof.boot.core.CoreBaseProperties;
import io.github.luversof.boot.core.CoreModuleProperties;
import io.github.luversof.boot.core.CoreProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Endpoint(id = "blueskyRefreshReset")
public class RefreshResetEndpoint {

	@WriteOperation
	public Collection<String> refresh() throws BeansException, Exception {

		var applicationContext = ApplicationContextUtil.getApplicationContext();
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		
		
		// 최초 로드된 properties 설정 복원
		// TODO 순서 처리 필요함 (BlueskyProperties(특히 CoreBaseProperties 제일 먼저), BlueskyModuleProperties 순서로 처리)
		BlueskyBootContextHolder.getContext().getInitialLoadedBlueskyResfreshPropertiesMap().forEach((key, value) -> {
			log.debug("restore properties : {}", key);
			var targetProperties = applicationContext.getBean(key, BlueskyRefreshProperties.class);
			BeanUtils.copyProperties(SerializationUtils.clone(value), targetProperties);
		});
		
		blueskyBootContext.clear();
		applicationContext.getBean(CoreBaseProperties.class).afterPropertiesSet();
		applicationContext.getBean(CoreProperties.class).afterPropertiesSet();
		applicationContext.getBean(CoreModuleProperties.class).afterPropertiesSet();

		
		return blueskyBootContext.getInitialLoadedBlueskyResfreshPropertiesMap().keySet();
	}
	
}
