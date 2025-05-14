# Используем OpenJDK 17 для сборки
FROM eclipse-temurin:17-jdk-jammy as build

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем Gradle-файлы
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Копируем исходный код
COPY src src

# Даём права на запуск gradlew и собираем проект
RUN chmod +x gradlew
RUN ./gradlew bootJar

# Финальный образ с JRE (без JDK)
FROM eclipse-temurin:17-jre-jammy

# Рабочая директория
WORKDIR /app

# Копируем собранный JAR из предыдущего этапа
COPY --from=build /app/build/libs/*.jar app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]