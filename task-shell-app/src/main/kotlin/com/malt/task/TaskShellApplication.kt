package com.malt.task

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TaskShellApplication

fun main(args: Array<String>) {
    val taskOwnerId = determineTaskOwnerId(args)
    CurrentTaskOwnerIdHolder.define(taskOwnerId)

    println("Please wait a bit, your task manager is loading...")
    println()

    runApplication<TaskShellApplication>(*args)
}

private fun determineTaskOwnerId(args: Array<String>): TaskOwnerId {
    lateinit var userName: String

    val userNameFromArgs = readUserNameFromArgs(args)
    if (userNameFromArgs != null) {
        userName = userNameFromArgs
        println("""

            Welcome $userName!
            
        """.trimIndent())
    } else {
        userName = askUserForHerName()
        println("""

            Welcome $userName!
            (Next time, you may directly provide your name as follows:
            <this program> --name "$userName"
            
        """.trimIndent())
    }

    // for this sample program, using the provided name as owner ID will do
    return TaskOwnerId(userName)
}

private fun readUserNameFromArgs(args: Array<String>): String? {
    return if (args.size == 2 && args[0] == "--user") args[1]
    else null
}

private fun askUserForHerName(): String {
    while (true) {
        println()
        print("Hi, please enter your user name: ")

        val name = readLine()
        if (!name.isNullOrBlank()) {
            return name.trim()
        }
    }
}
