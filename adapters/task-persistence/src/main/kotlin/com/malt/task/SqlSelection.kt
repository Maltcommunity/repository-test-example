package com.malt.task

import com.malt.task.TaskColumns as Cols

internal data class SqlSelection(
        val sql: String,
        val params: Map<String, Any?>,
        val paramSequence: Int
)

internal fun TaskSpecification.toSqlSelection() = toSqlSelection(1)

private fun TaskSpecification.toSqlSelection(paramSequence: Int): SqlSelection = when (this) {
    is AndTaskSpecification -> toSqlSelection(paramSequence)
    is NotTaskSpecification -> toSqlSelection(paramSequence)
    is OrTaskSpecification -> toSqlSelection(paramSequence)
    is TaskIdIs -> toSqlSelection(paramSequence)
    is TaskOwnerIdIs -> toSqlSelection(paramSequence)
}

private fun AndTaskSpecification.toSqlSelection(paramSequence: Int): SqlSelection {
    val (leftSql, leftParams, paramSequence2) = left.toSqlSelection(paramSequence)
    val (rightSql, rightParams, paramSequence3) = right.toSqlSelection(paramSequence2)
    return SqlSelection(
            sql = "($leftSql) AND ($rightSql)",
            params = leftParams + rightParams,
            paramSequence = paramSequence3
    )
}

private fun OrTaskSpecification.toSqlSelection(paramSequence: Int): SqlSelection {
    val (leftSql, leftParams, paramSequence2) = left.toSqlSelection(paramSequence)
    val (rightSql, rightParams, paramSequence3) = right.toSqlSelection(paramSequence2)
    return SqlSelection(
            sql = "($leftSql) OR ($rightSql)",
            params = leftParams + rightParams,
            paramSequence = paramSequence3
    )
}

private fun NotTaskSpecification.toSqlSelection(paramSequence: Int): SqlSelection {
    val (sql, params, paramSequence2) = spec.toSqlSelection(paramSequence)
    return SqlSelection(
            sql = "NOT ($sql)",
            params = params,
            paramSequence = paramSequence2
    )
}

private fun TaskIdIs.toSqlSelection(paramSequence: Int): SqlSelection {
    val (paramName, newParamSequence) = withSequence("id", paramSequence)

    return SqlSelection(
            sql = "${Cols.id} = :$paramName",
            mapOf(paramName to taskId.value),
            paramSequence = newParamSequence
    )
}

private fun TaskOwnerIdIs.toSqlSelection(paramSequence: Int): SqlSelection {
    val (paramName, newParamSequence) = withSequence("ownerId", paramSequence)

    return SqlSelection(
            sql = "${Cols.ownerId} = :$paramName",
            mapOf(paramName to ownerId.value),
            paramSequence = newParamSequence
    )
}

private fun withSequence(paramName: String, paramSequence: Int): Pair<String, Int> {
    val finalName = "$paramName$paramSequence"
    val newParamSequence = paramSequence + 1
    return finalName to newParamSequence
}
