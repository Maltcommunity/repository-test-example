package com.malt.task

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class TaskCommandsMockup {

    @ShellMethod(key = ["add-task"], value = "Add a new task")
    fun addTasks(
            summary: String,
            description: String
    ) = """
            Task #42 created with:
            Summary: $summary
            Description: $description
        """.trimIndent()

    @ShellMethod(key = ["list-tasks"], value = "List tasks")
    fun listTasks(
            @ShellOption(value = ["oneline"], help = "Display each task on a single line")
            oneLinePerTask: Boolean
    ) = if (oneLinePerTask)
        """
            #1 Some task
            #2 Some other task
        """.trimIndent()
    else
        """
            #1 Some task
            Do something
            
            #2 Some other task
            Do some other thing
        """.trimIndent()

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
