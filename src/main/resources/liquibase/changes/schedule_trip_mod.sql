alter table schedule
    add column if not exists partner_code varchar;

alter table schedule
    add column if not exists expected_arrival_time time;

alter table schedule
    add column if not exists set_off_time time;

alter table schedule
    add column if not exists days_of_week text[];

alter table schedule
    drop column if exists vehicle_id;

alter table schedule
    drop column if exists departure_time;

alter table trips
    add column if not exists schedule_id int8;