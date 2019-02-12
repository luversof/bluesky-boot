package net.luversof.boot.autoconfigure.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bluesky-boot")
public class MessageSourceExtensionProperties {
	private Map<String, List<String>> messageSource = new HashMap<>();
}
