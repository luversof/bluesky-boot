package io.github.luversof.boot.autoconfigure.context;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.StringUtils;

import io.github.luversof.boot.context.MessageSourceDevCheckController;
import io.github.luversof.boot.context.MessageUtil;
import io.github.luversof.boot.context.support.BlueskyReloadableResourceBundleMessageSource;


/**
 * {@link EnableAutoConfiguration Auto-configuration} for MessageSource support.
 * @author bluesky
 *
 */
@AutoConfiguration(before = org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration.class)
//@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(MessageSourceExtensionProperties.class)
public class MessageSourceAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.messages")
    MessageSourceProperties messageSourceProperties() {
        return new MessageSourceProperties();
    }

    @Bean
    MessageSource messageSource(MessageSourceProperties messageSourceProperties,  MessageSourceExtensionProperties messageSourceExtensionProperties) {
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
    MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
        var messageSourceAccessor = new MessageSourceAccessor(messageSource);
        MessageUtil.setMessageSourceAccessor(messageSourceAccessor);
        return messageSourceAccessor;
    }
    
    @Bean
    MessageSourceDevCheckController messageSourceDevCheckController(BlueskyReloadableResourceBundleMessageSource messageSource) {
    	return new MessageSourceDevCheckController(messageSource);
    }
}
