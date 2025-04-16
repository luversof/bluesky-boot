package io.github.luversof.boot.autoconfigure.security.crypto.encrypt;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.security.crypto.encrypt.DelegatingTextEncryptor;
import io.github.luversof.boot.security.crypto.encrypt.TextEncryptDevCheckController;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for TextEncrypt support.
 * @author bluesky
 *
 */
@AutoConfiguration("blueskyBootTextEncryptAutoConfiguration")
@ConditionalOnClass({ DelegatingTextEncryptor.class, DevCheckController.class})
@ConditionalOnProperty(prefix = "bluesky-boot.dev-check", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TextEncryptAutoConfiguration {

	@Bean
	TextEncryptDevCheckController textEncryptDevCheckController() {
		return new TextEncryptDevCheckController();
	}
}
