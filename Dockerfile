# --- ETAPA 1: CONSTRUCCIÓN (BUILD) ---
# Usamos una imagen de Maven con Java 17 para compilar el proyecto
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Compilamos el proyecto saltando los tests para ir más rápido
RUN mvn clean package -DskipTests

# --- ETAPA 2: EJECUCIÓN (RUN) ---
# Usamos una imagen ligera de Java 17 solo para correr el programa
FROM openjdk:17-jdk-slim
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto (informativo)
EXPOSE 8080

# Comando para iniciar la app
ENTRYPOINT ["java", "-jar", "app.jar"]