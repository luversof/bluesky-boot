package io.github.luversof.boot.config;

import java.util.Map;

/**
 * Top-level class provided for handling module branching
 * 
 * Used to implement per-feature module branch handling
 * @author bluesky
 *
 * @param <T>
 */
public interface BlueskyProperties<T> {

	Map<String, T> getModules();

}
