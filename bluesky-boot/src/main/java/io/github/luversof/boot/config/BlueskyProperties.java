package io.github.luversof.boot.config;

import java.util.Map;

public interface BlueskyProperties<T> {

	Map<String, T> getModules();

}
