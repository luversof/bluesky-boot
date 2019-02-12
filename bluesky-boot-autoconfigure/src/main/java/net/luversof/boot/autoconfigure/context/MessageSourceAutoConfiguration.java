package net.luversof.boot.autoconfigure.context;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.StringUtils;

import net.luversof.context.support.BlueskyReloadableResourceBundleMessageSource;

@Configuration("_blueskyBootMessageSourceAutoConfiguration")
//@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureBefore(org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration.class)
@EnableConfigurationProperties(MessageSourceExtensionProperties.class)
public class MessageSourceAutoConfiguration {
	
	@Bean
	@ConfigurationProperties(prefix = "spring.messages")
	public MessageSourceProperties messageSourceProperties() {
		return new MessageSourceProperties();
	}
	
	@Bean
	public MessageSource messageSource(MessageSourceProperties messageSourceProperties,  MessageSourceExtensionProperties messageSourceExtensionProperties) {
		AbstractResourceBasedMessageSource messageSource = new BlueskyReloadableResourceBundleMessageSource();
		
		List<String> baseNameList = new ArrayList<>();
		
		if (StringUtils.hasText(messageSourceProperties.getBasename())) {
			baseNameList.addAll(Arrays.asList(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(messageSourceProperties.getBasename()))));
		}
		
		messageSourceExtensionProperties.getMessageSource().values().stream().forEach(value -> baseNameList.addAll(value));
		messageSource.setBasenames(baseNameList.toArray(new String[0]));
		if (messageSourceProperties.getEncoding() != null) {
			messageSource.setDefaultEncoding(messageSourceProperties.getEncoding().name());
		}
		messageSource.setFallbackToSystemLocale(messageSourceProperties.isFallbackToSystemLocale());
		Duration cacheDuration = messageSourceProperties.getCacheDuration();
		if (cacheDuration != null) {
			messageSource.setCacheMillis(cacheDuration.toMillis());
		}
		messageSource.setAlwaysUseMessageFormat(messageSourceProperties.isAlwaysUseMessageFormat());
		messageSource.setUseCodeAsDefaultMessage(messageSourceProperties.isUseCodeAsDefaultMessage());
		return messageSource;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
		MessageUtil.setMessageSourceAccessor(messageSourceAccessor);
		return messageSourceAccessor;
	}
}
