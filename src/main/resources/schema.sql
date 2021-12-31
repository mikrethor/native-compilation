-- schema.sql
CREATE TABLE IF NOT EXISTS MESSAGE (
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    message VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);