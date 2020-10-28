package com.qudini.reactive.utils.metadata;

import lombok.Getter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@Getter
public final class DefaultMetadataService implements MetadataService {

    private static final String DEFAULT_NAME = "unknown";

    private static final String DEFAULT_VERSION = "unknown";

    private final String environment;

    private final String buildName;

    private final String buildVersion;

    public DefaultMetadataService(String environment, ApplicationContext applicationContext) {
        this.environment = environment;
        var applicationPackage = applicationContext
                .getBeansWithAnnotation(SpringBootApplication.class)
                .values()
                .stream()
                .findFirst()
                .map(Object::getClass)
                .map(Class::getPackage);
        this.buildName = applicationPackage
                .map(Package::getImplementationTitle)
                .orElse(DEFAULT_NAME);
        this.buildVersion = applicationPackage
                .map(Package::getImplementationVersion)
                .orElse(DEFAULT_VERSION);
    }

}
