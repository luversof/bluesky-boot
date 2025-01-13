package io.github.luversof.boot.connectioninfo;

import java.util.List;

/**
 * interface for calling connection information
 * @author bluesky
 *
 * @param <T> Target type to load via Loader
 */
public interface ConnectionInfoLoader<T> {

	/**
	 * Method for performing load processing based on the received connectionList
	 *  
	 * @param connectionList List of connections to load
	 * @return Collector containing loaded connection information
	 */
	ConnectionInfoCollector<T> load(List<String> connectionList);
	
	/**
	 * Method for handling load without parameters
	 * Used when making a call based on information registered in properties
	 * 
	 * @return Collector containing loaded connection information
	 */
	ConnectionInfoCollector<T> load();

}
