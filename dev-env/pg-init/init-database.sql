CREATE ROLE "task-app" WITH LOGIN INHERIT;

GRANT CONNECT ON DATABASE task TO "task-app";

CREATE SCHEMA IF NOT EXISTS task;
GRANT USAGE ON SCHEMA task TO "task-app";

ALTER DEFAULT PRIVILEGES IN SCHEMA task GRANT ALL ON TABLES TO "task-app";
ALTER DEFAULT PRIVILEGES IN SCHEMA task GRANT ALL ON SEQUENCES TO "task-app";
ALTER DEFAULT PRIVILEGES IN SCHEMA task GRANT ALL ON FUNCTIONS TO "task-app";
ALTER DEFAULT PRIVILEGES IN SCHEMA task  GRANT ALL ON TYPES TO "task-app";

GRANT ALL ON ALL TABLES IN SCHEMA task TO "task-app";
GRANT USAGE ON ALL SEQUENCES IN SCHEMA task TO "task-app";
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA task TO "task-app";
