package com.malt.task

class TaskNotFoundException(taskId: TaskId) : Exception("Task not found: ${taskId.value}")
