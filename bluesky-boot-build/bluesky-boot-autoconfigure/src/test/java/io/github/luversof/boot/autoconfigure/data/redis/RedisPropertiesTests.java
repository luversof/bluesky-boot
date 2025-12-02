/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.luversof.boot.autoconfigure.data.redis;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties.Lettuce;
import org.springframework.data.redis.support.collections.RedisProperties;

import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;

/**
 * Tests for {@link RedisProperties}.
 *
 * @author Stephane Nicoll
 */
class RedisPropertiesTests {

	@Test
	void lettuceDefaultsAreConsistent() {
		Lettuce lettuce = new DataRedisProperties().getLettuce();
		ClusterTopologyRefreshOptions defaultClusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
				.build();
		assertThat(lettuce.getCluster().getRefresh().isDynamicRefreshSources())
				.isEqualTo(defaultClusterTopologyRefreshOptions.useDynamicRefreshSources());
	}

}
