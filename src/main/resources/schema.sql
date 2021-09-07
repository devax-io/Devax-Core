CREATE TABLE IF NOT EXISTS  account
    (id SERIAL PRIMARY KEY,
    address VARCHAR(255) unique not null,
    name VARCHAR(255) unique not null,
    type VARCHAR(255),
    alias VARCHAR(255),
    contact VARCHAR(255),
    more_info VARCHAR(255),
    create_date VARCHAR(255) not null ,
    CONSTRAINT uc_account UNIQUE (name,alias)) ;


