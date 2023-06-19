package io.github.luversof.boot.autoconfigure.security.crypto.encrypt;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

import io.github.luversof.boot.autoconfigure.security.crypto.encrypt.controller.TextEncryptDevCheckController;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for TextEncrypt support.
 * @author bluesky
 *
 */
@AutoConfiguration(value = "_blueskyBootSecurityCryptoTextEncryptAutoConfiguration")
public class TextEncryptAutoConfiguration {

	@Bean
	TextEncryptDevCheckController textEncryptDevCheckController() {
		return new TextEncryptDevCheckController();
	}
}
