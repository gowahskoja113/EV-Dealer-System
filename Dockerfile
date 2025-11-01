# Sử dụng image Gradle chính thức cho giai đoạn build
FROM gradle:8.11.1-jdk21-alpine AS build

# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy các file cấu hình Gradle và tải các dependency
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle build --no-daemon -x test || return 0

# Copy toàn bộ mã nguồn của ứng dụng vào thư mục làm việc
COPY src ./src

# Build ứng dụng Spring Boot bằng Gradle
RUN gradle build --no-daemon -x test

# Giai đoạn thứ hai: tạo ra image nhỏ gọn hơn chỉ chứa JRE và ứng dụng đã build
# FROM amazoncorretto:17.0.15-al2023
FROM amazoncorretto:21.0.9-alpine3.22

# Đặt thư mục làm việc trong container
WORKDIR /app

# Sao chép file JAR đã build từ giai đoạn 'build' vào giai đoạn hiện tại
COPY --from=build /app/build/libs/*.jar app.jar

# Mở cổng mà ứng dụng Spring Boot lắng nghe (mặc định là 8080)
EXPOSE 8080

# Lệnh mặc định để chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "app.jar"]