
# Convert postgres:// → jdbc:postgresql://
export JDBC_DATABASE_URL=$(echo $DATABASE_URL | sed 's/^postgres:/jdbc:postgresql:/')

# Run Spring Boot app
java -jar target/notes-api-0.0.1-SNAPSHOT.jar
