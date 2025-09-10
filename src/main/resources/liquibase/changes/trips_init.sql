-- add trip [route, bus, driver, date]
create table if not exists trips
(
    id         serial8 primary key,
    route_id   int8 not null,
    bus_id     int8 not null,
    driver_id  int8,
    trip_date  date,
    status     varchar(20),
    created_at timestamp,
    created_by int8
);
-- trip seats [seat,charge, status]
create table if not exists trip_seats
(
    id        serial8 primary key,
    seat_id   int8,
    trip_id   int8,
    status    varchar(20),
    booked_at timestamp,
    booked_by int8
);
-- add districts
create table if not exists districts
(
    id            serial8 primary key,
    name          varchar(30),
    district_code varchar(30) unique,
    district_id   varchar(30) unique
);
