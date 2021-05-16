--liquibase formatted sql
--changeset dev-team:task-00002-create-task-table logicalFilePath:fixed

CREATE SCHEMA IF NOT EXISTS task;

CREATE TABLE IF NOT EXISTS task.tasks
(
    id            TEXT                     NOT NULL
        CONSTRAINT tasks_pk PRIMARY KEY,
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL,
    -- in a real system, it would likely be a foreign key referencing a `users` table
    owner_id      TEXT                     NOT NULL,
    summary       TEXT                     NOT NULL,
    description   TEXT                     NULL
)
