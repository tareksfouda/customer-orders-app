# Customer Orders Microservices Application

A Spring Boot microservices architecture for managing customer orders with three independent services:
- **Auth Service** (Port 8081) - Authentication and JWT token generation
- **Orders Service** (Port 8082) - Order management with token validation
- **Inventory Service** (Port 8083) - Product inventory management with token validation

## Architecture Overview

```
┌─────────────┐
│   Frontend  │ (React, Port 3000)
└──────┬──────┘
       │
       ▼
┌─────────────────────┐
│   API Gateway       │ (Port 8080)
│   Route & Auth      │
└──────┬──────────────┘
       │
   ┌───┴────┬──────────────┬──────────────┐
   │        │              │              │
   ▼        ▼              ▼              ▼
┌──────┐ ┌──────┐    ┌──────┐    ┌──────────┐
│ Auth │ │Orders│    │ Inv. │    │ Postgres │
│ Svc  │ │ Svc  │    │ Svc  │    │  DB (3)  │
└──┬───┘ └──┬───┘    └──┬───┘    └──────────┘
   │        │          │
   └────────┴──────────┘
   (Inter-service calls via REST/HTTP)
```

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose
- PostgreSQL 15 (or use Docker)

## Local Development Setup

### 1. Clone the repository
```bash
git clone <repo-url>
cd customer-orders-app
```

### 2. Start services with Docker Compose
```bash
docker-compose up -d
```

This will start:
- 3 PostgreSQL databases (ports 5432, 5433, 5434)
- Auth Service (port 8081)
- Orders Service (port 8082)
- Inventory Service (port 8083)
- API Gateway (port 8080)
- Frontend (port 3000)

### 3. Verify services are running
```bash
# Check all containers
docker-compose ps

# View logs
docker-compose logs -f auth-service
docker-compose logs -f orders-service
docker-compose logs -f inventory-service
```

## API Endpoints

### Auth Service (Port 8081)

#### Register User
```bash
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}

Response:
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "userId": 1
}
```

#### Login User
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "userId": 1
}
```

#### Validate Token
```bash
POST /api/auth/validate
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}

Response: true/false
```

### Orders Service (Port 8082)

#### Create Order
```bash
POST /api/orders/create
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "orderNumber": "ORD-001",
  "totalAmount": 299.99
}

Response:
{
  "id": 1,
  "userId": 1,
  "orderNumber": "ORD-001",
  "status": "PENDING",
  "totalAmount": 299.99,
  "orderItems": []
}
```

#### Get User Orders
```bash
GET /api/orders/my-orders
Authorization: Bearer <JWT_TOKEN>

Response: [Order1, Order2, ...]
```

### Inventory Service (Port 8083)

#### Create Product
```bash
POST /api/inventory/products
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "quantity": 50,
  "sku": "LAPTOP-001"
}

Response:
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "quantity": 50,
  "sku": "LAPTOP-001"
}
```

#### Get All Products
```bash
GET /api/inventory/products
Authorization: Bearer <JWT_TOKEN>

Response: [Product1, Product2, ...]
```

#### Check Stock
```bash
POST /api/inventory/check-stock
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "productId": 1,
  "quantity": 5
}

Response:
{
  "productId": 1,
  "available": true,
  "currentStock": 50,
  "requestedQuantity": 5
}
```

## Building and Running Individual Services

### Auth Service
```bash
cd services/auth-service
mvn clean package
java -jar target/auth-service-1.0.0.jar
```

### Orders Service
```bash
cd services/orders-service
mvn clean package
java -jar target/orders-service-1.0.0.jar
```

### Inventory Service
```bash
cd services/inventory-service
mvn clean package
java -jar target/inventory-service-1.0.0.jar
```

## Database Configuration

Each service has its own PostgreSQL database:

| Service | Database | Port | Connection String |
|---------|----------|------|-------------------|
| Auth | auth_db | 5432 | jdbc:postgresql://localhost:5432/auth_db |
| Orders | orders_db | 5433 | jdbc:postgresql://localhost:5433/orders_db |
| Inventory | inventory_db | 5434 | jdbc:postgresql://localhost:5434/inventory_db |

**Default credentials:** postgres/postgres

## AWS Deployment

### Prerequisites for AWS
- AWS Account
- AWS CLI configured
- IAM permissions for ECS, RDS, ECR, ALB

### Architecture on AWS

```
┌──────────────────────────────────────────────┐
│           AWS Cloud                          │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │  Application Load Balancer (ALB)       │ │
│  │  (Route traffic to ECS services)       │ │
│  └──────────────┬─────────────────────────┘ │
│                 │                            │
│     ┌───────────┼───────────┐               │
│     │           │           │               │
│  ┌──▼───┐   ┌──▼───┐   ┌──▼──────┐        │
│  │ECS   │   │ECS   │   │ECS      │        │
│  │Auth  │   │Orders│   │Inventory│        │
│  │Task  │   │Task  │   │Task     │        │
│  └──────┘   └──────┘   └─────────┘        │
│                                            │
│  ┌──────────────────────────────────────┐ │
│  │  AWS RDS (PostgreSQL)                │ │
│  │  - 3 Databases in same RDS instance  │ │
│  └──────────────────────────────────────┘ │
│                                            │
│  ┌──────────────────────────────────────┐ │
│  │  AWS Secrets Manager                 │ │
│  │  - JWT Secret                        │ │
│  │  - Database credentials              │ │
│  └──────────────────────────────────────┘ │
└──────────────────────────────────────────────┘
```

### Step 1: Push Docker Images to ECR

```bash
# Create ECR repository
aws ecr create-repository --repository-name customer-orders-app --region us-east-1

