# Database

The application uses PostgreSQL and Flyway for schema evolution.

## Main entities

- customers
- packages
- vouchers
- hotspot
- sessions
- payments
- audit_logs
- roles
- admin_users
- customer_entitlements
- devices
- radius_accounts
- support_tickets
- notifications

## Initial migration

The initial schema and seed data live in:

- backend/src/main/resources/db/migration/V1__init_schema.sql

## Important design rules

- UUID keys are used where appropriate.
- Unique constraints exist for customer email, customer username, voucher code, and hotspot code.
- Session and hotspot status indexes are included for operational reporting.
- The central accounting model keeps balances in a shared database rather than per-hotspot data stores.
