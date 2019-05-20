package net.luversof.boot.env;

import java.util.Arrays;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import lombok.extern.slf4j.Slf4j;
import net.luversof.boot.constant.ProfileInfo;
import net.luversof.boot.exception.BlueskyException;

@Slf4j
public class BlueskyEnvironmentPostProcessor implements EnvironmentPostProcessor {
	
	private static final String NET_PROFILE = "net-profile";
	
	Properties properties = new Properties();

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		System.out.println("!@#!@#!@#@!#!@#!@#");
		log.debug("test :::::::::::::: {}", environment);
		String[] activeProfiles = environment.getActiveProfiles();
		log.debug("activeProfiles : {}", Arrays.asList(activeProfiles));
		String profile = Arrays.stream(activeProfiles).filter(x -> Arrays.stream(ProfileInfo.NET_PROFILES).anyMatch(y -> y.equals(x))).findAny().orElseThrow(() -> new BlueskyException("NOT_EXIST_PROFILE"));
		
		properties.setProperty(NET_PROFILE, profile);
		
		environment.getPropertySources().addFirst(new PropertiesPropertySource("blueskyBootProperties", properties));
	}

}
