# URL Shortener API

A backend URL Shortener application built with Spring Boot that allows users to shorten long URLs and redirect using generated short codes.

## Features

- Shorten long URLs
- Redirect short URLs to original URLs
- Track click count for each shortened URL
- Input validation for invalid/empty URLs
- Global exception handling with clean API responses
- Persistent storage using MySQL
- RESTful API design

---

## Tech Stack

- Java 17+
- Spring Boot
- Spring Web
- Spring Data JPA
- MySQL
- Lombok
- Maven
- Hibernate

---

## Project Structure

```bash
src/main/java/com/Project/URL/Shortner
│
├── controller
│   └── UrlShortenerController.java
│
├── DTO
│   ├── request
│   │   └── CreateShortUrlRequest.java
│   └── response
│       ├── ShortUrlResponse.java
│       └── ErrorResponse.java
│
├── entity
│   └── UrlMapping.java
│
├── exceptions
│   ├── InvalidUrlException.java
│   ├── UrlNotFoundException.java
│   └── GlobalExceptionHandler.java
│
├── repository
│   └── UrlMappingRepo.java
│
├── service
│   ├── UrlMappingService.java
│   └── UrlMappingServiceImpl.java
│
└── util
    └── ShortCodeGenerator.java
```

---

## API Endpoints

### 1. Create Short URL

**POST**
```http
/api/urls
```

### Request Body
```json
{
  "originalUrl": "https://google.com"
}
```

### Response
```json
{
  "originalUrl": "https://google.com",
  "shortUrl": "http://localhost:8080/Ab12Xq",
  "shortCode": "Ab12Xq"
}
```

---

### 2. Redirect to Original URL

**GET**
```http
/{code}
```

Example:
```http
http://localhost:8080/Ab12Xq
```

Behavior:
- Finds original URL
- Increments click count
- Redirects to original destination

---

## Error Responses

### Invalid URL
```json
{
  "message": "Invalid URL is Given",
  "status": 400
}
```

### Empty URL
```json
{
  "message": "URL cannot be empty",
  "status": 400
}
```

### Short Code Not Found
```json
{
  "message": "Short code not found",
  "status": 404
}
```

### Internal Server Error
```json
{
  "message": "Something went wrong",
  "status": 500
}
```

---

## Database Schema

### url_mapping

| Column       | Type      | Description |
|-------------|-----------|-------------|
| id          | BIGINT    | Primary Key |
| original_url| TEXT      | Original long URL |
| short_code  | VARCHAR   | Generated unique short code |
| click_count | BIGINT    | Number of redirects |
| created_at  | DATETIME  | URL creation timestamp |

---

## Setup Instructions

### 1. Clone Repository

```bash
git clone https://github.com/Mritunjay28/url-shortener.git
cd url-shortener
```

---

### 2. Configure Database

Update `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/urlshortener
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

### 3. Run Application

Using Maven:

```bash
mvn spring-boot:run
```

Or from IntelliJ:
Run `UrlShortnerApplication`

---

## Testing

Use Postman to test:

### Create Short URL
```http
POST http://localhost:8080/api/urls
```

Body:
```json
{
  "originalUrl": "https://google.com"
}
```

---

### Test Redirect
Open returned short URL in browser:

```http
http://localhost:8080/Ab12Xq
```

---

## Future Improvements

Planned enhancements:

- Swagger/OpenAPI documentation
- URL analytics endpoint
- Custom short code support
- Expiry-based URLs
- Docker support
- Unit testing
- Integration testing
- Redis caching
- User authentication with JWT

---

## Learning Outcomes

This project helped practice:

- Layered backend architecture
- REST API development
- DTO pattern
- Exception handling
- Validation
- JPA/Hibernate persistence
- Redirect responses
- Service-oriented design

---

## Author - Mritunjay Senapati

Built as a backend learning project using Spring Boot. 
