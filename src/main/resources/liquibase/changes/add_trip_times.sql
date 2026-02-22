-- add set off time and estimated arrival time to trips
alter table trips
    add column if not exists set_off_time timestamp;
alter table trips
    add column if not exists estimated_arrival_time timestamp;
