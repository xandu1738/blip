alter table "system_user"
    add column if not exists phone_number varchar(15);

create table if not exists access_logs
(
    id           serial8 primary key,
    username     varchar(255),
    method       varchar(10)                            not null,
    path         varchar(255)                           not null,
    address      varchar(255),
    content_type varchar(255),
    request      text,
    response     text                                   not null,
    query_params text,
    user_agent   text,
    is_error     boolean      default false             not null,
    created_at   timestamp(6) default CURRENT_TIMESTAMP not null,
    partner_code varchar(255),
    client_info  varchar      default '{}'::jsonb
);

create
    or replace function prevent_update_delete_access_logs() returns trigger
    language plpgsql
as
$$
BEGIN
    IF
        TG_OP = 'UPDATE' THEN
        RAISE EXCEPTION 'Updates to access_logs table are not allowed.';
    ELSEIF
        TG_OP = 'DELETE' THEN
        RAISE EXCEPTION 'Deletes from access_logs table are not allowed.';
    ELSEIF
        TG_OP = 'TRUNCATE' THEN
        RAISE EXCEPTION 'Truncations from access_logs table are not allowed.';
    END IF;
    RETURN NULL;
END;
$$;

create trigger prevent_update_delete
    before update or
        delete
    on access_logs
    for each row
execute procedure prevent_update_delete_access_logs();

create table if not exists vehicle
(
    id                  serial8 primary key,
    registration_number varchar(20) unique                     not null,
    capacity            integer                                not null,
    category            varchar(50)                            not null, -- e.g., Passenger, Cargo
    type                varchar(20)                            not null, -- Bus, Van, Truck, etc.
    status              varchar(20)  default 'ACTIVE'          not null, -- active, inactive, maintenance
    created_at          timestamp(6) default CURRENT_TIMESTAMP not null,
    created_by          int8                                   not null  -- username of the creator
);

create table if not exists route
(
    id                 serial8 primary key,
    origin             varchar(100)                           not null,
    destination        varchar(100)                           not null,
    estimated_distance numeric(10, 2)                         not null, -- in kilometers
    estimated_time     interval                               not null, -- e.g., '1 hour 30 minutes'
    status             varchar(20)  default 'ACTIVE'          not null, -- active, inactive
    created_at         timestamp(6) default CURRENT_TIMESTAMP not null,
    created_by         int8                                   not null  -- username of the creator
);

create table if not exists stop
(
    id          serial8 primary key,
    route_id    int8                                   not null,
    name        varchar(100)                           not null,
    order_index integer                                not null, -- Order of the stop in the route
    crated_at   timestamp(6) default CURRENT_TIMESTAMP not null,
    created_by  int8                                   not null  -- username of the creator
);

create table if not exists schedule
(
    id             serial8 primary key,
    vehicle_id     int8                                   not null,
    route_id       int8                                   not null,
    departure_time timestamp(6) default CURRENT_TIMESTAMP not null,
    arrival_time   timestamp(6) default CURRENT_TIMESTAMP not null,
    status         varchar(20)  default 'scheduled'       not null, -- scheduled, departed, arrived, cancelled
    created_at     timestamp(6) default CURRENT_TIMESTAMP not null,
    created_by     int8                                   not null  -- username of the creator
);

create table if not exists booking
(
    id            serial8 primary key,
    user_id       int8                                   not null,
    schedule_id   int8                                   not null,
    seat_id       int8                                   not null,
    ticket_number varchar(50) unique                     not null,
    payment_id    int8,
    status        varchar(20)  default 'pending'         not null, -- pending, confirmed, cancelled, completed
    created_at    timestamp(6) default CURRENT_TIMESTAMP not null,
    created_by    int8                                   not null  -- id of the creator (if not self booking)
);

