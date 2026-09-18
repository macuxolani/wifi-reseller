# YourWiFi Control Centre

This repository contains a production-oriented MVP for a WiFi management and billing platform built with Java 21, Spring Boot 3, PostgreSQL, React + TypeScript, and Docker Compose.

## Stack

- Backend: Spring Boot 3 + Java 21 + Spring Security + JPA + Flyway
- Database: PostgreSQL
- Frontend: React + TypeScript + Vite
- Auth: JWT access/refresh tokens
- Network: RADIUS/MikroTik abstraction ready
- Deployment: Docker Compose

## Run locally

### Backend

```bash
cd backend
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

### Database

```bash
docker compose up -d postgres
```

For the complete local stack, copy `.env.example` to `.env`, replace the placeholder secrets, then run `docker compose up -d --build`. The portal is available at `http://localhost:5173` and the API health check is `http://localhost:8080/actuator/health`.

## Default seed admin

- Username: admin
- Email: admin@yourwifi.local
- Password: changeit

## Core architecture

- Customer account is centrally managed and reused across hotspots.
- Package and voucher entitlements feed the central billing model.
- Hotspot network integration is abstracted behind service interfaces for RADIUS and MikroTik compatibility.
- Admin dashboard is a dark responsive dashboard center.

## Docs

See the documentation set under the root docs folder for deployment, architecture, API, database, security, and network setup notes.

- [Production deployment](docs/PRODUCTION_DEPLOYMENT.md)
- [Hotspot onboarding](docs/HOTSPOT_ONBOARDING.md)
