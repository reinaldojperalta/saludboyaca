# Etapa 1: Compilación con Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar el pom.xml y descargar dependencias (aprovechando la caché de capas de Docker)
COPY pom.xml .
# Opcional: RUN mvn dependency:go-offline para cachear dependencias

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final con Tomcat 10
FROM tomcat:10-jdk17

# Eliminar las aplicaciones por defecto de Tomcat para mayor seguridad y limpieza
RUN rm -rf /usr/local/tomcat/webapps/*

# Copiar el .war generado de la etapa 1
# Se nombra saludboyaca.war para que el context path sea /saludboyaca
COPY --from=build /app/target/saludboyaca.war /usr/local/tomcat/webapps/saludboyaca.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
