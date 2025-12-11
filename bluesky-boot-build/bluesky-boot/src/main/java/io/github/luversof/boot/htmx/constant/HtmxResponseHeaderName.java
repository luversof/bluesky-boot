package io.github.luversof.boot.htmx.constant;

public enum HtmxResponseHeaderName {
	HX_TRIGGER("HX-Trigger"),
	HX_TRIGGER_AFTER_SETTLE("HX-Trigger-After-Settle"),
	HX_TRIGGER_AFTER_SWAP("HX-Trigger-After-Swap");

	private String headerName;

	HtmxResponseHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public String getHeaderName() {
		return headerName;
	}
}
