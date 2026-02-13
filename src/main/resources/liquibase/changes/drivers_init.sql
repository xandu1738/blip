create table if not exists drivers (
    id serial8 primary key,
    name varchar(255),
    license_number varchar(255),
    contact_number varchar(255),
    status varchar(255),
    partner_code varchar(255),
    created_at timestamp default now(),
    created_by int8
);