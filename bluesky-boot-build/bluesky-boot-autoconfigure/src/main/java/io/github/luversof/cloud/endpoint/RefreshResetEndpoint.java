package io.github.luversof.cloud.endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.util.SerializationUtils;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyRefreshProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Endpoint(id = "blueskyRefreshReset")
public class RefreshResetEndpoint {

	@WriteOperation
	public Collection<String> refresh() {

		var applicationContext = ApplicationContextUtil.getApplicationContext();
		
		List<String> targetList = new ArrayList<>();
		
		// TODO BaseProperties의 afterPropertiesSet을 우선 호출해야 함
		
		// 최초 로드된 properties 설정 복원
		BlueskyBootContextHolder.getContext().getInitialLoadBlueskyResfreshPropertiesMap().forEach((key, value) -> {
			log.debug("restore properties : {}", key);
			var targetProperties = applicationContext.getBean(key, BlueskyRefreshProperties.class);
			BeanUtils.copyProperties(SerializationUtils.clone(value), targetProperties);
			
			targetList.add(key);
		});

		
		return targetList;
	}
	
}
