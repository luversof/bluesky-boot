package net.luversof.boot.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum BlueskyErrorPage implements ErrorPage {
	
	DEFAULT("support/defaultError"),
	SIMPLE("support/simpleError"),
	NOT_SUPPORTED("support/notSupported");
	
	@Getter private String viewName;
}
