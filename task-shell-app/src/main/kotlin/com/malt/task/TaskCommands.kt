package com.malt.task

import com.malt.task.CurrentTaskOwnerIdHolder.currentTaskOwnerId
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.util.stream.Collectors.joining

private val NL = System.lineSeparator()

@ShellComponent
class TaskCommands(
        private val clock: Clock,
        private val taskRepository: TaskRepository
) {

    @Transactional
    @ShellMethod(key = ["add-task"], value = "Add a new task")
    fun addTask(
            summary: String,
            description: String
    ): String {
        val newTask = Task(
                clock = clock,
                ownerId = currentTaskOwnerId,
                summary = summary,
                description = description
        )
        taskRepository.save(newTask)

        return """
            Task ${newTask.id.value} created with:
            Summary: ${newTask.summary}
            Description: ${newTask.description}
        """.trimIndent()
    }

    @Transactional(readOnly = true)
    @ShellMethod(key = ["list-tasks"], value = "List tasks")
    fun listTasks(
            @ShellOption(value = ["oneline"], help = "Display each task on a single line")
            oneLinePerTask: Boolean
    ): String? {
        return taskRepository.find(TaskOwnerIdIs(currentTaskOwnerId))
                .map { it.multilineRepresentation(oneLinePerTask) }
                .collect(joining(NL + NL))
                .ifEmpty { "Nothing to do right now!" }
    }
}

private fun Task.multilineRepresentation(oneLinePerTask: Boolean): String {
    if (oneLinePerTask) {
        TODO("online option isn't supported yet")
    }
    return """
        ${id.value}
        $summary
        $description
    """.trimIndent()
}
