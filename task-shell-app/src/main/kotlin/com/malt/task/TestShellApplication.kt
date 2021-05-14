package com.malt.task

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TestShellApplication

fun main(args: Array<String>) {
	runApplication<TestShellApplication>(*args)
}
