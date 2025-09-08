create table if not exists modules(
    id serial8 primary key,
    name varchar(255),
    code varchar(255),
    description varchar(255),
    created_at timestamp default now(),
    created_by int8
);

create table if not exists subscriptions(
    id serial8 primary key,
    partner_code varchar(255),
    module_code varchar(255),
    status varchar(30),
    created_at timestamp default now(),
    created_by int8,
    start_date timestamp,
    end_date timestamp
);