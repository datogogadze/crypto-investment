create table imported_files
(
    id bigserial primary key,
    name varchar,
    constraint imported_files_unique_name_constraint unique (name)
);