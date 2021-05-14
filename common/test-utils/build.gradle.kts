plugins {
    id("io.spring.dependency-management")

    kotlin("jvm")
}

dependencies {
    api("io.mockk:mockk:1.11.0")

    val striktVersion = "0.31.0"
    api("io.strikt:strikt-core:$striktVersion")
    api("io.strikt:strikt-mockk:$striktVersion")
    api("io.strikt:strikt-java-time:0.28.2")

    api("org.assertj:assertj-core")
    api("org.postgresql:postgresql")
    api("org.springframework.boot:spring-boot-starter-data-jdbc")
    api("org.springframework.boot:spring-boot-starter-test")
    api("org.testcontainers:junit-jupiter")
    api("org.testcontainers:postgresql")
}
