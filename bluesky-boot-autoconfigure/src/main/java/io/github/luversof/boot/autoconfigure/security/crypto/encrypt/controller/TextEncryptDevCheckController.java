package io.github.luversof.boot.autoconfigure.security.crypto.encrypt.controller;

import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
@RequestMapping(value = "${bluesky-boot.dev-check.path-prefix}/blueskyBoot/textEncrypt", produces = MediaType.APPLICATION_JSON_VALUE)
public class TextEncryptDevCheckController {
	
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
