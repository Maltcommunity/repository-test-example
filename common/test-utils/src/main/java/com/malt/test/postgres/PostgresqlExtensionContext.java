package com.malt.test.postgres;

import org.testcontainers.containers.PostgreSQLContainer;

class PostgresqlExtensionContext {

    private final Class<?> entryPoint;
    private final PostgreSQLContainer<?> container;

    PostgresqlExtensionContext(Class<?> entryPoint, PostgreSQLContainer<?> container) {
        this.entryPoint = entryPoint;
        this.container = container;
    }

    public PostgreSQLContainer<?> getContainer() {
        return container;
    }

    public Class<?> getEntryPoint() {
        return entryPoint;
    }
}
