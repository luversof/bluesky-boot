package io.github.luversof.boot.autoconfigure.data.redis;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ContextConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.LettuceClientConfigurationBuilder;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;

import io.github.luversof.boot.test.context.runner.BlueskyApplicationContextRunner;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions.RefreshTrigger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Disabled
class RedisAutoConfigurationTests {

	private final BlueskyApplicationContextRunner contextRunner = BlueskyApplicationContextRunner.get()
			.withConfiguration(AutoConfigurations.of(RedisAutoConfiguration.class));

	@Test
	void testDefaultRedisConfiguration() {
		this.contextRunner.run(context -> {
			assertThat(context.getBean("redisTemplate")).isInstanceOf(RedisOperations.class);
			assertThat(context).hasSingleBean(StringRedisTemplate.class);
			assertThat(context).hasSingleBean(RedisConnectionFactory.class);
			assertThat(context.getBean(RedisConnectionFactory.class)).isInstanceOf(LettuceConnectionFactory.class);
		});
	}

	@Test
	void testOverrideRedisConfiguration() {
		this.contextRunner.withPropertyValues("spring.redis.host:foo", "spring.redis.database:1",
				"spring.redis.lettuce.shutdown-timeout:500").run(context -> {
					LettuceConnectionFactory cf = context.getBean(LettuceConnectionFactory.class);
					assertThat(cf.getHostName()).isEqualTo("foo");
					assertThat(cf.getDatabase()).isEqualTo(1);
					assertThat(getUserName(cf)).isNull();
					assertThat(cf.getPassword()).isNull();
					assertThat(cf.isUseSsl()).isFalse();
					assertThat(cf.getShutdownTimeout()).isEqualTo(500);
				});
	}

	@Test
	void testCustomizeRedisConfiguration() {
		this.contextRunner.withUserConfiguration(CustomConfiguration.class).run(context -> {
			LettuceConnectionFactory cf = context.getBean(LettuceConnectionFactory.class);
			assertThat(cf.isUseSsl()).isTrue();
		});
	}

	@Test
	void testRedisUrlConfiguration() {
		this.contextRunner
				.withPropertyValues("spring.redis.host:foo", "spring.redis.url:redis://user:password@example:33")	// NOSONAR secrets:S6739
				.run(context -> {
					LettuceConnectionFactory cf = context.getBean(LettuceConnectionFactory.class);
					assertThat(cf.getHostName()).isEqualTo("example");
					assertThat(cf.getPort()).isEqualTo(33);
					assertThat(getUserName(cf)).isEqualTo("user");
					assertThat(cf.getPassword()).isEqualTo("password");
					assertThat(cf.isUseSsl()).isFalse();
				});
	}

	@Test
	void testOverrideUrlRedisConfiguration() {
		this.contextRunner
				.withPropertyValues("spring.redis.host:foo", "spring.redis.password:xyz", "spring.redis.port:1000",
						"spring.redis.ssl:false", "spring.redis.url:rediss://user:password@example:33")	// NOSONAR secrets:S6739
				.run(context -> {
					LettuceConnectionFactory cf = context.getBean(LettuceConnectionFactory.class);
					assertThat(cf.getHostName()).isEqualTo("example");
					assertThat(cf.getPort()).isEqualTo(33);
					assertThat(getUserName(cf)).isEqualTo("user");
					assertThat(cf.getPassword()).isEqualTo("password");
					assertThat(cf.isUseSsl()).isTrue();
				});
	}

	@Test
	void testPasswordInUrlWithColon() {
		this.contextRunner.withPropertyValues("spring.redis.url:redis://user:password@example:33").run(context -> {	// NOSONAR secrets:S6739
			LettuceConnectionFactory cf = context.getBean(LettuceConnectionFactory.class);
			assertThat(cf.getHostName()).isEqualTo("example");
			assertThat(cf.getPort()).isEqualTo(33);
			assertThat(getUserName(cf)).isEmpty();
			assertThat(cf.getPassword()).isEqualTo("pass:word");
		});
	}

