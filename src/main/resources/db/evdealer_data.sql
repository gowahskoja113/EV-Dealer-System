USE [evdealer1];
GO

------------------------------------------------------------
-- ROLES
------------------------------------------------------------
INSERT INTO roles (role_name, description)
VALUES
    (N'ROLE_USER',      N'Default role for normal users'),
    (N'ROLE_ADMIN',     N'Administrator role'),
    (N'ROLE_EVMSTAFF',  N'EV Dealer Staff'),
    (N'ROLE_EVMMANAGER',N'EV Dealer Manager');
GO
-- role_id = 1..4

------------------------------------------------------------
-- USERS
------------------------------------------------------------
INSERT INTO users ([address], email, [name], [password], phone_number, role_id)
VALUES
    (N'Hà Nội',        N'user@evdealer.vn',    N'Nguyễn User',    N'123456', N'0900000001', 1),
    (N'Hồ Chí Minh',   N'admin@evdealer.vn',   N'Nguyễn Admin',   N'123456', N'0900000002', 2),
    (N'Đà Nẵng',       N'staff@evdealer.vn',   N'Lê Staff',       N'123456', N'0900000003', 3),
    (N'Cần Thơ',       N'manager@evdealer.vn', N'Trần Manager',   N'123456', N'0900000004', 4);
GO
-- user_id = 1..4

------------------------------------------------------------
-- DEALERSHIP
------------------------------------------------------------
INSERT INTO dealership ([address], [name], phone_number)
VALUES
    (N'123 Phố Huế, Hà Nội', N'EV Dealer Hà Nội', N'024-1111-2222');
GO
-- dealership_id = 1

------------------------------------------------------------
-- WAREHOUSE
------------------------------------------------------------
INSERT INTO warehouse (max_capacity, vehicle_quantity, warehouse_location, warehouse_name, dealership_id)
VALUES
    (50, 3, N'Long Biên, Hà Nội',    N'Kho Hà Nội 1', 1),
    (40, 1, N'Thủ Đức, Hồ Chí Minh', N'Kho HCM 1',    1);
GO
-- warehouse_id = 1,2

------------------------------------------------------------
-- MODEL
------------------------------------------------------------
INSERT INTO model (brand, model_color, model_code, production_year)
VALUES
    (N'VinFast VF3', N'Trắng', N'VINFAST01', 2024),
    (N'VinFast VF4', N'Đen',   N'VINFAST02', 2025);
GO
-- model_id = 1,2

------------------------------------------------------------
-- ELECTRIC_VEHICLE
------------------------------------------------------------
INSERT INTO electric_vehicle (
    battery_capacity, cost, image_url, price, [status], model_id
)
VALUES
    (50, 500000000,
     'https://firebasestorage.googleapis.com/v0/b/evdealer.firebasestorage.app/o/images%2Fvehicles%2Fvinfast-vf6.jpg?alt=media&token=021b324b-2ec6-41bb-b51c-39cb82927485',
     600000000,
     'AVAILABLE',
     1),
    (75, 800000000,
     'https://firebasestorage.googleapis.com/v0/b/evdealer.firebasestorage.app/o/images%2Fvehicles%2Fvinfast-white.jpg?alt=media&token=873cb4ad-928a-4fbd-ad80-24489cca51f3',
     950000000,
     'AVAILABLE',
     2);
GO
-- vehicle_id = 1,2

------------------------------------------------------------
-- CUSTOMER
------------------------------------------------------------
INSERT INTO customer ([address], [name], note, phone_number, [status], assigned_sales_id)
VALUES
    (N'Nam Từ Liêm, Hà Nội', N'Nguyễn Văn A', N'Khách VIP',                  N'0912345678', 'LEAD', 2),
    (N'Quận 1, HCM',         N'Trần Thị B',   N'Quan tâm dòng cao cấp',      N'0987654321', 'LEAD', 2);
GO
-- customer_id = 1,2

------------------------------------------------------------
-- SLOTS
------------------------------------------------------------
INSERT INTO slots (end_time, max_service, max_test_drive, service_count, start_time, test_drive_count)
VALUES
    ('2025-01-10T10:00:00', 2, 3, 0, '2025-01-10T09:00:00', 0),
    ('2025-01-10T11:00:00', 2, 3, 0, '2025-01-10T10:00:00', 0);
GO
-- slot_id = 1,2

------------------------------------------------------------
-- SERVICE_ENTITY
-- service_type CHECK: 'SERVICE' hoặc 'TEST_DRIVE'
------------------------------------------------------------
INSERT INTO service_entity ([description], [name], service_type)
VALUES
    (N'Dịch vụ lái thử xe điện tại showroom', N'Lái thử xe',        'TEST_DRIVE'),
    (N'Dịch vụ bảo dưỡng tiêu chuẩn',         N'Bảo dưỡng định kỳ', 'SERVICE');
GO
-- id = 1 (TEST_DRIVE), 2 (SERVICE)

------------------------------------------------------------
-- VEHICLE_SERIAL
-- status CHECK: 'SOLD_OUT','HOLD','AVAILABLE'
------------------------------------------------------------
INSERT INTO vehicle_serial (
    color_code, hold_until, seq_no, [status], vin,
    model_id, vehicle_id, warehouse_id
)
VALUES
    (N'W', NULL, 1, 'AVAILABLE', N'VIN2025111R0001', 1, 1, 1),
    (N'W', NULL, 2, 'AVAILABLE', N'VIN2025111R0002', 1, 1, 1),
    (N'K', NULL, 1, 'AVAILABLE', N'VIN2025112R0001', 2, 2, 2);
GO
-- vehicle_serial.id = 1,2,3

