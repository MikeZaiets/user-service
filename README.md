##RESTful User service 
1. It has User model with following fields:
   - **Email.**  Has validation for against email pattern.
   - **First name.** Has validations
   - **Last name.** Has validations
   - **Birth date.** Has validation: value must be earlier than current date
   - **Address.** Separate object. Has validations
   - **Phone number.** Has validations.
2. It has REST controllers with the following functionality:
   - Create user. It allows to register users who are more than [assigned value] years old.
   The value must be assigned in settings from properties file (For example - [18])
   - Find user by id
   - Update user address
   - Update all user fields
   - Delete user
   - Search for users by birth date range. Return a list of users.
   Has validation which checks that “From” is less than “To”.
3. Code is covered by unit tests using Spring
4. Code has error handling for REST
5. API responses are in JSON format
6. Used PostgesSQL for working environment
7. Used PostgesSQL testcontainer for tresting.
8. Latest version of Spring Boot. Java version 11
9. The project uses Swagger to describe the API of the Service.
10. Before running the application, need to execute a scripts:
    - **src/main/resources/init-db.sql  -** to configure the database 
    - **src/main/resources/fill-db.sql  -** to populate database;

    Or you can to create and populate database during application startup.
    - you need to uncomment the 30 line of code in **RunWithAppService.class**
    

11. Use http://localhost:8080/swagger-ui/#/user-controller page to view User-service API

(Adding Docker-compose + FlyWay in progress)
