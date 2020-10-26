package com.qudini.reactive.utils.build;

import lombok.Getter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@Getter
public final class DefaultBuildInfoService implements BuildInfoService {

    private static final String DEFAULT_NAME = "unknown";

    private static final String DEFAULT_VERSION = "unknown";

    private final String name;

    private final String version;

    public DefaultBuildInfoService(ApplicationContext applicationContext) {
        var applicationPackage = applicationContext
                .getBeansWithAnnotation(SpringBootApplication.class)
                .values()
                .stream()
                .findFirst()
                .map(Object::getClass)
                .map(Class::getPackage);
        this.name = applicationPackage
                .map(Package::getImplementationTitle)
                .orElse(DEFAULT_NAME);
        this.version = applicationPackage
                .map(Package::getImplementationVersion)
                .orElse(DEFAULT_VERSION);
    }

}
