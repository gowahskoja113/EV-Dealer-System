create database evdealer1
------------------------------------------------------------
-- ROLES
------------------------------------------------------------
CREATE TABLE roles (
    role_id     INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    role_name   NVARCHAR(255) NOT NULL,
    description NVARCHAR(255) NULL
);
GO

------------------------------------------------------------
-- USERS
------------------------------------------------------------
CREATE TABLE users (
    user_id      BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [password]   NVARCHAR(255) NOT NULL,
    [name]       NVARCHAR(255) NOT NULL,
    phone_number NVARCHAR(50) NULL,
    email        NVARCHAR(255) NULL,
    [address]    NVARCHAR(255) NULL,
    role_id      INT NULL,

    CONSTRAINT fk_user_role
        FOREIGN KEY (role_id) REFERENCES roles(role_id)
);
GO

------------------------------------------------------------
-- DEALERSHIP
------------------------------------------------------------
CREATE TABLE dealership (
    dealership_id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [name]        NVARCHAR(255) NOT NULL,
    [address]     NVARCHAR(255) NOT NULL,
    phone_number  NVARCHAR(20)  NULL
);
GO

------------------------------------------------------------
-- WAREHOUSE
------------------------------------------------------------
CREATE TABLE warehouse (
    warehouse_id      BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    warehouse_location NVARCHAR(255) NOT NULL,
    warehouse_name     NVARCHAR(255) NOT NULL,
    vehicle_quantity   INT NULL CONSTRAINT df_warehouse_vehicle_quantity DEFAULT(0),
    max_capacity       INT NULL CONSTRAINT df_warehouse_max_capacity DEFAULT(20),
    dealership_id      BIGINT NOT NULL,

    CONSTRAINT fk_warehouse_dealership
        FOREIGN KEY (dealership_id) REFERENCES dealership(dealership_id)
);
GO

------------------------------------------------------------
-- MODEL
------------------------------------------------------------
CREATE TABLE model (
    model_id     BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    model_code   NVARCHAR(50) NOT NULL,
    brand        NVARCHAR(100) NULL,
    model_color  NVARCHAR(100) NOT NULL,
    production_year INT NULL,

    CONSTRAINT ux_model_code UNIQUE (model_code)
);
GO

------------------------------------------------------------
-- ELECTRIC_VEHICLE
------------------------------------------------------------
CREATE TABLE electric_vehicle (
    vehicle_id       BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    cost             DECIMAL(18,2) NOT NULL,
    price            DECIMAL(18,2) NOT NULL,
    battery_capacity INT NOT NULL,
    model_id         BIGINT NOT NULL,
    image_url        NVARCHAR(500) NULL,
    [status]         VARCHAR(20) NOT NULL,

    CONSTRAINT fk_ev_model
        FOREIGN KEY (model_id) REFERENCES model(model_id),

    CONSTRAINT ux_ev_model UNIQUE (model_id)
);
GO

CREATE INDEX idx_ev_status ON electric_vehicle([status]);
GO

------------------------------------------------------------
-- CUSTOMER
------------------------------------------------------------
CREATE TABLE customer (
    customer_id   BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [name]        NVARCHAR(120) NOT NULL,
    phone_number  NVARCHAR(32) NOT NULL,
    [address]     NVARCHAR(255) NULL,
    note          NVARCHAR(255) NULL,
    [status]      VARCHAR(16) NOT NULL,
    assigned_sales_id BIGINT NULL,

    CONSTRAINT uq_customer_phone UNIQUE (phone_number),

    CONSTRAINT fk_customer_assigned_sales
        FOREIGN KEY (assigned_sales_id) REFERENCES users(user_id)
);
GO

CREATE INDEX idx_customer_name ON customer([name]);
GO

------------------------------------------------------------
-- SLOTS
------------------------------------------------------------
CREATE TABLE slots (
    slot_id          BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    start_time       DATETIME2(0) NOT NULL,
    end_time         DATETIME2(0) NOT NULL,
    max_test_drive   INT NOT NULL,
    max_service      INT NOT NULL,
    test_drive_count INT NOT NULL CONSTRAINT df_slots_test_drive_count DEFAULT(0),
    service_count    INT NOT NULL CONSTRAINT df_slots_service_count DEFAULT(0)
);
GO

------------------------------------------------------------
-- SERVICE_ENTITY
------------------------------------------------------------
CREATE TABLE service_entity (
    id           BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [name]       NVARCHAR(255) NOT NULL,
    [description] NVARCHAR(MAX) NULL,
    service_type VARCHAR(32) NOT NULL
);
GO

-- Index unique theo yêu cầu @Index(columnList = "name", unique = true)
CREATE UNIQUE INDEX ux_service_entity_name ON service_entity([name]);
GO

------------------------------------------------------------
-- VEHICLE_SERIAL
------------------------------------------------------------
CREATE TABLE vehicle_serial (
    id           BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    vehicle_id   BIGINT NOT NULL,
    model_id     BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    vin          NVARCHAR(64) NOT NULL,
    color_code   NVARCHAR(8) NOT NULL,
    seq_no       INT NOT NULL,
    [status]     VARCHAR(20) NOT NULL,
    hold_until   DATETIMEOFFSET(0) NULL,

    CONSTRAINT fk_vs_vehicle
        FOREIGN KEY (vehicle_id) REFERENCES electric_vehicle(vehicle_id),

    CONSTRAINT fk_vs_model
        FOREIGN KEY (model_id) REFERENCES model(model_id),

    CONSTRAINT fk_vs_warehouse
        FOREIGN KEY (warehouse_id) REFERENCES warehouse(warehouse_id),

    CONSTRAINT ux_vs_vin UNIQUE (vin)
);
GO

