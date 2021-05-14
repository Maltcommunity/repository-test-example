package com.malt.test.testcontainers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class TestContainersUserProperties {

    private static final Logger log = LoggerFactory.getLogger(TestContainersUserProperties.class);

    /**
     * See <a href="https://www.testcontainers.org/features/configuration/#disabling-the-startup-checks">
     * "Disabling the startup checks" on Testcontainers documentation</a>
     */
    public static void attemptToDisableTestcontainersStartupsChecks() throws IOException {
        var testContainersPropertiesFile = new File(System.getProperty("user.home"), ".testcontainers.properties");

        var testContainersProperties = new Properties();
        if (testContainersPropertiesFile.exists()) {
            try (var fReader = new FileReader(testContainersPropertiesFile, StandardCharsets.UTF_8)) {
                testContainersProperties.load(fReader);
            }
        }

        var dirty = false;

        if (!testContainersProperties.containsKey("checks.disable")) {
            testContainersProperties.put("checks.disable", "true");
            dirty = true;
            log.warn("{} has been added to {} to save a few seconds", "checks.disable=true", testContainersPropertiesFile);
        } else if ("false".equals(testContainersProperties.getProperty("checks.disable"))) {
            log.warn("{} contains {} which means your test runs may take a few more seconds", testContainersPropertiesFile, "checks.disable=false");
        }

        if (!testContainersProperties.containsKey("testcontainers.reuse.enable")) {
            testContainersProperties.put("testcontainers.reuse.enable", "true");
            dirty = true;
            log.warn("{} has been added to {} so that willing tests can reuse containers", "testcontainers.reuse.enable=true", testContainersPropertiesFile);
        } else if ("false".equals(testContainersProperties.getProperty("testcontainers.reuse.enable"))) {
            log.warn("{} contains {} which means willing tests cannot reuse containers on your env", testContainersPropertiesFile, "testcontainers.reuse.enable=false");
        }

        if (dirty) {
            try (var fWriter = new FileWriter(testContainersPropertiesFile, StandardCharsets.UTF_8)) {
                testContainersProperties.store(fWriter, "Testcontainers properties");
            }
        }
    }
}
