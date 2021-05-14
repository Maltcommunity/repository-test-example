plugins {
    id("org.springframework.boot")

    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":adapters:task-persistence"))

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.liquibase:liquibase-core")
    implementation("org.springframework.shell:spring-shell-starter:2.0.0.RELEASE")

    testImplementation(project(":common:test-utils"))
}
