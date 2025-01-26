package io.github.luversof.cloud.endpoint;

import java.util.Collection;
import java.util.Set;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import io.github.luversof.cloud.context.refresh.BlueskyPropertiesRefresher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Endpoint(id = "blueskyRefresh")
public class RefreshEndpoint {

	private final BlueskyPropertiesRefresher blueskyPropertiesRefresher;
	
	public RefreshEndpoint(BlueskyPropertiesRefresher blueskyPropertiesRefresher) {
		this.blueskyPropertiesRefresher = blueskyPropertiesRefresher;
	}
	
	@WriteOperation
	public Collection<String> refresh() {
		Set<String> keys = this.blueskyPropertiesRefresher.refresh();
		log.info("Refreshed keys : {}", keys);
		return keys;
	}
	
}
