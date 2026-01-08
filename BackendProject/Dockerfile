# Stage 1: Build the Java application
FROM eclipse-temurin:24-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw
# Télécharger les dépendances une fois pour les mettre en cache
RUN ./mvnw dependency:go-offline -B
COPY src ./src
# Construire l'application
#RUN ./mvnw package
RUN ./mvnw package -DskipTests

# Stage 2: Create the final Docker image
FROM eclipse-temurin:24-jre
WORKDIR /app
# Copier le JAR construit depuis le stage de build
COPY --from=build /app/target/*.jar app.jar
# Exposer le port de votre application Spring Boot (par défaut 8080)
EXPOSE 3006
# Point d'entrée de l'application
ENTRYPOINT ["java", "-jar", "app.jar"]