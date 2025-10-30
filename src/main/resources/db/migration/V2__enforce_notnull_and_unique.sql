-- Bật kiểm tra FK
ALTER TABLE warehouse_stock CHECK CONSTRAINT fk_stock_vehicle;

-- Siết NOT NULL
ALTER TABLE warehouse_stock
ALTER COLUMN vehicle_id BIGINT NOT NULL;

-- Tạo UNIQUE (warehouse_id, vehicle_id)
-- (Nếu đã tồn tại constraint trùng tên, hãy DROP trước)
ALTER TABLE warehouse_stock
ADD CONSTRAINT uk_stock_warehouse_vehicle UNIQUE (warehouse_id, vehicle_id);

-- (Tuỳ chọn) Index riêng nếu cần thêm hiệu năng:
-- CREATE INDEX idx_stock_vehicle ON warehouse_stock(vehicle_id);
