FROM maven:3.8.3-openjdk-17

ARG JAR_FILE=target/oceanclean.jar
ARG PROJECT_HOME=/usr/src/oceanclean

ENV PROJECT_HOME /usr/src/oceanclean
ENV JAR_NAME Ocean-Clean.jar

RUN groupadd -r appgroup && useradd -r -g appgroup appuser

RUN mkdir -p $PROJECT_HOME && chown -R appuser:appgroup $PROJECT_HOME

RUN mkdir -p /home/appuser/.m2 && chown -R appuser:appgroup /home/appuser/.m2

WORKDIR $PROJECT_HOME

COPY . .

USER appuser

RUN mvn clean package -DskipTests

RUN ls -l $PROJECT_HOME/target

USER root
RUN mv $PROJECT_HOME/target/*.jar $PROJECT_HOME/$JAR_NAME && chown appuser:appgroup $PROJECT_HOME/$JAR_NAME

USER appuser

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILE}", "Ocean-Clean.jar"]
