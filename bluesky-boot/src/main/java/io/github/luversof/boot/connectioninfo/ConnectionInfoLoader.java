package io.github.luversof.boot.connectioninfo;

import java.util.Map;

@FunctionalInterface
public interface ConnectionInfoLoader<T> {

	Map<String, T> load();

}
