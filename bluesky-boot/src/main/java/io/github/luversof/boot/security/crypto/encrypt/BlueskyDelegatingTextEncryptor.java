package io.github.luversof.boot.security.crypto.encrypt;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.util.CollectionUtils;

import io.github.luversof.boot.exception.BlueskyException;
import lombok.extern.slf4j.Slf4j;

/**
 * 암복호화 처리를 복수로 사용할 수 있도록 제공하기 위해 사용
 * 암호화된 text의 경우 prefix로 {암호화키}값  의 형태로 복호화 대상 textEncryptor를 추즉할 수 있지만
 * 복호화의 경우 별도의 지정이 없다면 여러 encryptor중 기본 사용 encryptor가 지정되어 있어야 한고 해당 encryptor로 암호화를 처리한다.
 * Spring 
 * @author bluesky
 *
 */
@Slf4j
public class BlueskyDelegatingTextEncryptor implements TextEncryptor {
	
	private static final String DEFAULT_ID_PREFIX = "{";

	private static final String DEFAULT_ID_SUFFIX = "}";
	
	private final String idPrefix;
	
	private final String idSuffix;
	
	private final String defaultTextEncryptorId;
	
	private final Map<String, TextEncryptor> textEncryptorMap;
	
	public BlueskyDelegatingTextEncryptor(String defaultTextEncryptorId, Map<String, TextEncryptor> textEncryptorMap) {
		this(defaultTextEncryptorId, textEncryptorMap, DEFAULT_ID_PREFIX, DEFAULT_ID_SUFFIX);
	}
	
	public BlueskyDelegatingTextEncryptor(String defaultTextEncryptorId, Map<String, TextEncryptor> textEncryptorMap, String idPrefix, String idSuffix) {
		
		if (defaultTextEncryptorId == null) {
			throw new BlueskyException("defaultId cannot be null");
		}
		
		if (idPrefix == null) {
			throw new BlueskyException("prefix cannot be null");
		}
		
		if (idSuffix == null || idSuffix.isEmpty()) {
			throw new BlueskyException("suffix cannot be empty");
		}
		
		if (idPrefix.contains(idSuffix)) {
			throw new BlueskyException("idPrefix " + idPrefix + " cannot contain idSuffix " + idSuffix);
		}
		
		if (!textEncryptorMap.containsKey(defaultTextEncryptorId)) {
			throw new BlueskyException("defaultTextEncryptorId " + defaultTextEncryptorId + "is not found in textEncryptorMap" + textEncryptorMap);
		}
		
		for (String id : textEncryptorMap.keySet()) {
			if (id == null) {
				continue;
			}
			if (!idPrefix.isEmpty() && id.contains(idPrefix)) {
				throw new BlueskyException("id " + id + " cannot contain " + idPrefix);
			}
			if (id.contains(idSuffix)) {
				throw new BlueskyException("id " + id + " cannot contain " + idSuffix);
			}
		}
		
		this.defaultTextEncryptorId = defaultTextEncryptorId;
		this.textEncryptorMap = new HashMap<>(textEncryptorMap);
		this.idPrefix = idPrefix;
		this.idSuffix = idSuffix;
	}

	@Override
	public String encrypt(String text) {
		return this.idPrefix + this.defaultTextEncryptorId + this.idSuffix + this.textEncryptorMap.get(this.defaultTextEncryptorId).encrypt(text);
	}
	
	public String encrypt(String textEncryptorId, String text) {
		return this.idPrefix + textEncryptorId + this.idSuffix + this.textEncryptorMap.get(textEncryptorId).encrypt(text);
	}

	
	@Override
	public String decrypt(String encryptedText) {
		
		String textEncryptorId = extractTextEncryptorId(encryptedText);
		if (textEncryptorId == null) {
			return encryptedText;
		}
		
		if (!this.textEncryptorMap.containsKey(textEncryptorId)) {
			return encryptedText;
		}
		
		return this.textEncryptorMap.get(textEncryptorId).decrypt(extractEncryptedText(encryptedText));
	}
	
	public boolean isEncrypted(String encryptedText) {
		return extractTextEncryptorId(encryptedText) != null;
	}
	
	private String extractTextEncryptorId(String encryptedText) {
		if (encryptedText == null) {
			return null;
		}
		int start = encryptedText.indexOf(this.idPrefix);
		if (start != 0) {
			return null;
		}
		
		int end = encryptedText.indexOf(this.idSuffix, start);
		if (end < 0) {
			return null;
		}
		
		var textEncryptorId = encryptedText.substring(start + this.idPrefix.length(), end);
		if (!this.textEncryptorMap.containsKey(textEncryptorId)) {
			log.debug("The textEncryptorId does not exist in the textEncryptorMap : {}", textEncryptorId);
			return null;
		}
		return textEncryptorId;
	}
	
	private String extractEncryptedText(String encryptedText) {
		if (encryptedText == null) {
			return encryptedText;
		}
		int start = encryptedText.indexOf(this.idPrefix);
		if (start != 0) {
			return encryptedText;
		}
		
		int end = encryptedText.indexOf(this.idSuffix, start);
		if (end < 0) {
			return encryptedText;
		}
		
		return encryptedText.substring(end + 1);
	}
	
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(this.textEncryptorMap);
	}
	

}
