create table if not exists subscription_requests
(
    id                serial8 primary key not null,
    partner_code      varchar,
    user_id           int8,
    subscription_plan int8,
    status            varchar(30),
    requested_on      timestamp default now()
);

alter table subscription_requests
    add column if not exists subscription_reference varchar;

drop index if exists idx_subscription_requests_id;

create index idx_subscription_requests_id on subscription_requests (id);
