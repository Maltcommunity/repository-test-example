package com.malt.task

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

internal class SqlSelectionTest {

    @Test
    fun `should convert a complex specification`() {
        // given
        val taskIdA = TaskId("taskA")
        val taskIdB = TaskId("taskB")
        val ownerIdA = TaskOwnerId("ownerA")
        val ownerIdB = TaskOwnerId("ownerB")

        val spec = (TaskOwnerIdIs(ownerIdA)
                and (TaskIdIs(taskIdA) or TaskOwnerIdIs(ownerIdB))
                and !TaskIdIs(taskIdB))

        // when
        val (sql, params) = spec.toSqlSelection()

        // then
        expectThat(sql) isEqualTo "((owner_id = :ownerId1) " +
                "AND ((id = :id2) OR (owner_id = :ownerId3))) " +
                "AND (NOT (id = :id4))"

        expectThat(params) isEqualTo mapOf(
                "ownerId1" to "ownerA",
                "id2" to "taskA",
                "ownerId3" to "ownerB",
                "id4" to "taskB",
        )
    }
}
