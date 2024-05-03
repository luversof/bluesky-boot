package io.github.luversof.boot.web;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import io.github.luversof.boot.core.BlueskyProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "bluesky-boot.cookie")
public class CookieProperties implements BlueskyProperties {

	private String name;
	
	@DurationUnit(ChronoUnit.SECONDS)
	private Duration maxAge;
	
	private String domain;
	
	private String path;
	
	private Boolean secure;
	
	private Boolean httpOnly;
	
	private String sameSite;

}