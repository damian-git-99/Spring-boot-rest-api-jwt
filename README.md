# Example of a rest api using JWT Spring Security

##
This is an example for using [JWT](https://jwt.io/) (JSON Web Token) with Spring Security and Spring Boot 2.7.5. By default, it uses an in-memory H2 database.

## Run project With Docker
- Install Docker
- Run `docker compose up`

## Run Project Manually

### Requirements
```
* Java 11
* Maven 3
* H2
```

### Using MySQL (optional)
```
1- Install MySQL 8.0.26
2- CREATE DATABASE tasks_app;
3- Change MySQL configuration in application.properties:
   spring.datasource.url=jdbc:mysql://localhost:3306/tasks_app
   spring.datasource.username=your_user
   spring.datasource.password=your_password
4- Add MySQL dependency in pom.xml (if not present):
   <dependency>
       <groupId>mysql</groupId>
       <artifactId>mysql-connector-java</artifactId>
   </dependency>
5- Start the application with the Spring Boot maven plugin (mvn spring-boot:run)
```

### Users Use Case Diagram
![USE_CASE](https://i.ibb.co/YNLSpW4/usecase.png)

### Users API
The application will be available at http://localhost:3000

Use postman to test the api
```
SignUp: POST /api/1.0/users | { username, email, password }
LogIn: POST /api/1.0/auth   | { email, password }
FindAllUsers: GET /api/1.0/users
FindUserById: GET /api/1.0/users/{id}
DeleteUser: DELETE /api/1.0/users/{id}
UpdateUSer: PUT /api/1.0/users/{id} | { username, email, password }
```