------------------------------------------------------------
-- WAREHOUSE_STOCK
------------------------------------------------------------
INSERT INTO warehouse_stock (quantity, model_id, warehouse_id)
VALUES
    (2, 1, 1),   -- 2 chiếc model 1 ở kho 1
    (1, 2, 1);   -- 1 chiếc model 2 ở kho 1
GO

------------------------------------------------------------
-- ORDERS
-- CHECK:
--   payment_status: 'OVERDUE','PAID','DEPOSIT_PAID','UNPAID'
--   status        : 'CANCELED','ORDER_PAID','DELIVERING','COMPLETED','PROCESSING'
------------------------------------------------------------

-- Order 1: Customer 1, mua serial 1, cọc 100m, còn 500m
INSERT INTO orders (
    currency, delivery_date, deposit_amount, deposit_paid_at, fully_paid_at,
    order_date, payment_status, planned_deposit_amount, remaining_amount,
    [status], updated_at, [version], customer_id, vehicle_serial_id
)
VALUES
    (N'VND',
     '2025-01-20',
     100000000,
     '2025-01-05T10:45:00',
     NULL,
     '2025-01-05T10:30:00',
     'DEPOSIT_PAID',
     100000000,
     500000000,
     'PROCESSING',
     '2025-01-05T10:45:00',
     1,
     1,
     1);
GO
-- order_id = 1

-- Order 2: Customer 2, mua serial 3, đã thanh toán đủ 950m
INSERT INTO orders (
    currency, delivery_date, deposit_amount, deposit_paid_at, fully_paid_at,
    order_date, payment_status, planned_deposit_amount, remaining_amount,
    [status], updated_at, [version], customer_id, vehicle_serial_id
)
VALUES
    (N'VND',
     '2025-01-25',
     950000000,
     '2025-11-16T07:15:24',
     '2025-11-16T07:20:36',
     '2025-01-06T11:00:00',
     'PAID',
     950000000,
     0,
     'ORDER_PAID',
     '2025-01-06T11:00:00',
     1,
     2,
     3);
GO
-- order_id = 2

------------------------------------------------------------
-- PAYMENT
-- CHECK:
--   method: 'CASH','VNPAY'
--   status: 'FAILED','PAID','PENDING'
--   type  : 'REMAINING','DEPOSIT'
------------------------------------------------------------

-- Payment cho Order 1: cọc 100m
INSERT INTO payment (
    amount, [message], [method], payment_date, [status],
    transaction_ref, [type], order_id
)
VALUES
    (100000000,
     N'Thanh toán tiền cọc bằng tiền mặt cho Order 1',
     'CASH',
     '2025-01-05T10:45:00',
     'PAID',
     N'CASH-ORD1-DEP',
     'DEPOSIT',
     1);

-- Payment cho Order 2: thanh toán full 950m
INSERT INTO payment (
    amount, [message], [method], payment_date, [status],
    transaction_ref, [type], order_id
)
VALUES
    (950000000,
     N'Thanh toán toàn bộ giá trị đơn hàng cho Order 2',
     'CASH',
     '2025-11-16T07:20:36',
     'PAID',
     N'CASH-ORD2-FULL',
     'DEPOSIT',
     2);
GO

------------------------------------------------------------
-- APPOINTMENTS
-- CHECK status: 'CANCELED','COMPLETED','IN_SERVICE','SCHEDULED'
------------------------------------------------------------

-- Lịch lái thử cho Customer 1 (TEST_DRIVE)
INSERT INTO appointments (
    end_at, note, start_at, [status],
    assigned_user_id, customer_id, service_id, slot_id
)
VALUES
    ('2025-01-10T10:00:00',
     N'Lịch lái thử đầu tiên cho khách Nguyễn Văn A',
     '2025-01-10T09:00:00',
     'SCHEDULED',
     2,   -- Nguyễn Admin
     1,   -- Nguyễn Văn A
     1,   -- Lái thử xe
     1);  -- Slot 1
GO
-- appointment_id = 1

-- Lịch bảo dưỡng cho Customer 2 (SERVICE) đã hoàn thành
INSERT INTO appointments (
    end_at, note, start_at, [status],
    assigned_user_id, customer_id, service_id, slot_id
)
VALUES
    ('2025-01-10T11:00:00',
     N'Bảo dưỡng định kỳ cho khách Trần Thị B',
     '2025-01-10T10:00:00',
     'COMPLETED',
     3,   -- Lê Staff
     2,   -- Trần Thị B
     2,   -- Bảo dưỡng định kỳ
     2);  -- Slot 2
GO
-- appointment_id = 2

------------------------------------------------------------
-- SERVICE_RECORD
------------------------------------------------------------
INSERT INTO service_record (
    [content], created_at, note, updated_at,
    appointment_id, customer_id, service_id, user_id
)
VALUES
    (N'Bảo dưỡng lần đầu: kiểm tra pin, phanh, lốp, cập nhật phần mềm.',
     '2025-01-10T11:00:00+07:00',
     N'Khách hài lòng, hẹn 10.000 km quay lại.',
     '2025-01-10T11:05:00+07:00',
     2,  -- gắn với appointment COMPLETED
     2,  -- customer 2
     2,  -- service "Bảo dưỡng định kỳ"
     3); -- Lê Staff
GO

------------------------------------------------------------
-- CẬP NHẬT LẠI SỐ LƯỢNG TRONG SLOTS
------------------------------------------------------------
UPDATE slots
SET test_drive_count = test_drive_count + 1
WHERE slot_id = 1;   -- 1 lịch TEST_DRIVE ở slot 1

UPDATE slots
SET service_count = service_count + 1
WHERE slot_id = 2;   -- 1 lịch SERVICE ở slot 2
GO
