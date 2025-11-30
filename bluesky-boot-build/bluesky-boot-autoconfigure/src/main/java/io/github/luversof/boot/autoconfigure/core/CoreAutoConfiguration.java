package io.github.luversof.boot.autoconfigure.core;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.github.luversof.boot.context.event.ServerPortLogApplicationListener;
import io.github.luversof.boot.core.CoreConfiguration;


/**
 * {@link EnableAutoConfiguration Auto-configuration} for Core support.
 * @author bluesky
 *
 */
@AutoConfiguration("blueskyBootCoreAutoConfiguration")
@Import(CoreConfiguration.class)
public class CoreAutoConfiguration {
	

	@Bean
	ServerPortLogApplicationListener serverPortLogApplicationListener() {
		return new ServerPortLogApplicationListener();
	}

}