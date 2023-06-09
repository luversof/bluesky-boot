package io.github.luversof.boot.security.crypto.factory;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import io.github.luversof.boot.security.crypto.encrypt.BlueskyDelegatingTextEncryptor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlueskyTextEncryptorFactories {
	
	private static TextEncryptor textEncryptor;
	
	private static String defaultTextEncryptorId = "text";
	
	public static TextEncryptor getTextEncryptor() {
		if (textEncryptor == null) {
			createDelegatingTextEncryptor();
		}
		
		return textEncryptor;
	}

	public static TextEncryptor createDelegatingTextEncryptor() {
		var textEncryptorMap = new HashMap<String, TextEncryptor>();
		textEncryptorMap.putAll(getDefaultTextEncryptorMap());
		textEncryptor = new BlueskyDelegatingTextEncryptor(defaultTextEncryptorId, textEncryptorMap);
		return textEncryptor;
	}
	
	public static TextEncryptor createDelegatingTextEncryptor(Map<String, TextEncryptor> textEncryptorMap) {
		textEncryptorMap = new HashMap<>(textEncryptorMap);
		textEncryptorMap.putAll(getDefaultTextEncryptorMap());
		textEncryptor = new BlueskyDelegatingTextEncryptor(defaultTextEncryptorId, textEncryptorMap);
		return textEncryptor;
	}
	
	private static Map<String, TextEncryptor> getDefaultTextEncryptorMap() {
		var textEncryptorMap = new HashMap<String, TextEncryptor>();
		textEncryptorMap.put("text", Encryptors.text("pass", "8560b4f4b3"));
		textEncryptorMap.put("delux", Encryptors.delux("pass", "8560b4f4b3"));
		return textEncryptorMap;
	}
}
