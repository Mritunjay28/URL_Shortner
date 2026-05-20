# URL Shortener

A full-stack URL Shortener application built using **Spring Boot + MySQL + JavaScript frontend** that allows users to shorten long URLs, create custom aliases, set expiry dates, track click analytics, and redirect seamlessly.

---

## Features

### Backend Features
- Shorten long URLs
- Redirect short URLs to original URLs
- Custom short code support
- Expiry-based URLs
- Click count tracking
- URL statistics endpoint
- Input validation
- Global exception handling
- Swagger/OpenAPI documentation
- RESTful API design
- MySQL persistent storage
- Unit testing (Service + Controller layer)

### Frontend Features
- Simple responsive UI using HTML, Tailwind CSS, and JavaScript
- Create short URLs directly from browser
- Optional custom short code support
- Optional expiry date selection
- Fetch URL statistics
- Copy shortened URL to clipboard
- Backend integration using Fetch API

---

# Tech Stack

## Backend
- Java 17+
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- MySQL
- Lombok
- Maven
- Swagger / OpenAPI
- JUnit 5
- Mockito

## Frontend
- HTML
- Tailwind CSS
- JavaScript
- Fetch API

---

# Project Structure

```bash
URL-Shortner/
│
├── Frontend/
│   ├── index.html
│   └── script.js
│
├── src/
│   ├── main/java/com/Project/URL/Shortner
│   │
│   │   ├── config
│   │   │   └── CorsConfig.java
│   │   │
│   │   ├── controller
│   │   │   └── UrlShortenerController.java
│   │   │
│   │   ├── DTO
│   │   │   ├── request
│   │   │   │   └── CreateShortUrlRequest.java
│   │   │   │
│   │   │   └── response
│   │   │       ├── ShortUrlResponse.java
│   │   │       ├── UrlStatsResponse.java
│   │   │       └── ErrorResponse.java
│   │   │
│   │   ├── entity
│   │   │   └── UrlMapping.java
│   │   │
│   │   ├── exceptions
│   │   │   ├── InvalidUrlException.java
│   │   │   ├── UrlNotFoundException.java
│   │   │   ├── UrlExpiredException.java
│   │   │   ├── ShortCodeAlreadyExistsException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   │
│   │   ├── repository
│   │   │   └── UrlMappingRepo.java
│   │   │
│   │   ├── service
│   │   │   ├── UrlMappingService.java
│   │   │   └── UrlMappingServiceImpl.java
│   │   │
│   │   └── util
│   │       └── ShortCodeGenerator.java
│   │
│   └── test/
│       ├── service
│       └── controller
│
├── pom.xml
└── README.md
```

---

# API Endpoints

## 1. Create Short URL

### POST
```http
/api/urls
```

### Request Body
```json
{
  "originalUrl": "https://google.com",
  "customCode": "google",
  "expiryAt": "2026-06-01T12:00:00"
}
```

### Response
```json
{
  "originalUrl": "https://google.com",
  "shortUrl": "http://localhost:8080/google",
  "shortCode": "google"
}
```

---

## 2. Redirect to Original URL

### GET
```http
/{code}
```

Example:

```http
http://localhost:8080/google
```

Behavior:
- Finds original URL
- Checks expiry
- Increments click count
- Redirects to original destination

---

## 3. Get URL Statistics

### GET
```http
/{code}/stats
```

### Response
```json
{
  "originalUrl": "https://google.com",
  "shortCode": "google",
  "createdAt": "2026-05-20T14:32:11",
  "clickCount": 12
}
```

---

# Error Responses

## Invalid URL
```json
{
  "message": "Invalid URL is Given",
  "status": 400
}
```

## Custom Code Already Exists
```json
{
  "message": "google is already taken",
  "status": 409
}
```

## Expired URL
```json
{
  "message": "The given ShortCode has Expired",
  "status": 410
}
```

## Short Code Not Found
```json
{
  "message": "Short code not found",
  "status": 404
}
```

---

# Database Schema

## url_mapping

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary Key |
| original_url | TEXT | Original long URL |
| short_code | VARCHAR | Unique short code |
| click_count | BIGINT | Number of clicks |
| created_at | DATETIME | URL creation timestamp |
| expiry_at | DATETIME | Expiry timestamp |

---

# Swagger Documentation

After running backend:

```bash
http://localhost:8080/swagger-ui/index.html
```

Interactive API docs because civilized debugging matters.

---

# Setup Instructions

## 1. Clone Repository

```bash
git clone https://github.com/Mritunjay28/URL_Shortner.git
cd URL_Shortner
```

---

## 2. Configure Database

Update:

```properties
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/urlshortener
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

app.base-url=http://localhost:8080
```

---

## 3. Run Backend

```bash
mvn spring-boot:run
```

or run:

```bash
UrlShortnerApplication
```

from IntelliJ.

---

## 4. Run Frontend

Open:

```bash
Frontend/index.html
```

or run with Live Server.

Frontend expects backend at:

```bash
http://localhost:8080
```

---

# Testing

## Unit Tests
Implemented using:

- JUnit 5
- Mockito

Test coverage includes:

- URL creation
- Invalid URL validation
- Duplicate custom short code
- Expired URL access
- URL not found
- Click count increment
- URL statistics retrieval
- Controller endpoint behavior

Run tests:

```bash
mvn test
```

---

# Future Improvements

- Docker containerization
- Redis caching
- User authentication (JWT)
- Rate limiting
- QR code generation for short URLs
- Link deletion support
- Admin dashboard
- Cloud deployment (Render / Railway / Netlify)

---

# Learning Outcomes

This project helped practice:

- Layered architecture
- REST API design
- DTO pattern
- Exception handling
- Validation
- JPA/Hibernate persistence
- Redirect responses
- Unit testing
- Controller testing
- Frontend-backend integration
- CORS handling
- Full-stack application design

---

# Author : **Mritunjay Senapati**

Built as a full-stack backend-focused learning project using Spring Boot.