	@Test
	void testPasswordInUrlStartsWithColon() {
		this.contextRunner.withPropertyValues("spring.redis.url:redis://user::pass:word@example:33").run(context -> {	// NOSONAR secrets:S6739
			LettuceConnectionFactory cf = context.getBean(LettuceConnectionFactory.class);
			assertThat(cf.getHostName()).isEqualTo("example");
			assertThat(cf.getPort()).isEqualTo(33);
			assertThat(getUserName(cf)).isEqualTo("user");
			assertThat(cf.getPassword()).isEqualTo(":pass:word");
		});
	}

	@Test
	void testRedisConfigurationWithPool() {
		this.contextRunner.withPropertyValues("spring.redis.host:foo", "spring.redis.lettuce.pool.min-idle:1",
				"spring.redis.lettuce.pool.max-idle:4", "spring.redis.lettuce.pool.max-active:16",
				"spring.redis.lettuce.pool.max-wait:2000", "spring.redis.lettuce.pool.time-between-eviction-runs:30000",
				"spring.redis.lettuce.shutdown-timeout:1000").run(context -> {
					LettuceConnectionFactory cf = context.getBean(LettuceConnectionFactory.class);
					assertThat(cf.getHostName()).isEqualTo("foo");
					GenericObjectPoolConfig<?> poolConfig = getPoolingClientConfiguration(cf).getPoolConfig();
					assertThat(poolConfig.getMinIdle()).isEqualTo(1);
					assertThat(poolConfig.getMaxIdle()).isEqualTo(4);
					assertThat(poolConfig.getMaxTotal()).isEqualTo(16);
					assertThat(poolConfig.getMaxWaitDuration()).isEqualTo(Duration.ofMillis(2000));
					assertThat(poolConfig.getDurationBetweenEvictionRuns()).isEqualTo(Duration.ofMillis(30000));
					assertThat(cf.getShutdownTimeout()).isEqualTo(1000);
				});
	}

	@Test
	void testRedisConfigurationWithTimeoutAndConnectTimeout() {
		this.contextRunner.withPropertyValues("spring.redis.host:foo", "spring.redis.timeout:250",
				"spring.redis.connect-timeout:1000").run(context -> {
					LettuceConnectionFactory cf = context.getBean(LettuceConnectionFactory.class);
					assertThat(cf.getHostName()).isEqualTo("foo");
					assertThat(cf.getTimeout()).isEqualTo(250);
					assertThat(cf.getClientConfiguration().getClientOptions().get().getSocketOptions()
							.getConnectTimeout().toMillis()).isEqualTo(1000);
				});
	}

	@Test
	void testRedisConfigurationWithDefaultTimeouts() {
		this.contextRunner.withPropertyValues("spring.redis.host:foo").run(context -> {
			LettuceConnectionFactory cf = context.getBean(LettuceConnectionFactory.class);
			assertThat(cf.getHostName()).isEqualTo("foo");
			assertThat(cf.getTimeout()).isEqualTo(60000);
			assertThat(cf.getClientConfiguration().getClientOptions().get().getSocketOptions().getConnectTimeout()
					.toMillis()).isEqualTo(10000);
		});
	}

	@Test
	void testRedisConfigurationWithClientName() {
		this.contextRunner.withPropertyValues("spring.redis.host:foo", "spring.redis.client-name:spring-boot")
				.run(context -> {
					LettuceConnectionFactory cf = context.getBean(LettuceConnectionFactory.class);
					assertThat(cf.getHostName()).isEqualTo("foo");
					assertThat(cf.getClientName()).isEqualTo("spring-boot");
				});
	}

	@Test
	void connectionFactoryWithJedisClientType() {
		this.contextRunner.withPropertyValues("spring.redis.client-type:jedis").run(context -> {
			assertThat(context).hasSingleBean(RedisConnectionFactory.class);
			assertThat(context.getBean(RedisConnectionFactory.class)).isInstanceOf(JedisConnectionFactory.class);
		});
	}

