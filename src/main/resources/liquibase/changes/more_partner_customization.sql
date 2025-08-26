alter table route
    add column if not exists partner_code varchar;

alter table route
    add column if not exists estimated_duration_hrs double precision;