# API Overview

## Authentication

- POST /api/auth/register
- POST /api/auth/login
- POST /api/auth/refresh

## Customer

- GET /api/customers/me
- GET /api/customers/me/balance
- GET /api/customers/me/sessions
- GET /api/customers/me/payments

## Packages

- GET /api/packages

## Hotspots

- GET /api/hotspots/available

## Vouchers

- POST /api/vouchers/redeem

## Admin

- GET /api/admin/customers
- GET /api/admin/customers/{id}
- GET /api/admin/hotspots
- GET /api/admin/sessions
- GET /api/admin/reports/revenue

All endpoints return structured JSON with consistent validation and error handling through the global exception handler.
