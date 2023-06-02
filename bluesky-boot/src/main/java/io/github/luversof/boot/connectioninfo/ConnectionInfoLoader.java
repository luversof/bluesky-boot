package io.github.luversof.boot.connectioninfo;

@FunctionalInterface
public interface ConnectionInfoLoader<T, U extends ConnectionInfoCollector<T>> {

	U load();

}
