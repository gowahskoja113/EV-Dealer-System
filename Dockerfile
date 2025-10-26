# Sử dụng image Maven chính thức cho giai đoạn build
# FROM  maven:3.9-eclipse-temurin-17-noble as build
FROM  maven:3.9.11-eclipse-temurin-21-noble as build


# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy pom.xml và tải các dependency của Maven.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ mã nguồn của ứng dụng vào thư mục làm việc
COPY src ./src

# Build ứng dụng Spring Boot bằng Maven
RUN mvn clean package -Dmaven.test.skip=true

# Giai đoạn thứ hai: tạo ra image nhỏ gọn hơn chỉ chứa JRE và ứng dụng đã build
# FROM amazoncorretto:17.0.15-al2023
FROM amazoncorretto:21.0.9-alpine3.22

# Đặt thư mục làm việc trong container
WORKDIR /app

# Sao chép file JAR đã build từ giai đoạn 'build' vào giai đoạn hiện tại
COPY --from=build /app/target/*.jar app.jar

# Mở cổng mà ứng dụng Spring Boot lắng nghe (mặc định là 8080)
EXPOSE 8080

# Lệnh mặc định để chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "app.jar"]