package io.github.luversof.boot.autoconfigure.context;

import java.util.ArrayList;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.autoconfigure.context.ResourceBasedMessageSourceConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.CollectionUtils;

import io.github.luversof.boot.context.support.BlueskyReloadableResourceBundleMessageSource;
import io.github.luversof.boot.context.support.MessageSourceDevCheckController;
import io.github.luversof.boot.context.support.MessageUtil;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for MessageSource support.
 *
 * @author bluesky
 */
@AutoConfiguration(
    value = "blueskyBootMessageSourceAutoConfiguration",
    before = org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration.class)
// @AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
// Boot 4.2 registers MessageSourceProperties the same way; sharing the bean name keeps one bean
@EnableConfigurationProperties({
  MessageSourceProperties.class,
  MessageSourceExtensionProperties.class
})
public class MessageSourceAutoConfiguration {

  @Bean
  MessageSource messageSource(
      MessageSourceProperties messageSourceProperties,
      MessageSourceExtensionProperties messageSourceExtensionProperties,
      ResourceBasedMessageSourceConfigurer configurer) {
    var messageSource = new BlueskyReloadableResourceBundleMessageSource();
    // spring.messages.* 전체(common-messages 포함)는 Boot 가 적용하고, basename 만 확장분을 합쳐 다시 넣는다
    configurer.configure(messageSource);

    var baseNameList = new ArrayList<>();

    if (!CollectionUtils.isEmpty(messageSourceProperties.getBasename())) {
      baseNameList.addAll(messageSourceProperties.getBasename());
    }

    messageSourceExtensionProperties.getMessageSource().values().forEach(baseNameList::addAll);
    messageSource.setBasenames(baseNameList.toArray(new String[0]));
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
  @ConditionalOnProperty(prefix = "bluesky-boot.dev-check", name = "enabled", havingValue = "true")
  MessageSourceDevCheckController messageSourceDevCheckController(
      BlueskyReloadableResourceBundleMessageSource messageSource) {
    return new MessageSourceDevCheckController(messageSource);
  }
}
