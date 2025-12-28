# Task API – In-Code API Testing Example

## Why I Built This

I built this repository to make a simple point that I've seen play out repeatedly over the years: writing API tests inside the codebase pays off far more than it costs, especially on active systems.

This project isn't about building the most feature-complete task (todo) API. It's a deliberately small Spring Boot application used to illustrate how in-code API tests help teams move faster with more confidence, even when timelines are tight and systems evolve.

While this repo uses Spring Boot, the ideas here are not Spring-specific. The same approach applies to Node.js services, other JVM frameworks, or any API-driven backend.

## Who This Repo Is For

This repository is useful if you are:
- **Backend developers** who want faster feedback when changing or refactoring APIs
- **Testers / QA engineers** who want to understand how APIs behave from inside the code, not just from the outside
- **Mixed teams** where quality is a shared responsibility, not something handed off at the end

If you've ever hesitated to touch an endpoint because you weren't sure what might break, this repo is for you.

## What This Project Is About

This project is not meant to be a feature-complete task manager.

It is meant to:
- Show how to structure API tests inside a Spring Boot application
- Demonstrate handling authentication in tests
- Cover critical workflows (create, update, status change)
- Illustrate how tests can act as living documentation

**The focus is on testing what matters, not testing everything.**

## Features

- User authentication (JWT-based)
- Create tasks
- Update task details
- Update task status (e.g. `PENDING` → `COMPLETED`)
- Basic validation and error handling
- In-code API tests using MockMvc

## Tech Stack

- Java
- Spring Boot
- Spring Security
- JPA / Hibernate
- H2 (test profile)
- JUnit 5
- MockMvc

## Project Structure (High Level)
```
src
 ├── main
 │   └── java
 │       └── com.ovansa.task_api
 │           ├── controller
 │           ├── service
 │           ├── repository
 │           ├── domain
 │           └── security
 └── test
     └── java
         └── com.ovansa.task_api
             ├── controller
             │   ├── task
             │   └── auth
             └── util
```

- `controller` tests focus on API behavior
- `TestUtils` contains helpers for reusable test setup (users, tasks, login)

## Getting Started

### Running the Application
```bash
./mvnw spring-boot:run
```

The API will start on the configured port (default Spring Boot port).

### Running Tests
```bash
./mvnw test
```

Tests use a dedicated test profile and an in-memory database.  
Each test cleans up after itself to keep scenarios isolated and predictable.

## Why Tests Live in the Code

The core idea behind this project is simple:

**API tests inside the codebase provide fast feedback, confidence, and shared understanding.**

Some of the benefits demonstrated here:
- Fast feedback when changes break existing behavior
- Clear documentation of how endpoints are expected to work
- Confidence to refactor without fear of silent regressions
- Easy setup of realistic test data to reproduce tricky bugs

The tests intentionally focus on critical paths — authentication, task creation, updates, and status changes.

## Examples

### Creating a Task (Authenticated)
```java
@Test
@DisplayName("Should create task successfully as authenticated user")
void shouldCreateTaskAsAuthenticatedUser() throws Exception {
    String rawPassword = "Password@123";
    User user = TestUtils.saveUser(userRepository, passwordEncoder, rawPassword);

    String token = TestUtils.loginAndGetToken(mockMvc, user.getEmail(), rawPassword);

    CreateTaskRequest request = new CreateTaskRequest();
    request.setTitle("Eat");

    mockMvc.perform(post("/task")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.id").exists())
            .andExpect(jsonPath("$.data.title").value("Eat"));
}
```

This test shows:
- Authentication handled directly in tests
- Real API calls through the controller layer
- Assertions on both HTTP status and response body

### Failing Without Authentication
```java
@Test
@DisplayName("Should fail to create task without authentication")
void shouldFailTaskCreationWithoutAuth() throws Exception {
    CreateTaskRequest request = new CreateTaskRequest();
    request.setTitle("Eat");

    mockMvc.perform(post("/task")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
}
```

Failure cases like this are intentional. Tests force you to think about:
- Missing authentication
- Invalid input
- Access control and permissions

## Test Utilities

To keep tests readable and focused, common setup logic lives in `TestUtils`, such as:
- Creating users
- Logging in and retrieving JWT tokens
- Creating tasks for specific users

This makes it easy to set up realistic scenarios and clean them up automatically.

## Authentication in Tests

Authentication is handled in-code by:
1. Creating a test user
2. Calling the login endpoint
3. Extracting the JWT token
4. Using the token in subsequent API calls

For systems where authentication is external, a different approach may be needed (e.g. mocking or calling services directly). The goal here is practicality, not perfection.

## What This Project Is Not

- It is **not** a full production-ready task manager
- It does **not** aim to test every possible edge case
- It is **not** tied strictly to Spring Boot — the ideas apply elsewhere

## Key Takeaway

**You don't need to test everything.**

Test the things that hurt when they break:
- Authentication
- Core business workflows
- Data integrity
- Access control

This project exists to show how lightweight, well-placed API tests can make a codebase easier to change, easier to understand, and safer to work in.

## Author

**Ovansa**  
- GitHub: [@ovansa](https://github.com/ovansa)
- Blog: [Blog](https://www.ovansa.me/blog)

## Acknowledgments

This project was built to accompany a blog post on API testing. Read the full article: [Why Writing API Tests in Code Matters](https://www.ovansa.me/blogpost/api-tests-in-code)
