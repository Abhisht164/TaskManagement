# Task Management API

A simple Spring Boot REST API built to explore how **Servlet Filters** and **Spring MVC Interceptors** work together in the request lifecycle.

Instead of focusing on complex business logic, this project demonstrates request logging, API key validation, method-level authorization, request correlation, and execution time tracking using a Task Management application.

---

## Tech Stack

* Java 21
* Spring Boot
* Spring MVC
* Spring Data JPA
* H2 Database
* Maven
* Lombok

---

# Request Flow

Every request follows the flow below:

```
Client
   │
   ▼
RequestLoggingFilter
   │
   ▼
ApiKeyFilter
   │
   ▼
DispatcherServlet
   │
   ▼
CustomHandlerInterceptor (preHandle)
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
CustomHandlerInterceptor (afterCompletion)
   │
   ▼
Response
```

---

# What's Implemented

## RequestLoggingFilter

The first filter executed for every request.

Responsibilities:

* Generates a unique Correlation ID (`UUID`)
* Logs the HTTP method and request URI
* Stores the Correlation ID as a request attribute so it can be accessed later by the interceptor

Example log:

```
GET /api/tasks 92c5a22e-d2d1-4f4f-8a91-6af7bfbcb7c4
```

---

## ApiKeyFilter

Executed after the logging filter.

This filter validates the `X-API-KEY` request header.

```
X-API-KEY: secret123
```

If the API key is valid, the request proceeds normally.

Otherwise, the filter immediately returns:

```
401 Unauthorized
```

Response body:

```
Missing or invalid API key
```

The health endpoint (`/api/public/health`) is excluded from this filter using `shouldNotFilter()`.

---

## CustomHandlerInterceptor

The interceptor performs two responsibilities.

### Authorization

Endpoints annotated with

```java
@RequiresRole("ADMIN")
```

require the client to send

```
X-USER-ROLE: ADMIN
```

If the required role is missing, the interceptor blocks the request and returns

```
403 Forbidden
```

before the controller is executed.

---

### Request Execution Time

The interceptor records the request start time in `preHandle()`.

Once the request finishes, `afterCompletion()` logs:

* Request execution time
* Correlation ID
* HTTP status
* Request URI
* HTTP method

Example:

```
Elapsed time: 18 ms
92c5a22e-d2d1-4f4f-8a91-6af7bfbcb7c4
200
/api/tasks
GET
```

---

# REST APIs

Base URL

```
/api/tasks
```

## Get All Tasks

```
GET /api/tasks
```

Returns all available tasks.

---

## Get Task By ID

```
GET /api/tasks/{id}
```

Returns a task by its ID.

If the task doesn't exist, the request is handled by the global exception handler.

---

## Create Task

```
POST /api/tasks
```

Creates a new task.

Example request:

```json
{
  "title": "Learn Filters",
  "description": "Understand Spring Filters",
  "status": "PENDING"
}
```

Returns **201 Created**.

---

## Update Task

```
PUT /api/tasks/{id}
```

Updates an existing task.

Returns the updated task.

---

## Delete Task

```
DELETE /api/tasks/{id}
```

Deletes a task.

This endpoint is protected using the custom `@RequiresRole("ADMIN")` annotation.

Required header:

```
X-USER-ROLE: ADMIN
```

Returns **204 No Content** on successful deletion.

---

## Health Check

```
GET /api/public/health
```

Simple endpoint to verify the application is running.

This endpoint bypasses API key validation.

---

# Required Headers

For all task APIs:

```
X-API-KEY: secret123
```

For deleting a task:

```
X-USER-ROLE: ADMIN
```

---

# Project Structure

```
src/main/java
│
├── Filters
│   ├── RequestLoggingFilter
│   └── ApiKeyFilter
│
├── interceptors
│   ├── CustomHandlerInterceptor
│   └── RequiresRole
│
├── controller
│   ├── TaskController
│   └── HealthController
│
├── service
├── repository
├── entity
├── dto
├── config
└── exception
```

---

# Running the Application

Clone the repository:

```bash
git clone https://github.com/Abhisht164/TaskManagement.git
```

Run the application:

```bash
./mvnw spring-boot:run
```

The application starts on:

```
http://localhost:8080
```

---

# What I Learned

While building this project, I gained hands-on experience with:

* Chaining multiple filters using `@Order`
* Implementing custom filters with `OncePerRequestFilter`
* Skipping filter execution using `shouldNotFilter()`
* Building a custom `HandlerInterceptor`
* Method-level authorization using a custom annotation
* Sharing request data between Filters and Interceptors
* Tracking request execution time
* Generating and propagating Correlation IDs for request tracing
* Understanding the request lifecycle in a Spring Boot application
