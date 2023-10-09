package io.github.luversof.boot.connectioninfo;

/**
 * interface for calling connection information
 * @author bluesky
 *
 * @param <T>
 * @param <U>
 */
@FunctionalInterface
public interface ConnectionInfoLoader<T, U extends ConnectionInfoCollector<T>> {

	U load();

}
