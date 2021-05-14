# repository-test-example
A sample codebase demonstrating how to write efficient and flexible automated tests when using
repositories that access a database.

## Points of interest

TODO

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
