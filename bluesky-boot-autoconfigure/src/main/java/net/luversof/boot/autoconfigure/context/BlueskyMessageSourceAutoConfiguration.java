package net.luversof.boot.autoconfigure.context;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import lombok.Setter;
import net.luversof.context.support.BlueskyReloadableResourceBundleMessageSource;

@Configuration
@ConditionalOnMissingBean(value = MessageSource.class, search = SearchStrategy.CURRENT)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "bluesky")
public class BlueskyMessageSourceAutoConfiguration {
	
	@Setter
	private Map<String, List<String>> messageSource;
	
	@Autowired
	private Environment envirionment;
	
	private String getBasename() {
		return envirionment.getProperty("spring.messages.basename", "messages");
	}
	
	private Charset getEncoding() {
		return Charset.forName(envirionment.getProperty("spring.messages.encoding", "UTF-8"));
	}
	
	private boolean isFallbackToSystemLocale() {
		if (envirionment.containsProperty("spring.messages.fallback-to-system-locale")) {
			return Boolean.valueOf(envirionment.getProperty("spring.messages.fallback-to-system-locale"));
		}
		return true;
	}
	
	private int getCacheSeconds() {
		return Integer.valueOf(envirionment.getProperty("spring.messages.cache-seconds", "-1"));
	}
	
	private boolean isAlwaysUseMessageFormat() {
		return Boolean.valueOf(envirionment.getProperty("spring.messages.always-use-message-format", "false"));
	}
	
	
	@Bean
	public MessageSource messageSource() {
		AbstractResourceBasedMessageSource messageSource = new BlueskyReloadableResourceBundleMessageSource();
		
		List<String> baseNameList = new ArrayList<>();
		
		if (StringUtils.hasText(getBasename())) {
			baseNameList.addAll(Arrays.asList(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(getBasename()))));
		}
		this.messageSource.values().stream().forEach(value -> baseNameList.addAll(value));
		messageSource.setBasenames(baseNameList.toArray(new String[0]));
		messageSource.setDefaultEncoding(getEncoding().name());
		messageSource.setFallbackToSystemLocale(isFallbackToSystemLocale());
		messageSource.setCacheSeconds(getCacheSeconds());
		messageSource.setAlwaysUseMessageFormat(isAlwaysUseMessageFormat());
		return messageSource;
	}
			
}
