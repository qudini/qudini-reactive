package com.qudini.reactive.example;

import com.qudini.reactive.logging.Logged;
import com.qudini.reactive.metrics.Measured;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Logged
    @Measured("qudini_duration")
    @GetMapping("/")
    @PreAuthorize("hasRole('EXAMPLE')")
    public Mono<String> index() {
        return Mono.just("hello reactive world");
    }

}
