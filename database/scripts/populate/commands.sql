CREATE OR REPLACE FUNCTION populate_commands() RETURNS boolean AS $$
DECLARE
	schema varchar(48) NOT NULL := 'testing';
	count int := 0;
	active boolean;
	verb varchar(16);
	rest_path varchar(16);
	exec_string varchar(255);
BEGIN
	exec_string := format('DELETE FROM "%1$s"."command"', schema);
	EXECUTE exec_string;

	DROP TABLE IF EXISTS note_commands;
	CREATE TEMP TABLE note_commands (
		active boolean NOT NULL,
		verb varchar(16) NOT NULL,
		rest_path varchar(128) NOT NULL
	);

	INSERT INTO "note_commands" VALUES ('true', 'register', '/rest/register');
	INSERT INTO "note_commands" VALUES ('true', 'login', '/rest/login');
	INSERT INTO "note_commands" VALUES ('true', 'logout', '/rest/logout');
	INSERT INTO "note_commands" VALUES ('true', 'ping', '/rest/ping');
	INSERT INTO "note_commands" VALUES ('true', 'create', '/rest/create');
	INSERT INTO "note_commands" VALUES ('true', 'read', '/rest/read');
	INSERT INTO "note_commands" VALUES ('true', 'update', '/rest/update');
	INSERT INTO "note_commands" VALUES ('false', 'delete', '/rest/delete');
	INSERT INTO "note_commands" VALUES ('true', 'toggle', '/rest/toggle');
	INSERT INTO "note_commands" VALUES ('true', 'permission', '/rest/permission');

	FOR active, verb, rest_path IN SELECT "note_commands"."active", "note_commands"."verb", "note_commands"."rest_path" FROM "note_commands" LOOP
		RAISE NOTICE 'Value: %, %, %', active, verb, rest_path;
		exec_string := format('INSERT INTO "%1$s"."command" ("active", "verb", "rest_path") VALUES (''%2$s'', ''%3$s'', ''%4$s'');', schema, active, verb, rest_path);
		EXECUTE exec_string;
		count := count + 1;
	END LOOP;

	RETURN true;

	EXCEPTION WHEN OTHERS THEN

	RAISE NOTICE 'Error: % %', SQLERRM, SQLSTATE;
	RETURN false;
END;
$$ LANGUAGE plpgsql;