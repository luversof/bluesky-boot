package io.github.luversof.boot.connectioninfo;

import java.util.List;

/**
 * interface for calling connection information
 * @author bluesky
 *
 * @param <T>
 * @param <U>
 */
public interface ConnectionInfoLoader<T> {

	/**
	 * 전달받은 connectionList를 기준으로 load 처리를 하기 위한 method 
	 * @param connectionList
	 * @return
	 */
	ConnectionInfoCollector<T> load(List<String> connectionList);
	
	/**
	 * 매개변수 없이 load 처리를 하기 위한 method
	 * properties에 등록된 정보를 기반으로 호출을 하는 경우 사용
	 * @return
	 */
	ConnectionInfoCollector<T> load();

}
