package io.github.luversof.boot.uuid;

import java.util.UUID;

import io.github.luversof.boot.context.ApplicationContextUtil;

public final class UuidGeneratorUtil {

	private UuidGeneratorUtil() {
	}

	public static UUID getUuid() {
		return ApplicationContextUtil.getApplicationContext().getBean(UuidGenerator.class).create();
	}

}