	@Test
	void connectionFactoryWithLettuceClientType() {
		this.contextRunner.withPropertyValues("spring.redis.client-type:lettuce").run(context -> {
			assertThat(context).hasSingleBean(RedisConnectionFactory.class);
			assertThat(context.getBean(RedisConnectionFactory.class)).isInstanceOf(LettuceConnectionFactory.class);
		});
	}

	@Test
	void testRedisConfigurationWithSentinel() {
		List<String> sentinels = Arrays.asList("127.0.0.1:26379", "127.0.0.1:26380");
		this.contextRunner
				.withPropertyValues("spring.redis.sentinel.master:mymaster",
						"spring.redis.sentinel.nodes:" + StringUtils.collectionToCommaDelimitedString(sentinels))
				.run(context -> assertThat(context.getBean(LettuceConnectionFactory.class).isRedisSentinelAware())
						.isTrue());
	}

	@Test
	void testRedisConfigurationWithSentinelAndDatabase() {
		this.contextRunner.withPropertyValues("spring.redis.database:1", "spring.redis.sentinel.master:mymaster",
				"spring.redis.sentinel.nodes:127.0.0.1:26379, 127.0.0.1:26380").run(context -> {
					LettuceConnectionFactory connectionFactory = context.getBean(LettuceConnectionFactory.class);
					assertThat(connectionFactory.getDatabase()).isEqualTo(1);
					assertThat(connectionFactory.isRedisSentinelAware()).isTrue();
				});
	}

	@Test
	void testRedisConfigurationWithSentinelAndAuthentication() {
		this.contextRunner.withPropertyValues("spring.redis.username=user", "spring.redis.password=password",
				"spring.redis.sentinel.master:mymaster",
				"spring.redis.sentinel.nodes:127.0.0.1:26379,  127.0.0.1:26380").run(context -> {
					LettuceConnectionFactory connectionFactory = context.getBean(LettuceConnectionFactory.class);
					assertThat(getUserName(connectionFactory)).isEqualTo("user");
					assertThat(connectionFactory.getPassword()).isEqualTo("password");
					RedisSentinelConfiguration sentinelConfiguration = connectionFactory.getSentinelConfiguration();
					assertThat(sentinelConfiguration.getSentinelPassword().isPresent()).isFalse();
					Set<RedisNode> sentinels = connectionFactory.getSentinelConfiguration().getSentinels();
					assertThat(sentinels.stream().map(Object::toString).collect(Collectors.toSet()))
							.contains("127.0.0.1:26379", "127.0.0.1:26380");
				});
	}

	@Test
	void testRedisConfigurationWithSentinelPasswordAndDataNodePassword() {
		this.contextRunner.withPropertyValues("spring.redis.password=password", "spring.redis.sentinel.password=secret",
				"spring.redis.sentinel.master:mymaster",
				"spring.redis.sentinel.nodes:127.0.0.1:26379,  127.0.0.1:26380").run(context -> {
					LettuceConnectionFactory connectionFactory = context.getBean(LettuceConnectionFactory.class);
					assertThat(getUserName(connectionFactory)).isNull();
					assertThat(connectionFactory.getPassword()).isEqualTo("password");
					RedisSentinelConfiguration sentinelConfiguration = connectionFactory.getSentinelConfiguration();
					assertThat(new String(sentinelConfiguration.getSentinelPassword().get())).isEqualTo("secret");
					Set<RedisNode> sentinels = sentinelConfiguration.getSentinels();
					assertThat(sentinels.stream().map(Object::toString).collect(Collectors.toSet()))
							.contains("127.0.0.1:26379", "127.0.0.1:26380");
				});
	}

	@Test
	void testRedisConfigurationWithCluster() {
		List<String> clusterNodes = Arrays.asList("127.0.0.1:27379", "127.0.0.1:27380");
		this.contextRunner.withPropertyValues("spring.redis.cluster.nodes[0]:" + clusterNodes.get(0),
				"spring.redis.cluster.nodes[1]:" + clusterNodes.get(1)).run(context -> {
					RedisClusterConfiguration clusterConfiguration = context.getBean(LettuceConnectionFactory.class)
							.getClusterConfiguration();
					assertThat(clusterConfiguration.getClusterNodes()).hasSize(2);
					assertThat(clusterConfiguration.getClusterNodes())
							.extracting(node -> node.getHost() + ":" + node.getPort())
							.containsExactlyInAnyOrder("127.0.0.1:27379", "127.0.0.1:27380");
				});

	}

