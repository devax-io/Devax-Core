    CREATE TABLE IF NOT EXISTS  account
        (id SERIAL PRIMARY KEY,
        address VARCHAR(255) unique not null,
        name VARCHAR(255) unique not null,
        type VARCHAR(255) not null,
        alias VARCHAR(255),
        contact VARCHAR(255),
        more_info VARCHAR(255),
        create_date VARCHAR(255) not null ,
        CONSTRAINT uc_account UNIQUE (name,address)) ;


    CREATE TABLE IF NOT EXISTS  balance_report
        (id SERIAL PRIMARY KEY,
        owner VARCHAR(255) unique not null,
        balance VARCHAR(255)  not null,
        last_modified VARCHAR(255),
        blocked VARCHAR(255),
        more_info VARCHAR(255)) ;


    CREATE TABLE IF NOT EXISTS  lot
        (id SERIAL PRIMARY KEY,
        ref_id VARCHAR(255)  not null,
        manufacture VARCHAR(255)  not null,
        pod VARCHAR(255) not null,
        exp VARCHAR(255) not null,
        orig VARCHAR(255),
        agent VARCHAR(255) not null,
        cost VARCHAR(255),
        payment_proof VARCHAR(255),
        create_date VARCHAR(255),
        more_info VARCHAR(255),
        CONSTRAINT uc_lot UNIQUE (ref_id,manufacture))  ;


    CREATE TABLE IF NOT EXISTS  sign
        (id SERIAL PRIMARY KEY,
        agent VARCHAR(255)  not null,
        act VARCHAR(255)  not null,
        type VARCHAR(255),
        create_date VARCHAR(255),
        more_info VARCHAR(255)) ;

   CREATE TABLE IF NOT EXISTS  tx
        (id SERIAL PRIMARY KEY,
        src VARCHAR(255)  not null,
        dest VARCHAR(255)  not null,
        value VARCHAR(255),
        vial VARCHAR(255),
        create_date VARCHAR(255),
        more_info VARCHAR(255)) ;

   CREATE TABLE IF NOT EXISTS  vial
        (id SERIAL PRIMARY KEY,
        ref_id VARCHAR(255) not null unique,
        parent_id VARCHAR(255)  not null,
        create_date VARCHAR(255),
        current_owner VARCHAR(255) not null,
        more_info VARCHAR(255),
        CONSTRAINT uc_vial UNIQUE (ref_id,parent_id) ) ;
