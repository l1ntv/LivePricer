# Берём официальный образ Tomcat с поддержкой JDK 17
FROM tomcat:9.0.82-jdk17

# Очищаем стандартные приложения (если нужно)
RUN rm -rf /usr/local/tomcat/webapps/*

# Копируем наш WAR-файл (имя берётся из mvn package)
COPY target/LivePricer-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/app.war

# Запускаем Tomcat в foreground режиме
CMD ["catalina.sh", "run"]