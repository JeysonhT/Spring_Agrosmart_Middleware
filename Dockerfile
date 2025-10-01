# --- Etapa de Compilación (Build Stage) ---
# Usa una imagen oficial de Maven para construir el proyecto.
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copia solo el pom.xml para aprovechar el caché de capas de Docker.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia el resto del código fuente
COPY src ./src

# Compila y empaqueta la aplicación, omitiendo los tests
RUN mvn package -DskipTests

# --- Etapa de Ejecución (Run Stage) ---
# Usa Tomcat para desplegar el archivo WAR.
FROM tomcat:9.0-jre17-temurin

# Limpia las aplicaciones por defecto de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia el WAR y lo renombra a ROOT.war para desplegarlo en la raíz
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
