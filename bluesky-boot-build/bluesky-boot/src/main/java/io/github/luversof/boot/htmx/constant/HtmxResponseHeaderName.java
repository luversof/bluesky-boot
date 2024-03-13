package io.github.luversof.boot.htmx.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum HtmxResponseHeaderName {
	HX_TRIGGER("HX-Trigger"),
	HX_TRIGGER_AFTER_SETTLE("HX-Trigger-After-Settle"),
	HX_TRIGGER_AFTER_SWAP("HX-Trigger-After-Swap")
	;
	@Getter
	private String headerName;
}
