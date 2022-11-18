./mvnw clean package -DskipTests
docker-compose stop crypto-investment
echo y | docker-compose rm crypto-investment
docker rmi crypto-investment
docker-compose up -d --build crypto-investment