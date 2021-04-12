package com.qudini.reactive.graphql.app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.WebFilter;

@SpringBootApplication
public class TestApplication {

    @Bean
    public WebFilter contextFilter() {
        return (exchange, chain) -> chain.filter(exchange).contextWrite(context -> context.put(String.class, "helloworld"));
    }

}
