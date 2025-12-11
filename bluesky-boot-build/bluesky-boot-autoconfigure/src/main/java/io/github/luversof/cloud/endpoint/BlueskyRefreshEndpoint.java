package io.github.luversof.cloud.endpoint;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

import io.github.luversof.cloud.context.refresh.BlueskyPropertiesRefresher;

@Endpoint(id = "blueskyRefresh")
public class BlueskyRefreshEndpoint {

	private static final Logger log = LoggerFactory.getLogger(BlueskyRefreshEndpoint.class);

	private final BlueskyPropertiesRefresher blueskyPropertiesRefresher;

	public BlueskyRefreshEndpoint(BlueskyPropertiesRefresher blueskyPropertiesRefresher) {
		this.blueskyPropertiesRefresher = blueskyPropertiesRefresher;
	}

	@WriteOperation
	public Collection<String> refresh() {
		Set<String> keys = this.blueskyPropertiesRefresher.refresh();
		log.info("Refreshed keys : {}", keys);
		return keys;
	}

}
