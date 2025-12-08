package io.github.luversof.boot.context.event;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ApplicationListener;

import lombok.extern.slf4j.Slf4j;

/**
 * ApplicationReadyEvent 발생시 서버 포트를 로그로 출력하는 리스너
 * spring boot report 로그 이전에 서버 포트가 출력되어 매번 위로 스크롤하여 로그를 확인해야 하는 불편함을 해소
 * @author bluesky
 *
 */
@Slf4j
public class ServerPortLogApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (event.getApplicationContext() instanceof WebServerApplicationContext ctx) {
			int port = ctx.getWebServer().getPort();
			String appName = ctx.getApplicationName();
			log.info("Application {}  started on port: {}", appName, port);
		}
	}

}
