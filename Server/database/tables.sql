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
  CONSTRAINT users_pkey PRIMARY KEY (id),
  CONSTRAINT users_username_key UNIQUE (username)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE users
  OWNER TO postgres;

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

CREATE TABLE permission-levels
(
  id integer NOT NULL,
  name character varying(50) NOT NULL,
  CONSTRAINT permission-levels_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE permission-levels
  OWNER TO postgres;
  
CREATE TABLE permission-types
(
  id integer NOT NULL,
  name character varying(50) NOT NULL,
  CONSTRAINT permission-types_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE permission-types
  OWNER TO postgres;

CREATE TABLE permission-groups
(
  id integer NOT NULL,
  name character varying(50) NOT NULL,
  CONSTRAINT permission-groups_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE permission-groups
  OWNER TO postgres;
/*
*******************************************************************
 Relationships
*******************************************************************
*/
CREATE TABLE building_permission-type_maps
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
