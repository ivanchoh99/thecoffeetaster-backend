-- V4__create_coffee_tables.sql
-- Creates the tables backing the Coffee domain entities:
--   bean_type, coffee, coffee_review
-- Follows the same dual-identifier strategy as the auth tables:
--   BIGINT internal id (PK, FKs) + UUID public identifier (API exposure).

CREATE TABLE bean_type (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL
);

CREATE INDEX idx_bean_type_uuid ON bean_type(uuid);

CREATE TABLE coffee (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    bean_type_id BIGINT REFERENCES bean_type(id) ON DELETE SET NULL,
    origin VARCHAR(255) NOT NULL,
    altitude INT NOT NULL,
    brand VARCHAR(255) NOT NULL,
    process VARCHAR(255) NOT NULL,
    tasting_notes VARCHAR(255) NOT NULL,
    roaster VARCHAR(255) NOT NULL,
    roast_level VARCHAR(255) NOT NULL,
    price VARCHAR(255) NOT NULL,
    date DATE
);

CREATE INDEX idx_coffee_uuid ON coffee(uuid);
CREATE INDEX idx_coffee_bean_type_id ON coffee(bean_type_id);

CREATE TABLE coffee_review (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    coffee_id BIGINT REFERENCES coffee(id) ON DELETE CASCADE,
    author VARCHAR(255) NOT NULL,
    date DATE,
    brew_method VARCHAR(255) NOT NULL,
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 0 AND 10)
);

CREATE INDEX idx_coffee_review_uuid ON coffee_review(uuid);
CREATE INDEX idx_coffee_review_coffee_id ON coffee_review(coffee_id);
