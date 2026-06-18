alter table partners
    add column if not exists archived boolean default false;