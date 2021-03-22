package io.github.luversof.boot.autoconfigure.context;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.StringUtils;

import io.github.luversof.boot.context.support.BlueskyReloadableResourceBundleMessageSource;


@Configuration(value = "_blueskyBootMessageSourceAutoConfiguration", proxyBeanMethods = false)
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
		var messageSource = new BlueskyReloadableResourceBundleMessageSource();
		
		var baseNameList = new ArrayList<>();
		
		if (StringUtils.hasText(messageSourceProperties.getBasename())) {
			baseNameList.addAll(Arrays.asList(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(messageSourceProperties.getBasename()))));
		}
		
		messageSourceExtensionProperties.getMessageSource().values().stream().forEach(baseNameList::addAll);
		messageSource.setBasenames(baseNameList.toArray(new String[0]));
		if (messageSourceProperties.getEncoding() != null) {
			messageSource.setDefaultEncoding(messageSourceProperties.getEncoding().name());
		}
		messageSource.setFallbackToSystemLocale(messageSourceProperties.isFallbackToSystemLocale());
		var cacheDuration = messageSourceProperties.getCacheDuration();
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
		var messageSourceAccessor = new MessageSourceAccessor(messageSource);
		MessageUtil.setMessageSourceAccessor(messageSourceAccessor);
		return messageSourceAccessor;
	}
}
