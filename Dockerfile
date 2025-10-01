# --- Etapa de Compilación (Build Stage) ---
# Esta etapa es idéntica a la anterior, compila el código y crea el .war
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

# --- Etapa de Ejecución (Run Stage) ---
# Usa una imagen de Tomcat para desplegar el archivo WAR
FROM tomcat:9.0-jre17-temurin

# Elimina las aplicaciones por defecto de Tomcat para limpiar la imagen
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia el archivo WAR desde la etapa de compilación a la carpeta de despliegue de Tomcat.
# Al renombrarlo a ROOT.war, Tomcat lo desplegará en la raíz del servidor (ej. http://<host>/)
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Expone el puerto 8080, que es el que usa Tomcat por defecto
EXPOSE 8080

# El comando por defecto de la imagen de Tomcat se encargará de iniciar el servidor.