package net.luversof.boot.env;

import java.util.Arrays;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;
import net.luversof.boot.constant.ProfileInfo;
import net.luversof.boot.exception.BlueskyException;

@Slf4j
public class BlueskyEnvironmentPostProcessor implements EnvironmentPostProcessor {
	
	private static final String NET_PROFILE = "net-profile";
	
	Properties properties = new Properties();

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		var activeProfiles = environment.getActiveProfiles();
		Assert.notNull(activeProfiles, "NOT EXIST activeProfiles");
		log.debug("activeProfiles : {}", Arrays.asList(activeProfiles));
		var profile = Arrays.stream(activeProfiles).filter(x -> ProfileInfo.NET_PROFILE_LIST.stream().anyMatch(y -> y.equals(x))).findAny().orElseThrow(() -> new BlueskyException("NOT_EXIST_PROFILE"));
		
		properties.setProperty(NET_PROFILE, profile);
		
		environment.getPropertySources().addFirst(new PropertiesPropertySource("blueskyBootProperties", properties));
	}

}
