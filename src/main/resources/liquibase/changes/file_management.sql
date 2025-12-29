drop table if exists file_repository;

create table if not exists file_repository
(
    id            bigserial
        primary key,
    description   varchar not null,
    file_category varchar not null,
    created_at    timestamp default now(),
    file_path     varchar
);

alter table file_repository
    add column if not exists
        entity_id int8;

alter table file_repository
    add column if not exists
        file_url text;

alter table file_repository
    add column if not exists
        added_by int8;