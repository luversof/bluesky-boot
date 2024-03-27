package io.github.luversof.boot.security.crypto.encrypt;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;
import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;
import lombok.AllArgsConstructor;

/**
 * {@link DevCheckController} for TextEncrypt support.
 * @author bluesky
 *
 */
@AllArgsConstructor
@DevCheckController
public class TextEncryptDevCheckController {
	
	private static final String PATH_PREFIX = "/blueskyBoot/security/crypto";
	
	@DevCheckDescription("encrypt")
	@GetMapping(PATH_PREFIX + "/encrypt")
	String encrypt(@RequestParam(required = false) String textEncryptorId, String text) {
		if (textEncryptorId == null) {
			return TextEncryptorFactories.getDelegatingTextEncryptor().encrypt(text);
		}
		return TextEncryptorFactories.getDelegatingTextEncryptor().encrypt(textEncryptorId, text);
	}
	
	@DevCheckDescription("decrypt")
	@GetMapping(PATH_PREFIX + "/decrypt")
	String decrypt(String text) {
		return TextEncryptorFactories.getDelegatingTextEncryptor().decrypt(text);
	}
	
	@DevCheckDescription("textEncryptorMapKeySet")
	@GetMapping(PATH_PREFIX + "/textEncryptorMapKeySet")
	Set<String> textEncryptorMapKeySet() {
		return TextEncryptorFactories.getDelegatingTextEncryptor().textEncryptorMapKeySet();
	}
}