	@Test
	void testRedisConfigurationWithClusterAndAuthentication() {
		List<String> clusterNodes = Arrays.asList("127.0.0.1:27379", "127.0.0.1:27380");
		this.contextRunner.withPropertyValues("spring.redis.username=user", "spring.redis.password=password",
				"spring.redis.cluster.nodes[0]:" + clusterNodes.get(0),
				"spring.redis.cluster.nodes[1]:" + clusterNodes.get(1)).run(context -> {
					LettuceConnectionFactory connectionFactory = context.getBean(LettuceConnectionFactory.class);
					assertThat(getUserName(connectionFactory)).isEqualTo("user");
					assertThat(connectionFactory.getPassword()).isEqualTo("password");
				}

		);
	}

	@Test
	void testRedisConfigurationCreateClientOptionsByDefault() {
		this.contextRunner.run(assertClientOptions(ClientOptions.class, options -> {
			assertThat(options.getTimeoutOptions().isApplyConnectionTimeout()).isTrue();
			assertThat(options.getTimeoutOptions().isTimeoutCommands()).isTrue();
		}));
	}

	@Test
	void testRedisConfigurationWithClusterCreateClusterClientOptions() {
		this.contextRunner.withPropertyValues("spring.redis.cluster.nodes=127.0.0.1:27379,127.0.0.1:27380")
				.run(assertClientOptions(ClusterClientOptions.class, options -> {
					assertThat(options.getTimeoutOptions().isApplyConnectionTimeout()).isTrue();
					assertThat(options.getTimeoutOptions().isTimeoutCommands()).isTrue();
				}));
	}

	@Test
	void testRedisConfigurationWithClusterRefreshPeriod() {
		this.contextRunner
				.withPropertyValues("spring.redis.cluster.nodes=127.0.0.1:27379,127.0.0.1:27380",
						"spring.redis.lettuce.cluster.refresh.period=30s")
				.run(assertClientOptions(ClusterClientOptions.class,
						options -> assertThat(options.getTopologyRefreshOptions().getRefreshPeriod())
								.hasSeconds(30)));
	}

	@Test
	void testRedisConfigurationWithClusterAdaptiveRefresh() {
		this.contextRunner
				.withPropertyValues("spring.redis.cluster.nodes=127.0.0.1:27379,127.0.0.1:27380",
						"spring.redis.lettuce.cluster.refresh.adaptive=true")
				.run(assertClientOptions(ClusterClientOptions.class,
						options -> assertThat(options.getTopologyRefreshOptions().getAdaptiveRefreshTriggers())
								.isEqualTo(EnumSet.allOf(RefreshTrigger.class))));
	}

	@Test
	void testRedisConfigurationWithClusterRefreshPeriodHasNoEffectWithNonClusteredConfiguration() {
		this.contextRunner.withPropertyValues("spring.redis.cluster.refresh.period=30s").run(assertClientOptions(
				ClientOptions.class, options -> assertThat(options.getClass()).isEqualTo(ClientOptions.class)));
	}

	@Test
	void testRedisConfigurationWithClusterDynamicRefreshSourcesEnabled() {
		this.contextRunner
				.withPropertyValues("spring.redis.cluster.nodes=127.0.0.1:27379,127.0.0.1:27380",
						"spring.redis.lettuce.cluster.refresh.dynamic-refresh-sources=true")
				.run(assertClientOptions(ClusterClientOptions.class,
						options -> assertThat(options.getTopologyRefreshOptions().useDynamicRefreshSources())
								.isTrue()));
	}

