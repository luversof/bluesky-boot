package io.github.luversof.boot.autoconfigure.connectioninfo.mongo;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.client.MongoClient;

import io.github.luversof.boot.connectioninfo.ConnectionInfoCollector;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.exception.BlueskyException;
import lombok.Getter;

/**
 * MongoClientConnectionInfoLoader를 사용하여 획득한 mongoClient를 관리
 */
public class MongoClientConnectionInfoCollectorDecorator implements ConnectionInfoCollector<MongoClient> {
	
	private Map<String, ConnectionInfoLoader<MongoClient>> mongoClientConnectionInfoLoaderMap;
	
	private Map<String, ZonedDateTime> nonExistConnectionMap = new HashMap<>();
	
	@Getter
	private Map<String, MongoClient> connectionInfoMap = new HashMap<>();
	
	public MongoClientConnectionInfoCollectorDecorator(Map<String, ConnectionInfoLoader<MongoClient>> mongoClientConnectionInfoLoaderMap) {
		this.mongoClientConnectionInfoLoaderMap = mongoClientConnectionInfoLoaderMap;
	}
	
	
	/**
	 * 단건 조회
	 * 없으면 loader에서 가져오기 시도
	 * @param connectionList
	 * @return
	 */
	public MongoClient getConnectionInfo(String connection) {
		if (connectionInfoMap.containsKey(connection)) {
			return connectionInfoMap.get(connection);
		}
		
		checkNonExistConnectionMap(connection);
		
		boolean isLoaded = false;
		
		for (var entry : mongoClientConnectionInfoLoaderMap.entrySet()) {
			var collector = entry.getValue().load(List.of(connection));
			if (!collector.getConnectionInfoMap().isEmpty()) {
				connectionInfoMap.put(connection, collector.getConnectionInfoMap().get(connection));
				isLoaded = true;
				break;
			}
			
		}
		
		if (!isLoaded) {
			nonExistConnectionMap.put(connection, ZonedDateTime.now());
			throw new BlueskyException("NOT_EXIST_MONGO_CONNECTION").setErrorMessageArgs(connection);
		}
		return connectionInfoMap.get(connection);
	}

	/**
	 * nonExistConnectionMap에 해당 키가 있고 캐시 기간내 요청인 경우 throw exception 처리
	 * @return
	 */
	private void checkNonExistConnectionMap(String connection) {
		if(!nonExistConnectionMap.containsKey(connection)) {
			return;
		}
		
		if (nonExistConnectionMap.get(connection).isAfter(ZonedDateTime.now().minusHours(1))) {
			throw new BlueskyException("NOT_EXIST_MONGO_CONNECTION").setErrorMessageArgs(connection);
		}
	}
}
