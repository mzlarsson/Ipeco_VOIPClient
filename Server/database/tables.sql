/*
*******************************************************************
 Entities
*******************************************************************
*/
CREATE TABLE users
(
  id serial NOT NULL,
  username character varying(50) NOT NULL,
  alias character varying(50),
  password character varying(110) NOT NULL,
  CONSTRAINT users_pkey PRIMARY KEY (id),
  CONSTRAINT users_username_key UNIQUE (username)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE users
  OWNER TO postgres;
ALTER SEQUENCE users_id_seq
  RESTART WITH 10000;

CREATE TABLE rooms
(
  id serial NOT NULL,
  name character varying(50) NOT NULL,
  CONSTRAINT rooms_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE rooms
  OWNER TO postgres;

CREATE TABLE buildings
(
  id serial NOT NULL,
  name character varying(50) NOT NULL,
  CONSTRAINT buildings_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE buildings
  OWNER TO postgres;

CREATE TABLE permission_levels
(
  id integer NOT NULL,
  name character varying(50) NOT NULL,
  CONSTRAINT permission_levels_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE permission_levels
  OWNER TO postgres;
  
CREATE TABLE permission_types
(
  id integer NOT NULL,
  name character varying(50) NOT NULL,
  CONSTRAINT permission_types_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE permission_types
  OWNER TO postgres;

CREATE TABLE permission_groups
(
  id integer NOT NULL,
  name character varying(50) NOT NULL,
  CONSTRAINT permission_groups_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE permission_groups
  OWNER TO postgres;
/*
*******************************************************************
 Relationships
*******************************************************************
*/
/*
CREATE TABLE building_permission_type_maps
(
  id integer NOT NULL,
  name character varying(50) NOT NULL,
  CONSTRAINT permission_groups_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE permission_groups
  OWNER TO postgres;
*/