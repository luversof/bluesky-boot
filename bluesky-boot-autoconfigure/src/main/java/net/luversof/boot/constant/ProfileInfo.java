package net.luversof.boot.constant;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProfileInfo {

	public static final String DEFAULT = "default";	// spring cloud config server profile
	public static final String OP_DEV = "opdev";
	public static final String LOCAL_DEV = "localdev";
	public static final String DOCKER_DEV = "dockerdev";
	public static final String K8S_DEV = "k8sdev";

	public static final List<String> NET_PROFILE_LIST = List.of(DEFAULT, OP_DEV, LOCAL_DEV, DOCKER_DEV, K8S_DEV);
}
