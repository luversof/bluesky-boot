package net.luversof.boot.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProfileInfo {

	public static final String OP_DEV = "opdev";
	public static final String LOCAL_DEV = "localdev";
	public static final String DOCKER_DEV = "dockerdev";

	public static final String[] NET_PROFILES = { OP_DEV, LOCAL_DEV, DOCKER_DEV };
}
