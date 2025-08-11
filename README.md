# Email Connector Core

A robust Spring Boot microservice for handling email operations with support for portfolio messaging and statistics tracking. Built with Java 21 and Spring Boot 3.4.2, this service provides a RESTful API for email management with Eureka service discovery integration.

## 🚀 Features

- **Email Sending**: Send emails with customizable subject, message, and recipient
- **Portfolio Messaging**: Specialized endpoint for portfolio-related queries
- **Statistics Tracking**: Daily email statistics and monitoring
- **Service Discovery**: Integrated with Netflix Eureka for microservice architecture
- **Multi-Environment Support**: Separate configurations for development and production
- **Docker Support**: Containerized deployment with optimized JVM settings
- **Security**: Non-root user execution in containers

## 🛠️ Technology Stack

- **Java**: 21 (Eclipse Temurin)
- **Spring Boot**: 3.4.2
- **Spring Cloud**: Netflix Eureka Client
- **Maven**: 3.9.9
- **Docker**: Multi-stage build with Alpine Linux
- **Lombok**: For reducing boilerplate code
- **Infisical**: For secure configuration management

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.9.9 or higher
- Docker (for containerized deployment)
- Eureka Service Registry (for service discovery)

## 🚀 Quick Start

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd emailConnecter-core
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run -P dev
   ```

   The application will start on port `10002` with the dev profile.

### Docker Deployment

1. **Build the Docker image**
   ```bash
   docker build -t email-connector:latest .
   ```

2. **Run the container**
   ```bash
   docker run -p 10002:10002 email-connector:latest
   ```

## ⚙️ Configuration

### Environment Profiles

The application supports multiple environment profiles:

- **Dev Profile** (`-P dev`): Default profile for development
- **Prod Profile** (`-P prod`): Production profile with optimized settings

### Application Properties

Key configuration properties:

```yaml
server:
  port: 10002

spring:
  application:
    name: EMAIL-SERVICE

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    hostname: localhost
```

## 📡 API Endpoints

### Base URL
```
http://localhost:10002/email-connector
```

### Available Endpoints

#### 1. Welcome Message
- **GET** `/welcome`
- **Description**: Returns a welcome message for the Email Connector service
- **Response**: ASCII art welcome message

#### 2. Send Email
- **POST** `/send-mail`
- **Description**: Send an email with custom subject and message
- **Request Body**:
  ```json
  {
    "to": "recipient@example.com",
    "subject": "Email Subject",
    "message": "Email message content"
  }
  ```
- **Response**: `BaseResponse` with operation status

#### 3. Send Portfolio Message
- **POST** `/send-portfolio-message`
- **Description**: Send a specialized portfolio query message
- **Request Body**: `PortfolioMessageRequest` object
- **Response**: `BaseResponse` with operation status

#### 4. Get Statistics
- **GET** `/get-current-day-statistics`
- **Description**: Retrieve current day email statistics
- **Response**: `BaseResponse` with statistics data

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/emailConnecter/
│   │       ├── config/           # Configuration classes
│   │       ├── constants/        # Application constants
│   │       ├── controller/       # REST controllers
│   │       ├── request/          # Request DTOs
│   │       ├── response/         # Response DTOs
│   │       ├── service/          # Business logic services
│   │       └── utility/          # Utility classes
│   └── resources/
│       └── profile/
│           ├── dev/              # Development configuration
│           └── prod/             # Production configuration
```

## 🔧 Development

### Building the Project

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package with specific profile
mvn package -P dev    # Development profile
mvn package -P prod   # Production profile
```

### Running Tests

```bash
mvn test
```

## 🐳 Docker

### Multi-Stage Build

The Dockerfile uses a multi-stage build approach:

1. **Build Stage**: Maven-based build with Java 21
2. **Runtime Stage**: Lightweight JRE with Alpine Linux

### Container Features

- Non-root user execution for security
- JVM optimization for containers
- Health check support
- Resource-aware memory management

### Docker Commands

```bash
# Build image
docker build -t email-connector:latest .

# Run container
docker run -d -p 10002:10002 --name email-connector email-connector:latest

# View logs
docker logs email-connector

# Stop container
docker stop email-connector
```

## 📊 Monitoring & Health

The service integrates with Eureka for service discovery and monitoring. Key monitoring points:

- Service registration status
- API endpoint health
- Email sending statistics
- Daily operation metrics

## 🔒 Security Considerations

- Non-root user execution in containers
- Secure configuration management with Infisical
- Input validation for email requests
- Logging for audit trails

## 🚀 Deployment

### Kubernetes Deployment

The project includes Kubernetes deployment manifests:

- `email-connector-deployment.yaml`: Deployment configuration
- `email-connector-service.yaml`: Service configuration
- `nginx.conf`: Nginx configuration for load balancing

### Production Considerations

- Use production profile (`-P prod`)
- Configure proper Eureka service registry URLs
- Set appropriate JVM memory settings
- Enable monitoring and logging
- Configure SSL/TLS if required

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:

- Create an issue in the repository
- Contact the development team
- Check the application logs for debugging information

## 🔄 Version History

- **v0.0.1-SNAPSHOT**: Initial development version
  - Basic email functionality
  - Portfolio messaging support
  - Eureka integration
  - Docker containerization

---

**Note**: This is a core email connector service designed to be integrated into larger microservice architectures. Ensure proper configuration of email servers and Eureka service registry before deployment.
