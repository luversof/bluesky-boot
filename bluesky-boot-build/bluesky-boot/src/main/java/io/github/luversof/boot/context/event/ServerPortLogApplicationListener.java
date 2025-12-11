package io.github.luversof.boot.context.event;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ApplicationReadyEvent 발생시 서버 포트를 로그로 출력하는 리스너
 * spring boot report 로그 이전에 서버 포트가 출력되어 매번 위로 스크롤하여 로그를 확인해야 하는 불편함을 해소
 * 
 * @author bluesky
 *
 */
public class ServerPortLogApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

	private static final Logger log = LoggerFactory.getLogger(ServerPortLogApplicationListener.class);

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (event.getApplicationContext() instanceof WebServerApplicationContext ctx) {
			String id = ctx.getId();
			int port = ctx.getWebServer().getPort();
			log.info("id: {} started on port: {}", id, port);
		}
	}

}
