CREATE SCHEMA if NOT EXISTS gallery;

SET search_path TO gallery;

CREATE SEQUENCE id_comments_sequence_generator START WITH 5000 INCREMENT BY 1 CACHE 100;

CREATE TABLE comments
(
    id         bigint PRIMARY KEY DEFAULT nextval('id_comments_sequence_generator'),
    image_id   bigint       NOT NULL,
    user_id    bigint       NOT NULL,
    content    VARCHAR(500) NOT NULL,
    created_at timestamptz  NOT NULL DEFAULT CURRENT_TIMESTAMP
);