package io.github.luversof.cloud.endpoint;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.util.SerializationUtils;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyRefreshProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Endpoint(id = "blueskyRefresh")
public class RefreshEndpoint {

	private final ContextRefresher contextRefresher;
	
	public RefreshEndpoint(ContextRefresher contextRefresher) {
		this.contextRefresher = contextRefresher;
	}
	
	@WriteOperation
	public Collection<String> refresh() {

		var applicationContext = ApplicationContextUtil.getApplicationContext();
		// 최초 properties 설정 복원
		BlueskyBootContextHolder.getContext().getInitialBlueskyResfreshPropertiesMap().forEach((key, value) -> {
			log.debug("refresh properties : {}", key);
			var targetProperties = applicationContext.getBean(key, BlueskyRefreshProperties.class);
			BeanUtils.copyProperties(SerializationUtils.clone(value), targetProperties);
		});
		
		Set<String> keys = this.contextRefresher.refresh();
		log.info("Refreshed keys : " + keys);
		
		// 실패 시 처리 (실패는 어떻게 판단함? exception이 있나?)
		
		return keys;
	}
	
}
