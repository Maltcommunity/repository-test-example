plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    testImplementation(project(":common:test-utils"))
}

configurations {
    create("testJar")
}

artifacts {
    val testJar = tasks.register<Jar>("testJar") {
        from(sourceSets["test"].output)
        archiveClassifier.set("tests")
    }
    add("testJar", testJar)
}
