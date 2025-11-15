alter table subscription_plans
    add column if not exists plan_code varchar;

alter table subscriptions
    add column subscription_plan varchar;