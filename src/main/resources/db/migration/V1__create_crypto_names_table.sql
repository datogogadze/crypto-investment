create table crypto_names
(
    id bigserial primary key,
    name varchar,
    constraint crypto_names_unique_name_constraint unique (name)
);