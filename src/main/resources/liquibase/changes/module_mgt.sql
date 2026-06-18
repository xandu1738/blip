alter table modules
    add column if not exists archived boolean default false;