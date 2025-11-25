-- 1. Xóa ràng buộc cũ
ALTER TABLE vehicle_serial
DROP CONSTRAINT CK__vehicle_s__statu__6A30C649;

-- 2. Thêm ràng buộc mới bao gồm cả trạng thái mới
-- (Nhớ liệt kê ĐẦY ĐỦ các trạng thái trong Enum Java của bạn)
ALTER TABLE vehicle_serial
ADD CONSTRAINT CK_vehicle_serial_status
CHECK (status IN ('AVAILABLE', 'HOLD', 'SOLD_OUT', 'DELIVERING', 'DELIVERED', 'UNDELIVERED'));