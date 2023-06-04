package io.github.luversof.boot.security.crypto.factory;

import java.util.HashMap;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import io.github.luversof.boot.security.crypto.encrypt.BlueskyDelegatingTextEncryptor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlueskyTextEncryptorFactories {

	public static TextEncryptor createDelegatingTextEncrpyptor() {
		var encryptors = new HashMap<String, TextEncryptor>();
		encryptors.put("text", Encryptors.text("pass", "8560b4f4b3"));
		encryptors.put("delux", Encryptors.delux("pass", "8560b4f4b3"));
		return new BlueskyDelegatingTextEncryptor("text", encryptors);
	}
}
