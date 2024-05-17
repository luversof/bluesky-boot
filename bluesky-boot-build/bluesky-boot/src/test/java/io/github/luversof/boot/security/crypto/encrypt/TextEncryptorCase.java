package io.github.luversof.boot.security.crypto.encrypt;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TextEncryptorCase {

	TEXT(Encryptors.text("pass", "8560b4f4b3")),
	DELUX(Encryptors.delux("pass", "8560b4f4b3")),
	NO_OP_TEXT(Encryptors.noOpText()),
	DELEGATING_TEXT_ENCRYPTOR(TextEncryptorFactories.createDelegatingTextEncryptor())
	;
	
	@Getter
	private TextEncryptor encryptor;	
}
