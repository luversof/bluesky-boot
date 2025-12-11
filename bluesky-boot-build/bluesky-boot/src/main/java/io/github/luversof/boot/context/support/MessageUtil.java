package io.github.luversof.boot.context.support;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

public final class MessageUtil {

	private MessageUtil() {
	}

	private static MessageSourceAccessor messageSourceAccessor;

	public static void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
		MessageUtil.messageSourceAccessor = messageSourceAccessor;
	}

	public static String getMessage(String code, String defaultMessage) {
		if (messageSourceAccessor == null) {
			return defaultMessage;
		}
		return messageSourceAccessor.getMessage(code, defaultMessage);
	}

	public static String getMessage(String code) {
		return getMessage(code, "");
	}

	public static String getMessage(MessageSourceResolvable resolvable) {
		if (messageSourceAccessor == null) {
			return "";
		}
		return messageSourceAccessor.getMessage(resolvable);
	}

}
