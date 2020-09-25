FROM openjdk:11
ADD ./build/libs/*.jar app.jar
ENTRYPOINT  java -Ddatabase.url=$url -Ddatabase.username=$username -Ddatabase.password=$password -jar app.jar
