package io.github.luversof.boot.autoconfigure.web.servlet;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

import ch.qos.logback.access.tomcat.LogbackValve;

public class LogbackTomcatServletWebServerFactoryCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

	@Override
	public void customize(TomcatServletWebServerFactory factory) {
		
		var logbackValve = new LogbackValve();
		logbackValve.setFilename("logback-access.xml");
		factory.addEngineValves(logbackValve);
		
	}

}
