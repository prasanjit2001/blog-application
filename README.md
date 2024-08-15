# Blog Application

This is a Blog Application where users can register, log in, create posts, comment on posts, and choose categories for their posts.

## Technologies Used

- **Spring Boot**: Backend framework for building the application.
- **Spring Security**: For securing the application, including user authentication and authorization.
- **JWT (JSON Web Token)**: For handling secure user authentication.
- **Swagger**: For API documentation and testing.
- **Spring Data JPA**: For database interactions and persistence.
- **MySQL**: (Choose one) Database used to store user data, posts, and comments.
- It is deployed on AWS also

## Features

- **User Registration & Login**: Users can sign up and log in using JWT-based authentication.
- **Create Posts**: Authenticated users can create and publish posts.
- **Commenting**: Users can comment on posts by choosing a category.
- **Category Selection**: Posts and comments can be categorized for better organization and filtering.
- **Role-based Access Control**: Different levels of access and permissions based on user roles.

## Getting Started

To get started with this project, follow the instructions below:

1. Clone the repository.
2. Set up the database connection in the `application.properties` file.
3. Run the application using your IDE or from the command line with `mvn spring-boot:run`.
4. Access the Swagger UI at `http://localhost:8080/swagger-ui.html` to explore the API.

## API Documentation

You can explore the API endpoints using Swagger. Once the application is running, navigate to:

