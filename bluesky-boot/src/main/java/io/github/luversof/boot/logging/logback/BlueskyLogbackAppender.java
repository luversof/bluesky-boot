package io.github.luversof.boot.logging.logback;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import lombok.Setter;

public class BlueskyLogbackAppender<E> extends UnsynchronizedAppenderBase<E> {
	
	private BlueskyLogbackAppenderService<E> blueskyLogbackAppenderService;
	
	public BlueskyLogbackAppender(BlueskyLogbackAppenderService<E> blueskyLogbackAppenderService) {
		this.blueskyLogbackAppenderService = blueskyLogbackAppenderService;
	}
	
	@Setter
	private Encoder<E> encoder;

	@Override
	protected void append(E eventObject) {
		if (!isStarted()) {
			return;
		}
		blueskyLogbackAppenderService.addLog(eventObject, new String(encoder.encode(eventObject)));
	}

}