CREATE INDEX idx_vs_model    ON vehicle_serial(model_id);
CREATE INDEX idx_vs_wh       ON vehicle_serial(warehouse_id);
CREATE INDEX idx_vs_status   ON vehicle_serial([status]);
GO

------------------------------------------------------------
-- ORDERS
------------------------------------------------------------
CREATE TABLE orders (
    order_id            BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    customer_id         BIGINT NOT NULL,
    order_date          DATETIME2(0) NOT NULL,
    [status]            VARCHAR(20) NULL,
    deposit_amount      DECIMAL(18,2) NULL,
    remaining_amount    DECIMAL(18,2) NULL,
    planned_deposit_amount DECIMAL(18,2) NULL,
    payment_status      VARCHAR(20) NULL,
    delivery_date       DATE NULL,
    currency            NVARCHAR(8) NULL,
    deposit_paid_at     DATETIME2(0) NULL,
    fully_paid_at       DATETIME2(0) NULL,
    updated_at          DATETIME2(0) NULL,
    [version]           BIGINT NULL,
    vehicle_serial_id   BIGINT NOT NULL,

    CONSTRAINT fk_order_customer
        FOREIGN KEY (customer_id) REFERENCES customer(customer_id),

    CONSTRAINT fk_order_vehicle_serial
        FOREIGN KEY (vehicle_serial_id) REFERENCES vehicle_serial(id)
);
GO

CREATE INDEX idx_orders_customer        ON orders(customer_id);
CREATE INDEX idx_orders_serial          ON orders(vehicle_serial_id);
CREATE INDEX idx_orders_status          ON orders([status]);
CREATE INDEX idx_orders_payment_status  ON orders(payment_status);
CREATE INDEX idx_orders_order_date      ON orders(order_date);
GO

------------------------------------------------------------
-- PAYMENT
------------------------------------------------------------
CREATE TABLE payment (
    id             BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    order_id       BIGINT NOT NULL,
    amount         DECIMAL(18,2) NOT NULL,
    [status]       VARCHAR(20) NOT NULL,
    [type]         VARCHAR(20) NOT NULL,
    [method]       VARCHAR(20) NOT NULL,
    transactionRef NVARCHAR(50) NULL,
    paymentDate    DATETIME2(0) NULL,
    [message]      NVARCHAR(255) NULL,

    CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
GO

------------------------------------------------------------
-- APPOINTMENT
------------------------------------------------------------
CREATE TABLE appointments (
    appointment_id   BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    customer_id      BIGINT NOT NULL,
    assigned_user_id BIGINT NOT NULL,
    service_id       BIGINT NOT NULL,
    slot_id          BIGINT NOT NULL,
    start_at         DATETIME2(0) NOT NULL,
    end_at           DATETIME2(0) NOT NULL,
    [status]         VARCHAR(32) NOT NULL,
    note             NVARCHAR(MAX) NULL,

    CONSTRAINT fk_appointment_customer
        FOREIGN KEY (customer_id) REFERENCES customer(customer_id),

    CONSTRAINT fk_appointment_assigned_user
        FOREIGN KEY (assigned_user_id) REFERENCES users(user_id),

    CONSTRAINT fk_appointment_service
        FOREIGN KEY (service_id) REFERENCES service_entity(id),

    CONSTRAINT fk_appointment_slot
        FOREIGN KEY (slot_id) REFERENCES slots(slot_id),

    CONSTRAINT uq_customer_slot
        UNIQUE (customer_id, service_id, start_at, end_at)
);
GO

------------------------------------------------------------
-- SERVICE_RECORD
------------------------------------------------------------
CREATE TABLE service_record (
    id            BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    customer_id   BIGINT NOT NULL,
    service_id    BIGINT NOT NULL,
    createdAt     DATETIME2(0) NOT NULL,
    updatedAt     DATETIME2(0) NULL,
    [content]     NVARCHAR(MAX) NOT NULL,
    note          NVARCHAR(MAX) NULL,
    appointment_id BIGINT NOT NULL,

    CONSTRAINT fk_service_record_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),

    CONSTRAINT fk_service_record_customer
        FOREIGN KEY (customer_id) REFERENCES customer(customer_id),

    CONSTRAINT fk_service_record_service
        FOREIGN KEY (service_id) REFERENCES service_entity(id),

    CONSTRAINT fk_service_record_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),

    CONSTRAINT ux_service_record_appointment UNIQUE (appointment_id)
);
GO

CREATE INDEX idx_service_record_createdAt   ON service_record(createdAt);
CREATE INDEX idx_service_record_user        ON service_record(user_id);
CREATE INDEX idx_service_record_customer    ON service_record(customer_id);
CREATE INDEX idx_service_record_service     ON service_record(service_id);
GO

------------------------------------------------------------
-- WAREHOUSE_STOCK
------------------------------------------------------------
CREATE TABLE warehouse_stock (
    stock_id     BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    warehouse_id BIGINT NOT NULL,
    model_id     BIGINT NOT NULL,
    quantity     INT NOT NULL,

    CONSTRAINT fk_ws_warehouse
        FOREIGN KEY (warehouse_id) REFERENCES warehouse(warehouse_id),

    CONSTRAINT fk_ws_model
        FOREIGN KEY (model_id) REFERENCES model(model_id)
);
GO
