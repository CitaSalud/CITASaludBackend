# CITASalud - Agendamiento

[![CI/CD Pipeline](https://github.com/SamuelPuerta/CITASalud-Agendamiento/actions/workflows/build.yml/badge.svg)](https://github.com/SamuelPuerta/CITASalud-Agendamiento/actions/workflows/build.yml)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A robust microservice for medical appointment scheduling built with Spring Boot, designed to handle user management, authentication, slot availability, and appointment booking with high reliability and scalability.

## 🚀 Features

- **User Management**: Secure user registration and authentication with JWT tokens
- **Appointment Scheduling**: Book, modify, and cancel medical appointments
- **Slot Management**: Real-time availability checking for appointment slots
- **Security**: JWT-based authentication and role-based access control
- **Database**: PostgreSQL with Flyway migrations for schema management
- **Messaging**: RabbitMQ integration for asynchronous processing
- **Email Notifications**: SMTP integration for appointment confirmations
- **API Documentation**: OpenAPI/Swagger documentation
- **Monitoring**: Spring Boot Actuator with Prometheus metrics
- **Testing**: Comprehensive unit and integration tests with JaCoCo coverage
- **CI/CD**: Automated pipeline with GitHub Actions, SonarCloud analysis, and Docker deployment
- **Security Scanning**: Snyk integration for vulnerability detection

## 🛠 Tech Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.5.6** - Framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data access layer
- **Spring GraphQL** - GraphQL API support
- **Spring AMQP** - RabbitMQ messaging
- **Spring Mail** - Email functionality
- **Spring Quartz** - Scheduled tasks
- **JWT** - Token-based authentication

### Database & Infrastructure
- **PostgreSQL 17** - Primary database
- **Flyway** - Database migrations
- **RabbitMQ 4** - Message broker
- **Docker & Docker Compose** - Containerization

### Testing & Quality
- **JUnit 5** - Unit testing
- **Mockito** - Mocking framework
- **JaCoCo** - Code coverage
- **SonarCloud** - Code quality analysis
- **Snyk** - Security scanning

### DevOps & Monitoring
- **GitHub Actions** - CI/CD pipeline
- **Spring Boot Actuator** - Application monitoring
- **Prometheus** - Metrics collection
- **Adminer** - Database administration
- **MailHog** - Email testing

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- Git

## 🏃‍♂️ Quick Start

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/SamuelPuerta/CITASalud-Agendamiento.git
   cd CITASalud-Agendamiento
   ```

2. **Set up environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Start infrastructure with Docker Compose**
   ```bash
   docker-compose up -d postgres rabbitmq mailhog adminer
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the application**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - GraphQL Playground: http://localhost:8080/graphiql
   - Adminer (Database): http://localhost:8081
   - MailHog (Emails): http://localhost:8025

### Docker Deployment

```bash
docker-compose up --build
```

## 🔧 Configuration

The application uses environment variables for configuration. Key settings include:

- **Database**: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- **JWT**: `JWT_SECRET`, `JWT_EXPIRATION`
- **RabbitMQ**: `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USER`, `RABBITMQ_PASS`
- **Email**: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`
- **Server**: `SERVER_PORT`

See `.env.example` for a complete list of configuration options.

## 📚 API Documentation

### REST Endpoints
- **Authentication**: `/api/auth/login`, `/api/auth/register`
- **Users**: `/api/users`
- **Appointments**: `/api/appointments`
- **Slots**: `/api/slots`

### GraphQL
The application also provides GraphQL endpoints for flexible querying:
- Playground: http://localhost:8080/graphiql

## 🧪 Testing

Run the test suite with coverage:

```bash
./mvnw test
```

View coverage report:
```bash
open target/site/jacoco/index.html
```

## 🔒 Security

- JWT tokens for stateless authentication
- Password encryption with BCrypt
- Role-based access control (USER, ADMIN)
- Input validation and sanitization
- CORS configuration
- Security headers

## 📊 Monitoring

Access monitoring endpoints:
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Prometheus: http://localhost:8080/actuator/prometheus

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built with Spring Boot and modern Java practices
- Inspired by clean architecture principles
- Thanks to the open-source community for amazing tools and libraries

## 📞 Support

For questions or support, please open an issue on GitHub or contact the development team.

---

**CITASalud** - Making healthcare appointment scheduling simple and efficient.