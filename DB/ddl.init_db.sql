DROP TABLE IF EXISTS word_translate;
DROP INDEX IF EXISTS groups_id_uindex;
DROP TABLE IF EXISTS word_groups CASCADE ;
DROP TABLE IF EXISTS user_groups;
DROP TABLE IF EXISTS user_words;
DROP TABLE IF EXISTS user_session;
DROP TABLE IF EXISTS words;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS users;

DROP SEQUENCE IF EXISTS word_translate_id_seq;
DROP SEQUENCE IF EXISTS hibernate_sequence;
DROP SEQUENCE IF EXISTS groups_id_seq;
DROP SEQUENCE IF EXISTS words_id_seq;
DROP SEQUENCE IF EXISTS user_session_id_seq;
DROP SEQUENCE IF EXISTS user_id_seq;

CREATE TABLE users
(
  id SERIAL PRIMARY KEY NOT NULL,
  email VARCHAR(255),
  login VARCHAR(255),
  password VARCHAR(255),
  role SMALLINT DEFAULT 0
);

CREATE TABLE groups
(
  id BIGINT PRIMARY KEY NOT NULL,
  name VARCHAR(250),
  comment VARCHAR(1023),
  user_id BIGINT,
  CONSTRAINT groups_user_fk FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE words
(
  id BIGINT PRIMARY KEY NOT NULL,
  native_word VARCHAR(255),
  description VARCHAR(255),
  comment VARCHAR(1023),
  language VARCHAR(3)
);

CREATE TABLE word_groups
(
  id SERIAL PRIMARY KEY NOT NULL,
  word_id BIGINT,
  group_id BIGINT,
  CONSTRAINT word_groups_words_fk FOREIGN KEY (word_id) REFERENCES words (id),
  CONSTRAINT word_groups_groups_fk FOREIGN KEY (group_id) REFERENCES groups (id)
);

CREATE TABLE  word_translate
(
  id SERIAL PRIMARY KEY NOT NULL,
  native_word_id BIGINT,
  translate_word_id BIGINT,
  CONSTRAINT  word_translate_words_fk FOREIGN KEY (native_word_id) REFERENCES words (id),
  CONSTRAINT  word_translate_translate_fk FOREIGN KEY (translate_word_id) REFERENCES words (id)
);

CREATE SEQUENCE groups_id_seq NO MINVALUE NO MAXVALUE NO CYCLE;
ALTER TABLE groups ALTER COLUMN id SET DEFAULT nextval('groups_id_seq');
ALTER SEQUENCE groups_id_seq OWNED BY groups.id;

CREATE SEQUENCE words_id_seq MINVALUE 2003 NO MAXVALUE NO CYCLE;
ALTER TABLE words ALTER COLUMN id SET DEFAULT nextval('words_id_seq');
ALTER SEQUENCE words_id_seq OWNED BY words.id;

DROP TRIGGER IF EXISTS check_delete_translate ON words;
drop FUNCTION IF EXISTS delete_translate_by_word();

CREATE FUNCTION delete_translate_by_word() RETURNS trigger AS $$
BEGIN
  delete from word_translate where (word_translate.translate_word_id = old.id) or (word_translate.native_word_id = old.id);
  RETURN old;
END;
$$ LANGUAGE  plpgsql;

CREATE TRIGGER check_delete_translate
  BEFORE DELETE ON words
  FOR EACH ROW
  EXECUTE PROCEDURE delete_translate_by_word();

CREATE TABLE user_session
(
  id SERIAL PRIMARY KEY NOT NULL,
  user_id BIGINT NOT NULL,
  ip VARCHAR(15) NOT NULL,
  time TIMESTAMP NOT NULL,
  hashcode VARCHAR(40) NOT NULL,
  user_agent VARCHAR(512),
  CONSTRAINT session_user_fk FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE UNIQUE INDEX session_hashcode_uindex ON user_session (hashcode);