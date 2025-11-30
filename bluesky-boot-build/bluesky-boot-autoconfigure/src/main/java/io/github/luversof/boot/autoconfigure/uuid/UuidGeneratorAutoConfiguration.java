package io.github.luversof.boot.autoconfigure.uuid;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.github.f4b6a3.uuid.alt.GUID;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.uuid.UuidGenerator;
import io.github.luversof.boot.uuid.UuidGeneratorGroupProperties;
import io.github.luversof.boot.uuid.UuidGeneratorModuleProperties;
import io.github.luversof.boot.uuid.UuidGeneratorProperties;

@AutoConfiguration("blueskyBootUuidGeneratorAutoConfiguration")
@ConditionalOnClass(GUID.class)
@EnableConfigurationProperties({
	UuidGeneratorProperties.class,
	UuidGeneratorModuleProperties.class,
	UuidGeneratorGroupProperties.class
})
public class UuidGeneratorAutoConfiguration {
	
	@Bean
	UuidGenerator uuidGenerator() {
		return () -> 
			switch (BlueskyContextHolder.getProperties(UuidGeneratorProperties.class).getVersion()) {
				case V1 -> GUID.v1().toUUID();
				case V4 -> GUID.v4().toUUID();
				case V6 -> GUID.v6().toUUID();
				case V7 -> GUID.v7().toUUID();
			}; 
	}
}
