CREATE TABLE t_owner
(
    idt         serial PRIMARY KEY,
    owner_name  VARCHAR(50)  NOT NULL,
    email       VARCHAR(30)  NOT NULL,
    owner_pass  VARCHAR(100) NOT NULL,
    avatar      bytea,
    creation_dt TIMESTAMP DEFAULT (current_timestamp AT TIME ZONE 'America/Sao_Paulo'),
    update_dt   TIMESTAMP DEFAULT (current_timestamp AT TIME ZONE 'America/Sao_Paulo')
);