# Example of a rest api using JWT Spring Security

##
This is an example for using [JWT](https://jwt.io/) (JSON Web Token)  and [Mysql](https://www.mysql.com/) with Spring Security and Spring Boot.

## Requirements
```
* Java 11
* Maven 3
* Mysql 8.0.26
```

## Usage
```
1- Install Mysql 
2- CREATE DATABASE tasks_app;
3- Change mysql user and password from application.properties
4- start the application with the Spring Boot maven plugin (mvn spring-boot:run).
```

### Users Use Case Diagram
![USE_CASE](https://i.ibb.co/1m4ntzZ/usecase.png)

### Users API
Use postman to test the api
```
SignIn: POST /api/1.0/users | { username, email, password }
LogIn: POST /api/1.0/auth   | { email, password }
FindAllUsers: GET /api/1.0/users
FindUserById: GET /api/1.0/users/{id}
DeleteUser: DELETE /api/1.0/users/{id}
```