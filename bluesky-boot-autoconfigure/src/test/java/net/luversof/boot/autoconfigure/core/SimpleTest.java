package net.luversof.boot.autoconfigure.core;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleTest {

	@Test
	public void stringTest() {
		
		String a = "bluesky-modules.mongo.connection-map.test2.host";
		String replace = a.replace("bluesky-modules.mongo.connection-map", "");
		log.debug("result : {}", replace);
		String[] split = replace.split("\\.");
		log.debug("result : {}", split[1]);
	}
}
