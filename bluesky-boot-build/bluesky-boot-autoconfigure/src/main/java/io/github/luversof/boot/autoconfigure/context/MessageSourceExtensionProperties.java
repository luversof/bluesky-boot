package io.github.luversof.boot.autoconfigure.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for MessageSource.
 */
@ConfigurationProperties(prefix = "bluesky-boot")
public class MessageSourceExtensionProperties {
	private Map<String, List<String>> messageSource = new HashMap<>();

	public Map<String, List<String>> getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(Map<String, List<String>> messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		MessageSourceExtensionProperties that = (MessageSourceExtensionProperties) o;
		return Objects.equals(messageSource, that.messageSource);
	}

	@Override
	public int hashCode() {
		return Objects.hash(messageSource);
	}

	@Override
	public String toString() {
		return "MessageSourceExtensionProperties{" +
				"messageSource=" + messageSource +
				'}';
	}
}
