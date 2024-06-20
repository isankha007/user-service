FROM gradle:8.2-jdk17-alpine as builder
WORKDIR /app
COPY . .
RUN gradle clean build

FROM alpine:3.18.3
WORKDIR /app
RUN apk add --no-cache openjdk17-jre-headless
COPY --from=builder /app/build/libs/userService-0.0.1-SNAPSHOT.jar .
EXPOSE 8082
CMD ["java", "-jar", "userService-0.0.1-SNAPSHOT.jar"]