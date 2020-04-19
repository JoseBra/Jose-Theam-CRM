# Jose-Theam-CRM

This project implements a CRM solution for a shop with Users, Admins and Customers.

## Tech Stack

This project is being developed with:  
*   Gradle  
*   SpringBoot + Kotlin  
*   PostgreSQL  
*   Flyway  
*   Docker compose

## How to run it locally

1) Load the Environment variables defined in the file ``.env`` into your environment.
2) Run ``docker-compose up`` to spin up the PSQL database docker container.
    - Changing the values in the ``.env`` file will affect both to docker compose and the Spring 
    app, so feel free to modify and customize it to your machine.
3) Run ```./gradlew bootRun```.
4) Your application should be running and listening on the port 8080.

## How to run all tests
- This project is using TestContainers, which will spin up a PSQLContainer at the 
start of the test cycle using a random port.
1) Run ```./gradlew test```