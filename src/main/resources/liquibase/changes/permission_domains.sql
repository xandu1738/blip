alter table system_permission
    drop column if exists domain;

alter table system_permission
    add column if not exists domains varchar[];