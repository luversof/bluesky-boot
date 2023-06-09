package io.github.luversof.boot.autoconfigure.security.crypto.encrypt.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckController;
import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckDescription;
import io.github.luversof.boot.security.crypto.factory.BlueskyTextEncryptorFactories;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@DevCheckController
@RestController
@RequestMapping(value = "${bluesky-boot.dev-check.path-prefix}/blueskyBoot/textEncrypt", produces = MediaType.APPLICATION_JSON_VALUE)
public class TextEncryptDevCheckController {

	
	@DevCheckDescription("encrypt")
	@GetMapping("/encrypt")
	public String encrypt(String text) {
		return BlueskyTextEncryptorFactories.getTextEncryptor().encrypt(text);
	}
	
	@DevCheckDescription("decrypt")
	@GetMapping("/decrypt")
	public String decrypt(String text) {
		return BlueskyTextEncryptorFactories.getTextEncryptor().decrypt(text);
	}
}
