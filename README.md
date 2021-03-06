# repository-test-example

A sample codebase accompanying the following article:
[A guide to safely and efficiently test code that uses repositories](https://medium.com/nerds-malt/a-guide-to-safely-and-efficiently-test-code-that-uses-repositories-c91effb41dd0).


## What does the code do?

A shell application is built to allow users to manage their tasks.

A task is a very simple concept here: it's composed of a summary and a description, and it belongs
to an owner. This is enough for our demonstration, and it already allows for imagining several
realistic use cases.

## Points of interest

* A repository interface speaking the language of the domain (i.e. a compile-time contract):
  [TaskRepository](task-domain/src/main/kotlin/com/malt/task/TaskRepository.kt)
* Automated tests defining the runtime contract that implementations of the above interface must respect:
  [TaskRepositoryContract](task-domain/src/test/kotlin/com/malt/task/TaskRepositoryContract.kt)
* A (so-called) "Fake" in-memory implementation of the repository contract:
  [InMemoryTaskRepository](task-domain/src/test/kotlin/com/malt/task/test/InMemoryTaskRepository.kt)
  (and its tests: [InMemoryTaskRepositoryTest](task-domain/src/test/kotlin/com/malt/task/test/InMemoryTaskRepositoryTest.kt)).  
  (Note: a Fake may expose more operations than the contract, for instance here: a `clear` method.)
* An implementation of the repository contract using PostgreSQL:
  [PostgresqlTaskRepository](adapters/task-persistence/src/main/kotlin/com/malt/task/PostgresqlTaskRepository.kt)
  (and its tests: [PostgresqlTaskRepositoryTest](adapters/task-persistence/src/test/kotlin/com/malt/task/PostgresqlTaskRepositoryTest.kt))
* A way to easily use [Testcontainers](https://www.testcontainers.org/) with Spring:
  [PostgresqlTest](common/test-utils/src/main/java/com/malt/test/postgres/PostgresqlTest.java)
* The contract tests guarantee that the in-memory repository is as much a valid implementation of
  the repository as the PostgreSQl one. Those two implementations are interchangeable.
* Fixtures for easily writing unit tests using of the in-memory implementation of the contract:
  [TaskFixtures](task-domain/src/test/kotlin/com/malt/task/test/TaskFixtures.kt),
  [TaskCommandsFixtures](task-shell-app/src/test/kotlin/com/malt/task/TaskCommandsFixtures.kt)
* No test uses "Mocks", neither manually written nor generated by a library. Instead, tests use the
  actual implementations of all classes, except that in-memory repositories are used.
  (But as written earlier, in-memory repositories are fully valid implementations.).
  See [TaskMergeServiceTest](task-domain/src/test/kotlin/com/malt/task/TaskMergeServiceTest.kt),
  [CurrentUserTaskServiceTest](task-shell-app/src/test/kotlin/com/malt/task/CurrentUserTaskServiceTest.kt)
  or [TaskCommandsTest](task-shell-app/src/test/kotlin/com/malt/task/TaskCommandsTest.kt)
  for examples of unit tests making use of it, both in the domain module and in the app module.

While not related to our main demonstration, the code also features:

* The use of [Liquibase](https://www.liquibase.org/) to evolve our DB schema. See
  [db.changelog-master.yaml](task-shell-app/src/main/resources/db/changelog/db.changelog-master.yaml),
  [task-00002-create-task-table.sql](adapters/task-persistence/src/main/resources/db/changelog/task/task-00002-create-task-table.sql),
  etc.
* An example of the [Specification pattern](https://en.wikipedia.org/wiki/Specification_pattern):
  [TaskSpecifications](task-domain/src/main/kotlin/com/malt/task/TaskSpecifications.kt) and a way
  to [map such specifications to SQL queries](adapters/task-persistence/src/main/kotlin/com/malt/task/SqlSelection.kt).
* The extraction of common test fixtures in a single place to make tests more maintainable:
  [TaskFixtures](task-domain/src/test/kotlin/com/malt/task/test/TaskFixtures.kt) and
  [TaskCommandsFixtures](task-shell-app/src/test/kotlin/com/malt/task/TaskCommandsFixtures.kt)
* A builder to easily create test tasks: [TaskBuilder](task-domain/src/test/kotlin/com/malt/task/test/TaskBuilder.kt)

## Why a shell application?

Because:

1. for this example, it doesn't matter which kind of application is built
2. we work with Web apps every day :-) 

## How to run the shell app

Unfortunately the shell doesn't play well with Spring's `bootRun` task, that's why the app must be
built first:

```sh
# build the app
./gradle build

# start the DB
docker-compose -f dev-env/docker-compose.yml start

# start the app
java -jar task-shell-app/build/libs/task-shell-app-0.0.1-SNAPSHOT.jar

# play a bit with the app, type "quit" to quit

# stop the DB
docker-compose -f dev-env/docker-compose.yml stop
```
