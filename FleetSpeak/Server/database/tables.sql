CREATE TABLE users
(
  id serial NOT NULL,
  username character varying(50) NOT NULL,
  alias character varying(50),
  CONSTRAINT users_pkey PRIMARY KEY (id),
  CONSTRAINT users_username_key UNIQUE (username)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE users
  OWNER TO postgres;