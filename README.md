# API Gateway 

## Overview

This **API Gateway** serves as the entry point to a microservices architecture, providing centralized routing, authentication, and service discovery. Designed to streamline requests and enhance security, this gateway acts as a robust intermediary between clients and backend services.

## Features

### 1. **JWT Authentication**
- Integrates a **JWT-based authentication system** to validate user requests at the gateway level.
- Authentication requests are sent to an **Authentication Server** to verify credentials and issue tokens.
- Implements caching with **Redis**, so authenticated user details are stored temporarily, avoiding repeated calls to the Authentication Server for the same user.  
  - This significantly reduces overhead and improves performance.

### 2. **Single Sign-On (SSO) Simplification**
- Once a user is authenticated at the gateway, **all downstream services** are shielded from handling authentication or SSO logic.
- Simplifies the security model across services by enforcing a single authentication entry point.

### 3. **Service Discovery**
- Fully configurable with **Eureka Discovery Server**, enabling dynamic service resolution and routing.
- Automatically discovers registered backend services, reducing the need for manual configurations.

### 4. **Request Restriction at the Gateway**
- Ability to restrict unauthorized or invalid requests at the gateway itself, ensuring backend services only receive secure, verified traffic.

## Benefits

- **Improved Security**: Centralized JWT validation and caching mechanism ensure secure and efficient handling of user authentication.
- **Performance Optimization**: Redis caching minimizes redundant calls to the Authentication Server.
- **Simplified Microservices**: Downstream services focus on business logic without needing to implement authentication or SSO.
- **Scalability**: Integration with Eureka enables seamless scaling and dynamic service discovery in large systems.

## Technology Stack

- **Spring Cloud**: Core framework for implementing gateway functionalities.
- **Redis**: For caching authentication data.
- **Eureka Discovery Server**: For service registration and discovery (optional and configurable).
- **JWT**: For secure token-based authentication.

## Getting Started

### Prerequisites
- Java 17 or higher
- Redis server running locally or accessible remotely
- Optional: Eureka Discovery Server

### Running the Application
1. Clone the repository.
   ```bash
   git clone https://github.com/hemayet-nirjhoy/api-gateway.git
   ```
2. Configure the application properties for Redis and Eureka (if applicable).
3. Build and run the application:
 ```bash
   ./mvnw spring-boot:run
   ```
4. Access the gateway at:
```arduino
   http://localhost:8080
```
### Configurations
 * **Redis Cache Expiry**: Configure the TTL (time-to-live) for user details in Redis to balance performance and freshness.
 * **Eureka Server**: Enable or disable Eureka integration via application properties. If you do not have any, hardcode the base URL of the microservices in the application.yaml
 * **JWT Settings**: Customize token expiration, signing algorithm, and secret in the configuration.

### Contribution
We welcome contributions! Please fork the repository, create a branch for your feature or bug fix, and submit a pull request.

### License
This project is licensed under the Apache License 2.0.
