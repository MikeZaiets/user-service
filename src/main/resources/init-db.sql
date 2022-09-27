GRANT ALL PRIVILEGES ON DATABASE postgres TO CURRENT_USER;

DROP SCHEMA IF EXISTS user_service_schema CASCADE;

CREATE SCHEMA IF NOT EXISTS user_service_schema;

SET SCHEMA 'user_service_schema';

CREATE TABLE IF NOT EXISTS address
(
    id BIGSERIAL PRIMARY KEY,
    country      VARCHAR(100) NOT NULL,
    city         VARCHAR(100) NOT NULL,
    street       VARCHAR(100) NOT NULL,
    house_number VARCHAR(20)  NOT NULL,
    apt_number   INTEGER,
    zipcode      INTEGER      NOT NULL,
    UNIQUE (country, city, street, house_number, apt_number, zipcode)
);

CREATE TABLE IF NOT EXISTS "users"
(
    id BIGSERIAL PRIMARY KEY,
    email      VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    phone      VARCHAR(20)  NOT NULL,
    birth_date DATE DEFAULT now(),
    address_id BIGINT,
    FOREIGN KEY (address_id) REFERENCES address (id)
        ON UPDATE CASCADE ON DELETE SET NULL
);
