# Architecture

The platform uses a central customer accounting model where every hotspot shares the same account, package, and entitlement source of truth in PostgreSQL.

## Core flow

- Customer registers and logs in through the customer portal or admin app.
- Package purchase creates an entitlement, not a hotspot-local balance.
- RADIUS and hotspot gateways authorize the same central customer identity.
- Session accounting records usage and updates remaining entitlement.
- Administrators operate from a single dashboard that aggregates revenue, sessions, hotspots, and support data.

## Modules

- auth: registration, login, JWT issuers, refresh tokens
- customer: account profile, account visibility, customer lifecycle
- package: package catalog and package validation
- hotspot: hotspot registration and network health
- voucher: secure vouchers, redemption, auditability
- session: active and historical session tracking
- radius: RADIUS abstraction for access and accounting messages
- payment: payment provider abstraction and webhook validation
- reporting: revenue and usage report generation
- audit: admin action history and traceability

## Security model

- JWT is used for authenticated app sessions.
- Refresh tokens should rotate in production.
- Router credentials and secrets are encrypted at rest.
- Sensitive operational actions are role-limited.
- All database access uses JPA with parameterized queries.
