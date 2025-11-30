package io.github.luversof.boot.env;

import java.util.Arrays;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.util.Assert;

import io.github.luversof.boot.constant.ProfileInfo;
import io.github.luversof.boot.exception.BlueskyException;
import lombok.extern.slf4j.Slf4j;

/**
 * bluesky-boot에서 사용할 profile 목록 생성
 * 설정한 profile 목록에서 특정한 profile을 따로 추출하여 bluesky-boot에서 사용하려는 용도
 * 
 * @author bluesky
 *
 */
@Slf4j
public class ProfileEnvironmentPostProcessor implements EnvironmentPostProcessor {

	private static final String BLUESKY_BOOT_PROFILE = "bluesky-boot-profile";

	Properties properties = new Properties();

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		var activeProfiles = environment.getActiveProfiles();
		if (activeProfiles.length == 0) {
			log.warn("No active profile configured, defaulting to {}", ProfileInfo.DEFAULT);
			environment.setActiveProfiles(ProfileInfo.DEFAULT);
			activeProfiles = environment.getActiveProfiles();
		}
		Assert.notEmpty(activeProfiles, "NOT EXIST activeProfiles");
		log.debug("activeProfiles : {}", Arrays.asList(activeProfiles));
		var profile = Arrays.stream(activeProfiles)
				.filter(x -> ProfileInfo.getBlueskyBootProfileList().stream().anyMatch(y -> y.equals(x))).findAny()
				.orElseThrow(() -> new BlueskyException("NOT_EXIST_PROFILE"));

		properties.setProperty(BLUESKY_BOOT_PROFILE, profile);

		environment.getPropertySources().addFirst(new PropertiesPropertySource("blueskyBootProperties", properties));
	}

}
