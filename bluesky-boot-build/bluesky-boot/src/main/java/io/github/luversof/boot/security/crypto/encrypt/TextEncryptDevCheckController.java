package io.github.luversof.boot.security.crypto.encrypt;

import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;
import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;

/**
 * {@link DevCheckController} for TextEncrypt support.
 * 
 * @author bluesky
 *
 */
@DevCheckController
@RequestMapping(value = "/blueskyBoot/security/crypto", produces = MediaType.APPLICATION_JSON_VALUE)
public class TextEncryptDevCheckController {

	public TextEncryptDevCheckController() {
	}

	@DevCheckDescription("encrypt")
	@GetMapping("/encrypt")
	String encrypt(@RequestParam(required = false) String textEncryptorId, String text) {
		if (textEncryptorId == null) {
			return TextEncryptorFactories.getDelegatingTextEncryptor().encrypt(text);
		}
		return TextEncryptorFactories.getDelegatingTextEncryptor().encrypt(textEncryptorId, text);
	}

	@DevCheckDescription("decrypt")
	@GetMapping("/decrypt")
	String decrypt(String text) {
		return TextEncryptorFactories.getDelegatingTextEncryptor().decrypt(text);
	}

	@DevCheckDescription("textEncryptorMapKeySet")
	@GetMapping("/textEncryptorMapKeySet")
	Set<String> textEncryptorMapKeySet() {
		return TextEncryptorFactories.getDelegatingTextEncryptor().textEncryptorMapKeySet();
	}
}
