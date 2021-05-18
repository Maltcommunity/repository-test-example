package com.malt.task

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class TaskCommandsMockup {

    @ShellMethod(key = ["show-task"], value = "Show a task")
    fun showTask(
            @ShellOption(help = "TASK_ID")
            taskId: String
    ) = """
        Task: #1
        Summary: Some task
        Description: Do something
    """.trimIndent()

}
