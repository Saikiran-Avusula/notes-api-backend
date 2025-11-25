# Step 1: Build the Spring Boot app
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

COPY . .

# FIX 1: normalize line endings (important if on Windows)
RUN sed -i 's/\r$//' mvnw

# FIX 2: make mvnw executable
RUN chmod +x mvnw

# Now run the wrapper safely
RUN ./mvnw clean package -Dmaven.test.skip=true


# Step 2: Run the built JAR
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
