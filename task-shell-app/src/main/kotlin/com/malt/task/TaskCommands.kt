package com.malt.task

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

private val NL = System.lineSeparator()

@ShellComponent
class TaskCommands(
        private val taskService: CurrentUserTaskService
) {

    @ShellMethod(key = ["add-task"], value = "Add a new task")
    fun addTask(
            summary: String,
            description: String?
    ): String {
        if (!isAValidTaskSummary(summary.trim())) {
            return "Sorry, the provided summary is invalid. Task summary can't be blank."
        }

        val newTask = taskService.addTaskForUser(summary.trim(), description?.trim())

        return """
            Task ${newTask.id.value} created with:
            Summary: ${newTask.summary}
            ${if (newTask.description != null) "Description: ${newTask.description}" else "No description"}
        """.trimIndent()
    }

    @ShellMethod(key = ["list-tasks"], value = "List tasks")
    fun listTasks(
            @ShellOption(value = ["oneline"], help = "Display each task on a single line")
            oneLinePerTask: Boolean
    ): String? {
        val userTasks = taskService.findAllUserTasks()
        return if (userTasks.isEmpty()) {
            "Nothing to do right now!"
        } else {
            val taskHeaderLine = "--------------------$NL"
            userTasks.joinToString(separator = "$NL$NL$taskHeaderLine", prefix = taskHeaderLine) {
                it.multilineRepresentation(oneLinePerTask)
            }
        }
    }

    @ShellMethod(key = ["merge-tasks"], value = "Merge some tasks together")
    fun mergeTasks(
            @ShellOption(help = "TASK_ID_1")
            taskId1: String,
            @ShellOption(help = "TASK_ID_2")
            taskId2: String
    ): String {
        return try {
            val mergedTask = taskService.mergeUserTasks(TaskId(taskId1), TaskId(taskId2))

            """
            |Tasks $taskId1 and $taskId2 successfully merged:
            |
            |${mergedTask.multilineRepresentation()}
        """.trimMargin()
        } catch (e: TaskNotFoundException) {
            e.message!!
        } catch (e: TasksCanNotBeMergedException) {
            e.message!!
        }
    }
}

private fun Task.multilineRepresentation(singleLine: Boolean = false): String {
    if (singleLine) {
        TODO("online option isn't supported yet")
    }
    return """
        |${id.value}
        |$summary
        |${description ?: "No description"}
    """.trimMargin()
}
