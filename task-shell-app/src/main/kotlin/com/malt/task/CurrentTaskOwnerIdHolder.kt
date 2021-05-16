package com.malt.task

/**
 * There may be only one owner concerned during the execution of this program: the user running it.
 */
object CurrentTaskOwnerIdHolder {

    private lateinit var ownerId: TaskOwnerId

    val currentTaskOwnerId: TaskOwnerId by lazy { ownerId }

    val isDefined by lazy { this::ownerId.isInitialized }

    fun define(ownerId: TaskOwnerId) {
        this.ownerId = ownerId
    }
}