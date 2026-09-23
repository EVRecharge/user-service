# User Service - Independent Microservice

## Overview
User Service is a standalone microservice responsible for user registration, authentication, profile management, and OAuth integration. It operates completely independently with its own database, configuration, and deployment pipeline.

## Service Details
- **Service Name**: user-service
- **Port**: 8001
- **Database**: PostgreSQL (ev_user_db)
- **Authentication**: JWT + OAuth 2.0
- **Version**: 1.0.0

## Project Structure
```
user-service/
├── pom.xml                          # Independent Maven configuration
├── Dockerfile                       # Containerization
├── docker-compose.yml              # Standalone deployment
├── init-db.sql                     # Database schema
├── .env                            # Environment variables
├── README.md                       # This file
├── .gitignore
├── src/
│   ├── main/
│   │   ├── java/com/evcharging/userservice/
│   │   │   ├── UserServiceApplication.java
│   │   │   ├── controller/
│   │   │   │   └── UserController.java
│   │   │   ├── service/
│   │   │   │   └── UserService.java
│   │   │   ├── entity/
│   │   │   │   └── User.java
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java
│   │   │   ├── dto/
│   │   │   │   └── UserDTO.java
│   │   │   ├── security/
│   │   │   │   └── JwtTokenProvider.java
│   │   │   └── config/
│   │   │       └── SecurityConfig.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/evcharging/userservice/
│           └── UserServiceApplicationTests.java
└── logs/
    └── user-service.log

```

## Prerequisites
- Docker 20.10+
- Docker Compose 2.0+
- Java 11+ (for local development without Docker)
- Maven 3.6+ (for local development)

## Quick Start - Docker (Recommended)

### 1. Start the Service
```bash
# Navigate to user-service directory
cd user-service

# Start using docker-compose (creates database + service)
docker-compose up -d
```

### 2. Verify Service is Running
```bash
# Check logs
docker-compose logs -f user-service

# Test health endpoint
curl http://localhost:8001/actuator/health
```

### 3. Access the Service
```
API Endpoint: http://localhost:8001
Health Check: http://localhost:8001/actuator/health
Metrics: http://localhost:8001/actuator/metrics
```

## Local Development Setup

### 1. Build the Service
```bash
mvn clean install
```

### 2. Configure Database
Update `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ev_user_db
    username: postgres
    password: postgres
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

## API Endpoints

### User Registration
```bash
POST http://localhost:8001/api/users/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "phoneNumber": "+1234567890",
  "address": "123 Main St",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "vehicleType": "Tesla Model 3",
  "vehicleNumber": "ABC123"
}
```

### OAuth Login
```bash
POST http://localhost:8001/api/users/oauth/login?email=john@example.com&firstName=John&lastName=Doe&oauthProvider=GOOGLE&oauthId=google_id_123
```

### Get User
```bash
GET http://localhost:8001/api/users/{userId}
Authorization: Bearer <jwt_token>
```

### Update User
```bash
PUT http://localhost:8001/api/users/{userId}
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "phoneNumber": "+9876543210"
}
```

### Delete User
```bash
DELETE http://localhost:8001/api/users/{userId}
Authorization: Bearer <jwt_token>
```

### Validate Token
```bash
POST http://localhost:8001/api/users/validate-token
Authorization: Bearer <jwt_token>
```

## Environment Configuration

All environment variables can be set via `.env` file or Docker environment:

```env
# Service
SERVER_PORT=8001
SPRING_PROFILE=dev

# Database
DB_HOST=user-service-postgres
DB_PORT=5432
DB_NAME=ev_user_db
DB_USER=postgres
DB_PASSWORD=postgres

# Eureka Discovery
EUREKA_SERVER=http://localhost:8761/eureka/

# Security
JWT_SECRET=your_secret_key_here
JWT_EXPIRATION=86400000

