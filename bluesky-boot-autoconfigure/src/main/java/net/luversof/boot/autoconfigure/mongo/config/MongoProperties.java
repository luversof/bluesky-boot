package net.luversof.boot.autoconfigure.mongo.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 굳이 module 구성 할 이유가 없어보이는데?
 * @author bluesky
 *
 */
@Data
@ConfigurationProperties(prefix = "bluesky-modules.mongo")
public class MongoProperties {

	private BlueskyMongoProperties defaultProperties;
	
	private Map<String, BlueskyMongoProperties> connectionMap = new HashMap<>();

	@Data
	@EqualsAndHashCode(callSuper = true)
	@Builder
	public static class BlueskyMongoProperties extends org.springframework.boot.autoconfigure.mongo.MongoProperties {

	}
}
