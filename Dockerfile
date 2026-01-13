# Используем JDK 11
#FROM eclipse-temurin:11-jdk
FROM maven:3.9.2-eclipse-temurin-17 AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем pom.xml и загружаем зависимости (кэшируем слои)
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Делаем mvnw исполняемым
RUN chmod +x mvnw

RUN ./mvnw dependency:resolve

# Копируем исходники
COPY src ./src

# Собираем jar
#RUN ./mvnw clean package -DskipTests
RUN mvn clean install -DskipTests

# Указываем точку входа
ENTRYPOINT ["java", "-jar", "target/cadence-demo-1.0.0.jar"]
