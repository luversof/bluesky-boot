package io.github.luversof.boot.uuid;

import java.util.UUID;

import io.github.luversof.boot.context.ApplicationContextUtil;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UuidGeneratorUtil {
	
	public static UUID getUuid() {
		return ApplicationContextUtil.getApplicationContext().getBean(UuidGenerator.class).create();
	}

}
