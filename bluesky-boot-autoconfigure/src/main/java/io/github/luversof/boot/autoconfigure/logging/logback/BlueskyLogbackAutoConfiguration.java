package io.github.luversof.boot.autoconfigure.logging.logback;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import io.github.luversof.boot.constant.ProfileInfo;
import io.github.luversof.boot.logging.logback.BlueskyLogbackAppender;
import io.github.luversof.boot.logging.logback.BlueskyLogbackAppenderService;

@Configuration(proxyBeanMethods = false)
@Profile( "!" + ProfileInfo.LIVE)
public class BlueskyLogbackAutoConfiguration {

	@Bean
	public BlueskyLogbackAppenderService<ILoggingEvent> blueskyLogbackAppenderService() {
		var blueskyLogbackAppenderService = new BlueskyLogbackAppenderService<ILoggingEvent>();
		addLogbackAppender(blueskyLogbackAppenderService);
		return blueskyLogbackAppenderService;
	}

	private void addLogbackAppender(BlueskyLogbackAppenderService<ILoggingEvent> blueskyLogbackAppenderService) {
		var loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		var appender = (RollingFileAppender<ILoggingEvent>) loggerContext.getLogger("ROOT").getAppender("FILE");
		var blueskyLogbackAppender = new BlueskyLogbackAppender<>(blueskyLogbackAppenderService);
		blueskyLogbackAppender.setContext(loggerContext);
		blueskyLogbackAppender.setName("blueskyLogbackAppender");
		blueskyLogbackAppender.setEncoder(appender == null ? new PatternLayoutEncoder() : appender.getEncoder());

		blueskyLogbackAppender.start();
		loggerContext.getLogger("ROOT").addAppender(blueskyLogbackAppender);
	}
	
	@Bean
	public BlueskyLogbackDevCheckController blueskyLogbackDevCheckController(BlueskyLogbackAppenderService<ILoggingEvent> blueskyLogbackAppenderService) {
		return new BlueskyLogbackDevCheckController(blueskyLogbackAppenderService);
	}
}
