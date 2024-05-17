package io.github.luversof.boot.security.crypto.encrypt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class TextEncryptorTests {

	@ParameterizedTest
	@EnumSource(value = TextEncryptorCase.class)
	void encryptTest(TextEncryptorCase textEncryptorCase) {
		var text = "test text!!!";
		var encryptor = textEncryptorCase.getEncryptor();
		var encryptText = encryptor.encrypt(text);
		log.debug("encryptor : {}", textEncryptorCase.name());
		log.debug("encryptText : {}", encryptText);
		var decryptText = encryptor.decrypt(encryptText);
		log.debug("decryptText : {}, {}", text.equals(decryptText), decryptText);
		
		assertThat(text).isEqualTo(decryptText);
	}
	
	@Test
	void collectionPropertyPatternTest() {
		var pattern = Pattern.compile("(\\S+)?\\[(\\d+)\\](\\.\\S+)?");
		String encryptText = "{text}2ce098a348636f59a31ee83e7192a891a4f5cb58b34f7ecd1f77d6d058c1b1b6";
		
		
		boolean matches = pattern.matcher(encryptText).matches();
		
		log.debug("matches : {}", matches);
		
	}
}
