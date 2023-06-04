package io.github.luversof.boot.security.crypto.encrypt;

import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * Spring Security Crypto에서 제공하는 TextEncryptor를 편하게 사용하기 위해 decorate 처리
 * @author bluesky
 *
 */
public interface BlueskyTextEncryptor extends TextEncryptor {

}