	@Test
	void testRedisConfigurationWithClusterDynamicRefreshSourcesDisabled() {
		this.contextRunner
				.withPropertyValues("spring.redis.cluster.nodes=127.0.0.1:27379,127.0.0.1:27380",
						"spring.redis.lettuce.cluster.refresh.dynamic-refresh-sources=false")
				.run(assertClientOptions(ClusterClientOptions.class,
						options -> assertThat(options.getTopologyRefreshOptions().useDynamicRefreshSources())
								.isFalse()));
	}

	@Test
	void testRedisConfigurationWithClusterDynamicSourcesUnspecifiedUsesDefault() {
		this.contextRunner
				.withPropertyValues("spring.redis.cluster.nodes=127.0.0.1:27379,127.0.0.1:27380",
						"spring.redis.lettuce.cluster.refresh.dynamic-sources=")
				.run(assertClientOptions(ClusterClientOptions.class,
						options -> assertThat(options.getTopologyRefreshOptions().useDynamicRefreshSources())
								.isEqualTo(ClusterTopologyRefreshOptions.DEFAULT_DYNAMIC_REFRESH_SOURCES)));
	}
	
	@Test
	@Disabled("테스트 용으로 작성, 비활성화 처리함")
	void testBlueskyRedisTemplate() {
		this.contextRunner
		.withUserConfiguration(BlueskyCustomConfiguration.class)
		.run(context -> {
			@SuppressWarnings("unchecked")
			HashOperations<String, String, Object> redisOperations = context.getBean("redisTemplate", HashOperations.class);
			var redisTemplate = context.getBean("redisTemplate", RedisTemplate.class);
			assertThat(redisTemplate).isNotNull();
			Long increment = redisOperations.increment("key1", "key2", 1);
			log.debug("Test : {}", increment);
			log.debug("Test2 : {}", redisOperations.increment("key1", "key2", 0));
			
		});
	}
	
	@Test
	void testBlueskyStringRedisTemplate() {
		this.contextRunner
//		.withUserConfiguration(BlueskyCustomConfiguration.class)
		.run(context -> {
			@SuppressWarnings("unchecked")
			HashOperations<String, String, Integer> redisOperations = context.getBean("stringRedisTemplate", HashOperations.class);
			Long increment = redisOperations.increment("key1", String.valueOf(1), 1);
			log.debug("Test : {}", increment);
			log.debug("Test2 : {}", redisOperations.get("key1", String.valueOf(1)));
			
		});
	}

	private <T extends ClientOptions> ContextConsumer<AssertableApplicationContext> assertClientOptions(
			Class<T> expectedType, Consumer<T> options) {
		return context -> {
			LettuceClientConfiguration clientConfiguration = context.getBean(LettuceConnectionFactory.class)
					.getClientConfiguration();
			assertThat(clientConfiguration.getClientOptions()).isPresent();
			ClientOptions clientOptions = clientConfiguration.getClientOptions().get();
			assertThat(clientOptions.getClass()).isEqualTo(expectedType);
			options.accept(expectedType.cast(clientOptions));
		};
	}

	private LettucePoolingClientConfiguration getPoolingClientConfiguration(LettuceConnectionFactory factory) {
		return (LettucePoolingClientConfiguration) ReflectionTestUtils.getField(factory, "clientConfiguration");
	}

	private String getUserName(LettuceConnectionFactory factory) {
		return ReflectionTestUtils.invokeMethod(factory, "getRedisUsername");
	}

	@Configuration(proxyBeanMethods = false)
	static class CustomConfiguration {

		@Bean
		LettuceClientConfigurationBuilderCustomizer customizer() {
			return LettuceClientConfigurationBuilder::useSsl;
		}

	}
	
	@Configuration(proxyBeanMethods = false)
	static class BlueskyCustomConfiguration {
        @Bean
        RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<Object, Object> template = new RedisTemplate<>();
            template.setDefaultSerializer(RedisSerializer.json());
            template.setKeySerializer(RedisSerializer.string());
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }

        @Bean
        StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
            StringRedisTemplate template = new StringRedisTemplate();
            template.setDefaultSerializer(RedisSerializer.json());
            template.setKeySerializer(RedisSerializer.string());
            template.setHashKeySerializer(RedisSerializer.string());
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }
	}
}
