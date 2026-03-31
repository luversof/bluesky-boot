package io.github.luversof.boot.uuid;

import io.github.luversof.boot.context.ApplicationContextUtil;
import java.util.UUID;

public final class UuidGeneratorUtil {

    private UuidGeneratorUtil() {}

    public static UUID getUuid() {
        return ApplicationContextUtil.getApplicationContext().getBean(UuidGenerator.class).create();
    }
}
