# Un solo Dockerfile per tutti i servizi del backend: quale costruire lo dice l'argomento SERVICE,

# 1) Compilazione con Maven, dentro un'immagine con la JDK
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY common common
COPY user-service user-service
COPY issue-service issue-service
COPY notification-service notification-service
COPY gateway gateway
ARG SERVICE

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -q -pl ${SERVICE} -am package -DskipTests \
    && cp ${SERVICE}/target/${SERVICE}-*.jar /app.jar

# 2) Immagine finale: solo la JRE e il jar.
FROM eclipse-temurin:21-jre-alpine
# Il servizio non gira come root.
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
# La cartella delle immagini caricate (la usa solo issue-service, su un volume):
# deve esistere già e appartenere all'utente app, altrimenti il volume nasce di root.
RUN mkdir uploads && chown -R app:app /app
COPY --from=build /app.jar app.jar
USER app
ENTRYPOINT ["java", "-jar", "app.jar"]