# JVM
JAVA_OPTS=-Xms256m -Xmx512m
```

## Database Schema

### Users Table
| Column | Type | Description |
|--------|------|-------------|
| user_id | SERIAL PRIMARY KEY | Unique user identifier |
| first_name | VARCHAR(100) | First name |
| last_name | VARCHAR(100) | Last name |
| email | VARCHAR(255) UNIQUE | Email address |
| phone_number | VARCHAR(20) UNIQUE | Phone number |
| address | VARCHAR(500) | Street address |
| city | VARCHAR(100) | City |
| state | VARCHAR(50) | State/Province |
| zip_code | VARCHAR(20) | Postal code |
| vehicle_type | VARCHAR(100) | Type of EV |
| vehicle_number | VARCHAR(50) | License plate |
| oauth_provider | VARCHAR(50) | OAuth provider name |
| oauth_id | VARCHAR(255) | OAuth user ID |
| is_active | BOOLEAN | Account status |
| created_at | TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | Last update timestamp |

## Docker Management

### Build Docker Image
```bash
docker-compose build
```

### Start Service
```bash
docker-compose up -d
```

### Stop Service
```bash
docker-compose down
```

### View Logs
```bash
docker-compose logs -f user-service
```

### Restart Service
```bash
docker-compose restart user-service
```

### Access Database
```bash
docker-compose exec user-service-postgres psql -U postgres -d ev_user_db
```

## Production Deployment

### 1. Update Configuration
Edit `.env` with production values:
```env
SPRING_PROFILE=prod
DB_PASSWORD=<secure_password>
JWT_SECRET=<long_random_string>
EUREKA_SERVER=<production_eureka_url>
JAVA_OPTS=-Xms512m -Xmx2g
```

### 2. Build Production Image
```bash
docker build -t evcharging/user-service:1.0.0 .
```

### 3. Push to Registry
```bash
docker push evcharging/user-service:1.0.0
```

### 4. Deploy to Kubernetes/Docker Swarm
```bash
# Using docker stack
docker stack deploy -c docker-compose.yml user-service-prod

# Or using Kubernetes
kubectl apply -f k8s-deployment.yaml
```

## Monitoring & Logs

### Health Check
```bash
curl http://localhost:8001/actuator/health
```

### Metrics
```bash
curl http://localhost:8001/actuator/metrics
```

### View Logs
```bash
# Docker logs
docker-compose logs -f user-service

# File logs
tail -f logs/user-service.log
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### API Testing
```bash
# Using curl
curl -X POST http://localhost:8001/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@example.com",...}'

# Using Postman
# Import API endpoints and test
```

## Troubleshooting

### Service Won't Start
```bash
# Check logs
docker-compose logs user-service

# Check if port is in use
lsof -i :8001

# Check database connection
docker-compose logs user-service-postgres
```

### Database Connection Error
```bash
# Verify database is running
docker-compose exec user-service-postgres pg_isready

# Check credentials in .env file
cat .env | grep DB_

# Reset database
docker-compose down -v
docker-compose up -d
```

### Can't Reach Eureka
```bash
# Verify Eureka is running
curl http://localhost:8761

# Update EUREKA_SERVER in .env
# Restart service
docker-compose restart user-service
```

## Performance Tuning

### Database Connection Pool
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
```

### JVM Memory
```env
JAVA_OPTS=-Xms512m -Xmx2g
```

### Caching
Enable caching for frequently accessed user profiles:
```yaml
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=500,expireAfterWrite=10m
```

## Security Considerations

1. **JWT Secret**: Use strong, randomly generated secrets
2. **HTTPS**: Enable SSL/TLS in production
3. **Password**: Never commit `.env` with real passwords
4. **Database**: Use strong passwords, restrict access
5. **CORS**: Configure allowed origins carefully

## Dependencies

### Spring Boot
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-validation

### Database
- PostgreSQL JDBC driver
- Hibernate ORM

### Security
- JWT (JJWT)
- Spring Security OAuth2

### Service Discovery
- Spring Cloud Netflix Eureka

## CI/CD Pipeline

### GitHub Actions Example
```yaml
name: User Service CI/CD
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
      - run: mvn clean install
      - run: docker build -t user-service:latest .
```

## Version History
- **1.0.0** (2024-01-15) - Initial release

## Support & Contributing

### Reporting Issues
1. Check existing issues on GitHub
2. Create detailed bug report with logs
3. Include reproduction steps

### Contributing
1. Fork the repository
2. Create feature branch
3. Submit pull request

## License
MIT License - See LICENSE file

## Contact
- Development Team: dev@evcharging.com
- Issues: GitHub Issues
- Documentation: See `/docs` directory

---

**Status**: Production Ready ✅
**Last Updated**: June 30, 2024
**Maintainer**: EV Charging Team
