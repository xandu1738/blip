create table if not exists seats(
    id           serial8 primary key,
    bus_id       int8,
    seat_number  varchar(10),
    seat_type    varchar(20),
    status       varchar(20)
);

create table if not exists tickets(
    id                serial8 primary key,
    ticket_number     varchar(30),
    booking_id        int8,
    user_id           int8,
    payment_id        int8,
    fare_amount       numeric(10, 2),
    currency          varchar(3),
    status            varchar(30),
    check_in_time     timestamp,
    checked_in_by     int8,
    qr_code           varchar(30),
    created_at        timestamp default now(),
    validation_status varchar(30)
);