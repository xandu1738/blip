create table if not exists partners
(
    id                 bigserial primary key,
    partner_name       varchar(255),
    partner_code       varchar(255) unique not null,
    account_number     varchar(64),
    contact_person     varchar(255),
    contact_phone      varchar(255),
    account_id         bigint,
    business_reference varchar,
    active             boolean default false,
    logo               text,
    package            text    default 'FULL',
    date_created       timestamp(6),
    created_by         varchar(255)
);

create table if not exists file_repository
(
    id            bigserial primary key,
    file_name     varchar(255),
    file_type     varchar(64),
    file_category varchar(64),
    file_size     bigint,
    file_path     text,
    uploaded_by   varchar(255),
    date_uploaded timestamp(6)
);

alter table "system_user"
    add column if not exists partner_code varchar;

alter table vehicle
    add column if not exists partner_code varchar;
