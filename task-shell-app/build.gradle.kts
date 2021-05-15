plugins {
    id("org.springframework.boot")

    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":adapters:task-persistence"))
    implementation(project(":task-domain"))

    implementation("org.liquibase:liquibase-core")
    implementation("org.springframework.shell:spring-shell-starter:2.0.0.RELEASE")

    testImplementation(project(path = ":task-domain", configuration = "testJar"))
    testImplementation(project(":common:test-utils"))
}
