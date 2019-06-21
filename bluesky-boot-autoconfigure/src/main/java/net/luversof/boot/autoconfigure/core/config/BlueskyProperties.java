package net.luversof.boot.autoconfigure.core.config;

import java.util.Map;

public interface BlueskyProperties<T> {

	Map<String, T> getModules();

}