create table if not exists parcel
(
    id                serial8 primary key,
    sender_id         int8                                   not null,
    receiver_name     varchar(100)                           not null,
    receiver_phone    varchar(15)                            not null,
    pickup_location   text                                   not null,
    drop_off_location text                                   not null,
    weight            numeric(10, 2)                         not null, -- in kilograms
    dimensions        varchar(50),                                     -- e.g., '30x20x10 cm'
    type              varchar(20)                            not null, -- luggage, document, fragile, parcel
    cost              numeric(10, 2)                         not null,
    status            varchar(20)  default 'registered'      not null, --
    created_at        timestamp(6) default CURRENT_TIMESTAMP not null,
    created_by        int8                                   not null
);

create table if not exists consignment
(
    id                 serial8 primary key,
    consignment_number varchar(50) unique                     not null, -- Unique reference/tracking number
    schedule_id        int8                                   not null, -- Schedule for the consignment
    vehicle_id         int8                                   not null, -- Bus for the consignment
    origin             varchar(100)                           not null,
    destination        varchar(100)                           not null,
    total_weight       numeric(10, 2)                         not null, -- Total weight in kilograms
    total_cost         numeric(10, 2)                         not null, -- Total cost of the consignment
    status             varchar(20)  default 'created'         not null, -- created, in_transit, delivered, cancelled
    created_by         int8                                   not null, -- User/Agent who created it
    created_at         timestamp(6) default CURRENT_TIMESTAMP not null,
    updated_at         timestamp(6) default CURRENT_TIMESTAMP not null,
    last_updated_by    int8
);

create table if not exists consignment_parcel
(
    id             serial8 primary key,
    consignment_id int8                                   not null,
    parcel_id      int8                                   not null,
    remarks        text,                                           -- Optional remarks like fragile, high priority, etc.
    added_at       timestamp(6) default CURRENT_TIMESTAMP not null,
    added_by       int8                                   not null -- User/Agent who added the parcel to the consignment
);

create table if not exists delivery_proof
(
    id                serial8 primary key,
    parcel_id         int8                                   not null,
    delivery_staff_id int8                                   not null,
    signature         bytea, -- Digital or scanned signature
    photo             bytea, -- Optional photo of the delivery
    delivered_at      timestamp(6) default CURRENT_TIMESTAMP not null,
    delivered_by      int8                                   not null,
    remarks           text
);

create table if not exists payment
(
    id                    serial8 primary key,
    user_id               int8                                   not null,
    amount                numeric(10, 2)                         not null,
    currency              varchar(10)                            not null, -- e.g., USD, EUR, KES
    payment_method        varchar(20)                            not null, -- e.g., mobile money, card, cash
    transaction_reference varchar(50) unique                     not null,
    status                varchar(20)  default 'pending'         not null, -- pending, success, failed, refunded
    created_at            timestamp(6) default CURRENT_TIMESTAMP not null
);

create table if not exists refund
(
    id         serial8 primary key,
    payment_id int8                                   not null,
    amount     numeric(10, 2)                         not null,
    reason     text                                   not null,
    status     varchar(20)  default 'requested'       not null, -- requested, processed, rejected
    created_at timestamp(6) default CURRENT_TIMESTAMP not null,
    created_by int8                                   not null  -- id of the creator
);

create table if not exists notification
(
    id         serial8 primary key,
    user_id    int8                                   not null,
    message    text                                   not null,
    type       varchar(20)                            not null, -- ticket, parcel, alert
    status     varchar(20)  default 'sent'            not null, -- sent, delivered, read
    created_at timestamp(6) default CURRENT_TIMESTAMP not null
);

create table if not exists change_request
(
    id        serial8 primary key,
    user_id   int8,
    entity    varchar(50)                            not null, -- e.g., Booking, Parcel, Payment
    entity_id int8                                   not null, -- ID of the entity being audited
    action    varchar(10)                            not null, -- create, update, delete
    old_value varchar      default '{}'::jsonb       not null, -- JSON representation of the old value
    new_value varchar      default '{}'::jsonb       not null, -- JSON representation of the new value
    timestamp timestamp(6) default CURRENT_TIMESTAMP not null
);