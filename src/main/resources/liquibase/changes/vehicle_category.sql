create table if not exists vehicle_category
(
    id            serial8 primary key,
    name          varchar(50),
    description   text,
    category_code varchar(50),
    amenities     text[],
    active        boolean default true
);

create table if not exists amenities
(
    id          serial8 primary key,
    name        varchar(50),
    description text,
    code        varchar(50)
);

alter table vehicle
    add column if not exists vehicle_category varchar(50);