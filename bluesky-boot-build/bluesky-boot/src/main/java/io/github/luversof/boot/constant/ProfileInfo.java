package io.github.luversof.boot.constant;

import java.util.List;

public final class ProfileInfo {

	public static final String DEFAULT = "default"; // spring cloud config server profile
	public static final String OP_DEV = "opdev";
	public static final String LOCAL_DEV = "localdev";
	public static final String DOCKER_DEV = "dockerdev";
	public static final String K8S_DEV = "k8sdev";
	public static final String LIVE = "live";

	private static List<String> blueskyBootProfileList;

	private ProfileInfo() {
	}

	public static void setBlueskyBootProfileList(List<String> blueskyBootProfileList) {
		ProfileInfo.blueskyBootProfileList = blueskyBootProfileList;
	}

	public static List<String> getBlueskyBootProfileList() {
		if (blueskyBootProfileList == null) {
			blueskyBootProfileList = List.of(DEFAULT, OP_DEV, LOCAL_DEV, DOCKER_DEV, K8S_DEV, LIVE);
		}
		return blueskyBootProfileList;
	}

}
