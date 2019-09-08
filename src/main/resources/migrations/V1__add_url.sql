CREATE TABLE url
(
     id char(8) PRIMARY KEY NOT NULL,
     url TEXT NOT NULL,
     createdAt timestamptz NOT NULL
);

GRANT SELECT, INSERT ON url TO url_shortener_user;
