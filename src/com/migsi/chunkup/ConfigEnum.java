package com.migsi.chunkup;

public enum ConfigEnum {
	VERSION("version"),
	USE_ALTERNATIVE_CHUNKLOADER("use-alternative-chunkloader"),
	IGNORE_INTERVAL("ignore-interval"),
	REFRESH_IN_TICKS("refresh-in-ticks"),
	NEXT_ID("next-id"),
	NEXT_ROUTE("next-route"),
	OWNERS("owners"),
	PERMISSIONS("permissions"),
	OP("op");
	
	private final String value;
	
	ConfigEnum(String value) {
		this.value = value;
	}
	
	String value() {
		return value;
	}
}
