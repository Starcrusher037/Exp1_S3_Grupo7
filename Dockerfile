# Usamos la imagen ligera de ejecución para Java 21
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copiamos el archivo .jar ejecutable que Maven ya generó con éxito en el paso anterior
COPY target/*.jar app.jar

# Exponemos el puerto de red estándar de Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación al encender el contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]