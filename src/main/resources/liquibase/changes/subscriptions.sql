create table if not exists subscription_plans
(
    id         serial8 primary key,
    name       varchar,
    created_by int8,
    created_on timestamp
);

alter table subscription_plans
    add column if not exists description varchar;

create table if not exists partner_subscriptions
(
    id                serial8 primary key,
    partner_id        int8,
    plan_id           int8,
    payment_id        int8,
    start_date        timestamp,
    end_date          timestamp,
    status            varchar,
    renewal_type      varchar,
    renewal_date      timestamp,
    cancellation_date timestamp
);