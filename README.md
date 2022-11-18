# crypto-investment

    Application can be run with deploy.sh script, which starts the spring boot server as a
        
    docker container on 8081:8080 as well as the postgres on 5433:5432 with docker-compose.

    It can be explicitly run with: docker-compose up -d

    To run tests: - ./mvnw test.

    Flyway is used for migrations.

    For testing I use testcontainers so the tests do not use the application database.

    There are unit tests for the service to check that every publuc method works as expected.

    For API Documentation swagger ui is used which is served on - localhost:8081/docs

    There are descriptions for every endpoint and also what the endpoints accept as patameters.

    Other than swagger desctiptions, there are comments for every method describing what it does.

# How it works

    On application startup every cvs file is checked, if it is already imported in databse or not

    I decided to use databse here for scaling reasons, when the number of files will increase
    
    quering database will give us more flexibility and better performance.

# Rate limit

    All endpoints are secured with rate limit of 20 requests per minure (arbitrarily chosen)
    
    can be changed from properties

# Open for extension

    If any new crypto appears in any files, the entry for it will be created in the database

    so from now on we will be able to calculate stats for this new crypto too

# Ways of getting the data from service

    The service is designed in a way that it can support any new type of time frames                                                    

    because the database is queried on start date and end date ranges it will be easy to

    just provide these parameters and we can see data for any period of time


    