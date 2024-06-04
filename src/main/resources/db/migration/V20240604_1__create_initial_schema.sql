CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    photo TEXT,
    birth_date DATE NOT NULL,
    role VARCHAR(255) NOT NULL
);

CREATE TABLE posts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_user UUID NOT NULL,
    date_post TIMESTAMP NOT NULL,
    status VARCHAR(255) NOT NULL,
    text VARCHAR(255) NOT NULL,
    upvote_count INT NOT NULL,
    comment_count INT NOT NULL,
    FOREIGN KEY (id_user) REFERENCES users(id)
);

CREATE TABLE upvotes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_user UUID NOT NULL,
    id_post UUID NOT NULL,
    FOREIGN KEY (id_user) REFERENCES users(id),
    FOREIGN KEY (id_post) REFERENCES posts(id)
);

CREATE TABLE comments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_user UUID NOT NULL,
    id_post UUID NOT NULL,
    text VARCHAR(255) NOT NULL,
    date_comment TIMESTAMP NOT NULL,
    answer_count INT NOT NULL,
    FOREIGN KEY (id_user) REFERENCES users(id),
    FOREIGN KEY (id_post) REFERENCES posts(id)
);

CREATE TABLE answers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    id_user UUID NOT NULL,
    id_comment UUID NOT NULL,
    date_answer TIMESTAMP NOT NULL,
    text VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_user) REFERENCES users(id),
    FOREIGN KEY (id_comment) REFERENCES comments(id)
);