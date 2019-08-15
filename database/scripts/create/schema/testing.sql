BEGIN;
CREATE TABLE "testing"."user_role" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "user" integer NOT NULL,
    "role" integer NOT NULL,
    CONSTRAINT user_role_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."role" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "Name" varchar(32) NOT NULL,
    CONSTRAINT role_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."user" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "alias" varchar(32) NOT NULL UNIQUE,
    "password_hash" varchar(10485500) NOT NULL,
    "salt" varchar(10485500) NOT NULL,
    CONSTRAINT user_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."role_permission" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "role" integer NOT NULL,
    "permission" integer NOT NULL,
    CONSTRAINT role_permission_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."organization_user" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "organization" integer NOT NULL,
    "userid" integer NOT NULL,
    CONSTRAINT organization_user_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."permission" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "force" BOOLEAN NOT NULL DEFAULT 'false',
    "command" integer NOT NULL,
    "target" integer NOT NULL,
    CONSTRAINT permission_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."organization" (
    "id" integer NOT NULL,
    "active" BOOLEAN NOT NULL,
    "name" varchar(64) NOT NULL,
    CONSTRAINT organization_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."project" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "name" varchar(64) NOT NULL,
    CONSTRAINT project_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."command" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "verb" integer NOT NULL,
    CONSTRAINT command_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."verb" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "path" varchar(24) NOT NULL UNIQUE,
    CONSTRAINT verb_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."status" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "name" varchar(48) NOT NULL,
    "color" integer NOT NULL,
    CONSTRAINT status_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."note" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "parent" integer,
    "author" integer NOT NULL,
    "title" varchar(128) NOT NULL,
    "description" TEXT NOT NULL,
    "expanded" BOOLEAN NOT NULL DEFAULT 'true',
    "progress" integer NOT NULL,
    "status" integer NOT NULL,
    CONSTRAINT note_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."note_tag" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "note" integer NOT NULL,
    "tag" integer NOT NULL,
    "color" integer NOT NULL,
    CONSTRAINT note_tag_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."tag" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "name" varchar(48) NOT NULL,
    CONSTRAINT tag_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."user_permission" (
    "id" serial NOT NULL,
    "Active" BOOLEAN NOT NULL DEFAULT 'true',
    "user" integer NOT NULL,
    "permission" integer NOT NULL,
    CONSTRAINT user_permission_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."color" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "name" varchar(24) NOT NULL UNIQUE,
    "hex" VARCHAR(7) NOT NULL UNIQUE,
    CONSTRAINT color_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."user_note" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "user" integer NOT NULL,
    "note" integer NOT NULL UNIQUE,
    CONSTRAINT user_note_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."organization_note" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "organization" integer NOT NULL,
    "note" integer NOT NULL,
    CONSTRAINT organization_note_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."project_note" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "project" integer NOT NULL,
    "note" integer NOT NULL,
    CONSTRAINT project_note_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."organization_project" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL,
    "organization" integer NOT NULL,
    "project" integer NOT NULL,
    CONSTRAINT organization_project_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."target" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "table" integer NOT NULL,
    "target_id" integer,
    CONSTRAINT target_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."organization_role" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "organization" integer NOT NULL,
    "role" integer NOT NULL,
    CONSTRAINT organization_role_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."session" (
    "id" serial NOT NULL,
    "token" varchar(36) NOT NULL UNIQUE,
    "app_session" varchar(36) NOT NULL UNIQUE,
    "expires" TIMESTAMP NOT NULL,
    CONSTRAINT session_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."system_config" (
    "id" serial NOT NULL,
    "version" DECIMAL NOT NULL,
    "created" TIMESTAMP NOT NULL,
    "modified" TIMESTAMP,
    CONSTRAINT system_config_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."user_session" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "user" integer NOT NULL UNIQUE,
    "session" integer NOT NULL UNIQUE,
    CONSTRAINT user_session_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."table" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "name" varchar(255) NOT NULL UNIQUE,
    CONSTRAINT table_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."column" (
    "id" serial NOT NULL,
    "name" varchar(255) NOT NULL,
    "type" varchar(255) NOT NULL,
    CONSTRAINT column_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "testing"."table_column" (
    "id" serial NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT 'true',
    "table" integer NOT NULL,
    "column" integer NOT NULL,
    CONSTRAINT table_column_pk PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



ALTER TABLE "testing"."user_role" ADD CONSTRAINT "user_role_fk0" FOREIGN KEY ("user") REFERENCES "testing"."user"("id");
ALTER TABLE "testing"."user_role" ADD CONSTRAINT "user_role_fk1" FOREIGN KEY ("role") REFERENCES "testing"."role"("id");



ALTER TABLE "testing"."role_permission" ADD CONSTRAINT "role_permission_fk0" FOREIGN KEY ("role") REFERENCES "testing"."role"("id");
ALTER TABLE "testing"."role_permission" ADD CONSTRAINT "role_permission_fk1" FOREIGN KEY ("permission") REFERENCES "testing"."permission"("id");

ALTER TABLE "testing"."organization_user" ADD CONSTRAINT "organization_user_fk0" FOREIGN KEY ("organization") REFERENCES "testing"."organization"("id");
ALTER TABLE "testing"."organization_user" ADD CONSTRAINT "organization_user_fk1" FOREIGN KEY ("userid") REFERENCES "testing"."user"("id");

ALTER TABLE "testing"."permission" ADD CONSTRAINT "permission_fk0" FOREIGN KEY ("command") REFERENCES "testing"."command"("id");
ALTER TABLE "testing"."permission" ADD CONSTRAINT "permission_fk1" FOREIGN KEY ("target") REFERENCES "testing"."target"("id");



ALTER TABLE "testing"."command" ADD CONSTRAINT "command_fk0" FOREIGN KEY ("verb") REFERENCES "testing"."verb"("id");


ALTER TABLE "testing"."status" ADD CONSTRAINT "status_fk0" FOREIGN KEY ("color") REFERENCES "testing"."color"("id");

ALTER TABLE "testing"."note" ADD CONSTRAINT "note_fk0" FOREIGN KEY ("parent") REFERENCES "testing"."note"("id");
ALTER TABLE "testing"."note" ADD CONSTRAINT "note_fk1" FOREIGN KEY ("author") REFERENCES "testing"."user"("id");
ALTER TABLE "testing"."note" ADD CONSTRAINT "note_fk2" FOREIGN KEY ("status") REFERENCES "testing"."status"("id");

ALTER TABLE "testing"."note_tag" ADD CONSTRAINT "note_tag_fk0" FOREIGN KEY ("note") REFERENCES "testing"."note"("id");
ALTER TABLE "testing"."note_tag" ADD CONSTRAINT "note_tag_fk1" FOREIGN KEY ("tag") REFERENCES "testing"."tag"("id");
ALTER TABLE "testing"."note_tag" ADD CONSTRAINT "note_tag_fk2" FOREIGN KEY ("color") REFERENCES "testing"."color"("id");


ALTER TABLE "testing"."user_permission" ADD CONSTRAINT "user_permission_fk0" FOREIGN KEY ("user") REFERENCES "testing"."user"("id");
ALTER TABLE "testing"."user_permission" ADD CONSTRAINT "user_permission_fk1" FOREIGN KEY ("permission") REFERENCES "testing"."permission"("id");


ALTER TABLE "testing"."user_note" ADD CONSTRAINT "user_note_fk0" FOREIGN KEY ("user") REFERENCES "testing"."user"("id");
ALTER TABLE "testing"."user_note" ADD CONSTRAINT "user_note_fk1" FOREIGN KEY ("note") REFERENCES "testing"."note"("id");

ALTER TABLE "testing"."organization_note" ADD CONSTRAINT "organization_note_fk0" FOREIGN KEY ("organization") REFERENCES "testing"."organization"("id");
ALTER TABLE "testing"."organization_note" ADD CONSTRAINT "organization_note_fk1" FOREIGN KEY ("note") REFERENCES "testing"."note"("id");

ALTER TABLE "testing"."project_note" ADD CONSTRAINT "project_note_fk0" FOREIGN KEY ("project") REFERENCES "testing"."project"("id");
ALTER TABLE "testing"."project_note" ADD CONSTRAINT "project_note_fk1" FOREIGN KEY ("note") REFERENCES "testing"."note"("id");

ALTER TABLE "testing"."organization_project" ADD CONSTRAINT "organization_project_fk0" FOREIGN KEY ("organization") REFERENCES "testing"."organization"("id");
ALTER TABLE "testing"."organization_project" ADD CONSTRAINT "organization_project_fk1" FOREIGN KEY ("project") REFERENCES "testing"."project"("id");

ALTER TABLE "testing"."target" ADD CONSTRAINT "target_fk0" FOREIGN KEY ("table") REFERENCES "testing"."table"("id");

ALTER TABLE "testing"."organization_role" ADD CONSTRAINT "organization_role_fk0" FOREIGN KEY ("organization") REFERENCES "testing"."organization"("id");
ALTER TABLE "testing"."organization_role" ADD CONSTRAINT "organization_role_fk1" FOREIGN KEY ("role") REFERENCES "testing"."role"("id");



ALTER TABLE "testing"."user_session" ADD CONSTRAINT "user_session_fk0" FOREIGN KEY ("user") REFERENCES "testing"."user"("id");
ALTER TABLE "testing"."user_session" ADD CONSTRAINT "user_session_fk1" FOREIGN KEY ("session") REFERENCES "testing"."session"("id");



ALTER TABLE "testing"."table_column" ADD CONSTRAINT "table_column_fk0" FOREIGN KEY ("table") REFERENCES "testing"."table"("id");
ALTER TABLE "testing"."table_column" ADD CONSTRAINT "table_column_fk1" FOREIGN KEY ("column") REFERENCES "testing"."column"("id");

COMMIT;