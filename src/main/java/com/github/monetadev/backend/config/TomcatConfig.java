package com.github.monetadev.backend.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return (factory) -> {
            factory.addContextCustomizers(context -> {
                context.setAllowCasualMultipartParsing(Boolean.FALSE);
                context.setSwallowAbortedUploads(Boolean.FALSE);
            });

            factory.addConnectorCustomizers(connector -> {
                connector.setXpoweredBy(Boolean.FALSE);
            });
        };
    }
}
