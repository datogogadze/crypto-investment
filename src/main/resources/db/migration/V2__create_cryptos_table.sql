create table cryptos
(
    id bigserial primary key,
    crypto_name_id bigserial REFERENCES crypto_names (id),
    price double precision not null,
    timestamp timestamp,
    constraint cryptos_unique_timestamp_name_id_constraint unique (timestamp, crypto_name_id)
);