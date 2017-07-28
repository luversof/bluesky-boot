package net.luversof.boot.autoconfigure.context;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUtil {

	@Setter
	private static MessageSourceAccessor messageSourceAccessor;
	
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
