CREATE TABLE samples
(
    sample_id   serial PRIMARY KEY,
    description VARCHAR(50) NOT NULL,
    created_on  TIMESTAMP   NOT NULL
);

insert into samples (description, created_on)
values ('Sample data', now());
insert into samples (description, created_on)
values ('Another sample data', now());