# Jose-Theam-CRM

This project implements a CRM solution for a shop with Users, Admins and Customers.

# Tech Stack

This project is being developed with:  
*   Gradle  
*   SpringBoot + Kotlin  
*   PostgreSQL  
*   Flyway  
*   Docker compose

# How to run it locally
### Using the generated local docker image
1) Run the script ```./scripts/create-docker-image.sh``` which will create a local docker image of the app.
2) Run ```docker-compose up``` which will spin up a dockerized Posgres database and then the Spring boot application.
3) Run ``docker-compose down`` at any moment to stop both containers.

### Using your own environment
1) Load the Environment variables defined in the file ``.env`` into your environment.
2) Run ``docker-compose up postgres_db`` to spin up the PSQL database docker container.
    - Changing the values in the ``.env`` file will affect both to docker compose and the Spring 
    app, so feel free to modify and customize it to your machine.
3) Run ```./gradlew bootRun```.
4) Your application should be running and listening on the port 8080.

# How to run all tests
- This project is using TestContainers, which will spin up a PSQLContainer at the 
start of the test cycle using a random port.
1) Run ```./gradlew test```

# Documentation

You can find some examples to the different API endpoints under ``/docs/postman``.

# About Image uploading
Image uploading for rest APIs is a challenge itself since they are supposed to operate with JSON, therefore multipart files uploads are not so easy (as they require multipart/form-data header for requests).
After some investigation I found out that a common way to handle file uploads through Base64 encoded strings, which can be part of a valid JSON body request. This encoded string tends to be quite consume around a 25% extra bandwidth, but in this case I weighed design over performance.

Also, to avoid sending a huge request at the moment of creating a customer, the image can be uploaded in a request beforehand and have it ready to be attached on customer creation.

Therefore, in this application the flow to upload a picture to a Customer is the following:
1) Upload a picture MIME Base64 encoded String through a POST request to ``/pictures``.
    - Its up to the client to create the Base64 encoded string with MIME headers. It will be validated before saving it.
    - Answer from this request will include a ``pictureId``.
2) At this point, when creating or updating a Customer the picture can be attached to it including the ``pictureId`` in the request body.
    - This field is optional, meaning that customers can be created without a Picture. That can be easily changed, but I decided to keep customer creation making pictures not mandatory.
    - This has the drawback of having uploaded pictures not attached to any Customer entity. An easy solution for this would be a scheduled job cleaning those from time to time.

Assumptions are that users have access to all customer's pictures.
