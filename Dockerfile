FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el JAR compilado
COPY build/libs/sistema-inventario-1.0-SNAPSHOT.jar app.jar

# Puerto del backend
EXPOSE 8080

# Variables de entorno por defecto (se sobreescriben en docker-compose)
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]