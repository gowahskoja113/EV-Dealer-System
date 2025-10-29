-- Thêm cột vehicle_id cho phép NULL
ALTER TABLE warehouse_stock
ADD vehicle_id BIGINT NULL;

-- Tạo FK ở trạng thái NOCHECK (để không chặn dữ liệu NULL giai đoạn đầu)
ALTER TABLE warehouse_stock WITH NOCHECK
ADD CONSTRAINT fk_stock_vehicle
FOREIGN KEY (vehicle_id) REFERENCES electric_vehicle(vehicle_id);