# Get login token
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com

# Build and push Auth Service
docker build -t customer-orders-app:auth-latest ./services/auth-service
docker tag customer-orders-app:auth-latest <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/customer-orders-app:auth-latest
docker push <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/customer-orders-app:auth-latest

# Build and push Orders Service
docker build -t customer-orders-app:orders-latest ./services/orders-service
docker tag customer-orders-app:orders-latest <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/customer-orders-app:orders-latest
docker push <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/customer-orders-app:orders-latest

# Build and push Inventory Service
docker build -t customer-orders-app:inventory-latest ./services/inventory-service
docker tag customer-orders-app:inventory-latest <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/customer-orders-app:inventory-latest
docker push <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/customer-orders-app:inventory-latest
```

### Step 2: Create RDS PostgreSQL Instance

```bash
aws rds create-db-instance \
  --db-instance-identifier customer-orders-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --master-username postgres \
  --master-user-password <STRONG_PASSWORD> \
  --allocated-storage 20 \
  --region us-east-1
```

### Step 3: Create Databases

After RDS is running:

```bash
# Connect to RDS
psql -h customer-orders-db.xxxxx.us-east-1.rds.amazonaws.com -U postgres

# Create databases
CREATE DATABASE auth_db;
CREATE DATABASE orders_db;
CREATE DATABASE inventory_db;
```

### Step 4: Create ECS Cluster

```bash
aws ecs create-cluster --cluster-name customer-orders-cluster --region us-east-1
```

### Step 5: Create ECS Task Definitions

See `aws/` directory for CloudFormation templates.

## Environment Variables

### Auth Service
```
SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/auth_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=<password>
JWT_SECRET=<min-32-chars-secret>
JWT_EXPIRATION=86400000
```

### Orders Service
```
SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5433/orders_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=<password>
JWT_SECRET=<min-32-chars-secret>
AUTH_SERVICE_URL=http://auth-service:8081
INVENTORY_SERVICE_URL=http://inventory-service:8083
```

### Inventory Service
```
SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5434/inventory_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=<password>
JWT_SECRET=<min-32-chars-secret>
AUTH_SERVICE_URL=http://auth-service:8081
```

## Testing

### Test Auth Service
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!"
  }'
```

### Test Orders Service (with token from login)
```bash
curl -X POST http://localhost:8080/api/orders/create \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "ORD-001",
    "totalAmount": 299.99
  }'
```

## Stopping Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

## Troubleshooting

### Database Connection Issues
```bash
# Check if databases are running
docker-compose ps

# View database logs
docker-compose logs postgres-auth
docker-compose logs postgres-orders
docker-compose logs postgres-inventory
```

### Service Connection Issues
```bash
# Test service connectivity
curl -v http://localhost:8081/api/auth/validate
curl -v http://localhost:8082/api/orders
curl -v http://localhost:8083/api/inventory/products
```

### Token Validation Issues
- Ensure JWT_SECRET is the same across all services
- Check token expiration time
- Verify Authorization header format: `Bearer <token>`

## Project Structure

```
customer-orders-app/
├── docker-compose.yml
├── README.md
├── api-gateway/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/...
├── services/
│   ├── auth-service/
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/main/java/com/customerorders/auth/
│   │       ├── AuthServiceApplication.java
│   │       ├── controller/
│   │       ├── service/
│   │       ├── entity/
│   │       ├── repository/
│   │       ├── dto/
│   │       ├── security/
│   │       └── config/
│   ├── orders-service/
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/main/java/com/customerorders/orders/
│   │       ├── OrdersServiceApplication.java
│   │       ├── controller/
│   │       ├── service/
│   │       ├── entity/
│   │       ├── repository/
│   │       ├── dto/
│   │       ├── filter/
│   │       └── config/
│   └── inventory-service/
│       ├── pom.xml
│       ├── Dockerfile
│       └── src/main/java/com/customerorders/inventory/
│           ├── InventoryServiceApplication.java
│           ├── controller/
│           ├── service/
│           ├── entity/
│           ├── repository/
│           ├── dto/
│           ├── filter/
│           └── security/
└── aws/
    ├── cloudformation-template.yml
    ├── task-definition-auth.json
    ├── task-definition-orders.json
    └── task-definition-inventory.json
```

## CI/CD Pipeline

See `.github/workflows/` for GitHub Actions CI/CD pipeline setup.

## Contributing

1. Create a feature branch
2. Make changes
3. Run tests
4. Create a Pull Request

## License

MIT License
