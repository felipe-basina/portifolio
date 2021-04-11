CREATE TABLE t_contact
(
    idt          serial PRIMARY KEY,
    contact_name VARCHAR(50) NOT NULL,
    email        VARCHAR(30) NOT NULL,
    phone_number VARCHAR(11) NOT NULL,
    owner_idt    serial      NOT NULL,
    avatar       bytea,
    creation_dt  TIMESTAMP DEFAULT (current_timestamp AT TIME ZONE 'America/Sao_Paulo'),
    update_dt    TIMESTAMP DEFAULT (current_timestamp AT TIME ZONE 'America/Sao_Paulo'),
    CONSTRAINT fk_owner FOREIGN KEY (owner_idt) REFERENCES t_owner (idt)
);