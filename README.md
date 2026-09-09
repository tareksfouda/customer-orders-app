# Customer Orders Application

A full-stack web application for managing customers, orders, items, and purchases with Spring Boot backend and React frontend. Includes Spring Security OAuth2 JWT authentication and authorization.

## Architecture

- **Backend**: Spring Boot 3.x with Spring Security, Spring Data JPA, OAuth2, JWT
- **Frontend**: React 18.x with Axios for API calls
- **Database**: PostgreSQL
- **Containerization**: Docker & Docker Compose

## Features

- User authentication with JWT tokens
- OAuth2 integration for secure API access
- Role-based authorization (CUSTOMER, ADMIN)
- Customers can only view their own orders
- RESTful API for managing customers, orders, items, and purchases
- Real-time order tracking
- Responsive React UI

## Project Structure

```
.
├── backend/                 # Spring Boot application
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                # React application
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml       # Docker compose configuration
└── README.md
```

## Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 17+ (for local development)
- Node.js 16+ (for local development)

### Using Docker Compose

```bash
# Clone the repository
git clone https://github.com/tareksfouda/customer-orders-app.git
cd customer-orders-app

# Start all services
docker-compose up -d

# Check logs
docker-compose logs -f
```

Services will be available at:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- Database: localhost:5432

### Local Development

#### Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### Frontend

```bash
cd frontend
npm install
npm start
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new customer
- `POST /api/auth/login` - Login and get JWT token

### Customers
- `GET /api/customers/{id}` - Get customer details
- `PUT /api/customers/{id}` - Update customer

### Orders
- `GET /api/orders` - Get current customer's orders
- `GET /api/orders/{id}` - Get order details (only if owner)
- `POST /api/orders` - Create new order
- `PUT /api/orders/{id}` - Update order
- `DELETE /api/orders/{id}` - Delete order

### Items
- `GET /api/items` - Get all available items
- `GET /api/items/{id}` - Get item details

### Purchases
- `POST /api/purchases` - Create purchase
- `GET /api/purchases/{id}` - Get purchase details

## Authentication & Authorization

All API endpoints (except `/api/auth/*`) require JWT token in the `Authorization` header:

```
Authorization: Bearer <JWT_TOKEN>
```

Customers can only access:
- Their own profile
- Their own orders and purchases

Admins can access all resources.

## Database Schema

### Customers
```sql
CREATE TABLE customers (
  id BIGINT PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  phone VARCHAR(20),
  address VARCHAR(500),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

### Orders
```sql
CREATE TABLE orders (
  id BIGINT PRIMARY KEY,
  customer_id BIGINT NOT NULL,
  order_number VARCHAR(50) UNIQUE,
  status VARCHAR(50),
  total_amount DECIMAL(10,2),
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  FOREIGN KEY (customer_id) REFERENCES customers(id)
);
```

### Items
```sql
CREATE TABLE items (
  id BIGINT PRIMARY KEY,
  name VARCHAR(255),
  description TEXT,
  price DECIMAL(10,2),
  quantity INT,
  created_at TIMESTAMP
);
```

### Purchases
```sql
CREATE TABLE purchases (
  id BIGINT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  quantity INT,
  unit_price DECIMAL(10,2),
  created_at TIMESTAMP,
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (item_id) REFERENCES items(id)
);
```

## Environment Variables

### Backend (.env)
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/customer_orders_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### Frontend (.env)
```
REACT_APP_API_URL=http://localhost:8080/api
```

## Testing

### Backend
```bash
cd backend
mvn test
```

### Frontend
```bash
cd frontend
npm test
```

## Troubleshooting

### Database Connection Issues
- Ensure PostgreSQL is running
- Check credentials in `.env` file
- Verify database name matches configuration

### Port Already in Use
- Change ports in `docker-compose.yml`
- Kill process using the port: `lsof -i :PORT`

### CORS Issues
- Update `CORS_ALLOWED_ORIGINS` in backend configuration
- Ensure frontend URL is whitelisted

## Contributing

1. Create a feature branch from `develop`
2. Make your changes
3. Create a pull request

## License

MIT
