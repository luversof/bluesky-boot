package io.github.luversof.boot.autoconfigure.logging.logback;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckDescription;
import io.github.luversof.boot.logging.logback.BlueskyLogbackAppenderService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/_check/logging", produces = MediaType.APPLICATION_JSON_VALUE)
public class BlueskyLogbackDevCheckController {

	private final BlueskyLogbackAppenderService<ILoggingEvent> blueskyLogbackAppenderService;
	
	@DevCheckDescription("로그 확인")
	@GetMapping("/logView")
	public List<String> logView() {
		return blueskyLogbackAppenderService.getLogQueue().stream().map(queue -> queue.getLogMessage().replaceAll(CoreConstants.LINE_SEPARATOR, "").replace("\t", "")).collect(Collectors.toList());
	}
}
