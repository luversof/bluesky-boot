package io.github.luversof.boot.logging.logback;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public class BlueskyLogbackAppenderService<E> {
	
	private static final int QUEUE_SIZE = 500;
	
	@Getter
	private Queue<LogObject<E>> logQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
	
	public void addLog(E eventObject, String logMessage) {
		if (logQueue.size() >= QUEUE_SIZE) {
			logQueue.remove();
		}
		logQueue.offer(new LogObject<>(eventObject, logMessage));
	}
	
	@Data
	@AllArgsConstructor
	public static class LogObject<E> {
		E eventObject;
		String logMessage;
	}

}
