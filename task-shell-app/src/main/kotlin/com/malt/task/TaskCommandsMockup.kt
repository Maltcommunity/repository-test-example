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

    @ShellMethod(key = ["merge-tasks"], value = "Merge some tasks together")
    fun mergeTasks(
            @ShellOption(help = "TASK_ID_1")
            taskId1: String,
            @ShellOption(help = "TASK_ID_2")
            taskId2: String
    ) = """
        Edit the result and validate...
        
        Tasks merged: $taskId1, $taskId2
        Summary: Merged summary
        Description: Merged description
    """
}
