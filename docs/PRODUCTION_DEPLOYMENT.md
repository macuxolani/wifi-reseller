# Production deployment

## Required controls

- Java 21 runtime for the backend build.
- PostgreSQL 16 with encrypted storage, automated backups, and restore tests.
- HTTPS for the portal and API.
- Long random values for `JWT_SECRET`, `POSTGRES_PASSWORD`, and `DATABASE_PASSWORD`.
- Secrets supplied through the host secret manager or CI/CD environment, never committed to Git.
- Firewall rules that allow only application traffic to the API and RADIUS UDP traffic from registered routers.
- Centralized logs, metrics, alerting, and a process for rotating RADIUS and JWT secrets.

## First deployment

```bash
cp .env.example .env
# Replace every placeholder in .env.
docker compose up -d --build
docker compose ps
curl http://localhost:8080/actuator/health
```

The default Compose database URL uses `host.docker.internal` because this development container does not permit sibling-container bridge traffic. In a normal production Docker host, set `DATABASE_URL=jdbc:postgresql://postgres:5432/yourwifi` and remove the host-gateway fallback if direct Compose service networking is available.

## Operational checks

```bash
docker compose logs --tail=200 backend
docker compose exec postgres pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB"
```

Do not use `ddl-auto=create` or disable Flyway validation. Schema changes must be additive, reviewed, and shipped as numbered migrations.

## Current boundary

This repository is production-oriented application infrastructure, not a complete managed RADIUS provider. Before serving public users, connect a real RADIUS daemon or gateway, configure payment-provider webhooks, add operator RBAC, and complete load, failover, backup-restore, and security testing.