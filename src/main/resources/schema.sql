CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  description VARCHAR(512) NOT NULL,
  requestor_id int NOT null REFERENCES users(id),
  CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512) NOT NULL,
  is_available bool NOT NULL,
  owner_id int NOT null REFERENCES users(id),
  request_id int REFERENCES requests(id),
  CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_time timestamp without time zone NOT NULL,
  end_time timestamp without time zone,
  item_id int NOT null REFERENCES items(id),
  booker_id int NOT null REFERENCES users(id),
  status VARCHAR(8) NOT NULL,
  CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  text VARCHAR(512) NOT NULL,
  item_id int NOT null REFERENCES items(id),
  author_id int NOT null REFERENCES users(id),
  CONSTRAINT pk_comment PRIMARY KEY (id)
);