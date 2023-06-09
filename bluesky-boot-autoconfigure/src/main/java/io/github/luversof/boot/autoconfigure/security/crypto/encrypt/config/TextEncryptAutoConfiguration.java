package io.github.luversof.boot.autoconfigure.security.crypto.encrypt.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import io.github.luversof.boot.autoconfigure.security.crypto.encrypt.controller.TextEncryptDevCheckController;

@AutoConfiguration(value = "_blueskyBootSecurityCryptoTextEncryptAutoConfiguration")
public class TextEncryptAutoConfiguration {

	@Bean
	public TextEncryptDevCheckController textEncryptDevCheckController() {
		return new TextEncryptDevCheckController();
	}
}
