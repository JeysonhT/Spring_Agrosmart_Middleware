# --- Etapa de Compilación (Build Stage) ---
# Usa una imagen de Maven para construir el JAR ejecutable.
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copia el pom.xml para cachear las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia el resto del código fuente
COPY src ./src

# Compila y empaqueta la aplicación
RUN mvn package -DskipTests

# --- Etapa de Ejecución (Run Stage) ---
# Usa una imagen de JRE mínima para ejecutar la aplicación
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia el JAR ejecutable desde la etapa de compilación
COPY --from=builder /app/target/*.jar app.jar

# Expone el puerto 8080 (el default de Spring Boot)
EXPOSE 8080

# Variable de entorno para la API Key (debes configurarla en Render)
ENV API_KEY=${API_KEY}

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
