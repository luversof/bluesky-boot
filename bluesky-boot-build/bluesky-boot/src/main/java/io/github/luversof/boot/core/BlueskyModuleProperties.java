package io.github.luversof.boot.core;

import java.util.Map;

/**
 * Top-level class provided for handling module branching
 * 
 * Used to implement per-feature module branch handling
 * @author bluesky
 *
 * @param <T>
 */
public interface BlueskyModuleProperties<T extends BlueskyProperties> {

	Map<String, T> getModules();

}
