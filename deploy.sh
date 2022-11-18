./mvnw clean package -DskipTests
./mvnw spring-boot::run -Dspring-boot.run.jvmArguments="\
  -Dspring.datasource.url=jdbc:postgresql://localhost:5433/cryptodb\
  -Dspring.datasource.username=postgres\
  -Dspring.datasource.password=postgres
  -Dserver.port=8081"
cd src/main/docker || exit 1
docker-compose up -d