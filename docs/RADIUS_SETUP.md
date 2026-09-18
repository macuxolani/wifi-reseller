# RADIUS Setup

The project is designed around a RADIUS-compatible architecture that can integrate with MikroTik or other Hotspot controllers.

## Supported RADIUS flows

- Access-Request
- Access-Accept
- Access-Reject
- Accounting-Start
- Accounting-Interim-Update
- Accounting-Stop

## Configuration

Set these environment variables before deployment:

- RADIUS_HOST
- RADIUS_PORT
- RADIUS_SECRET
- RADIUS_AUTHENTICATION_TIMEOUT

The backend exposes an abstraction layer so router-specific code remains isolated from the main application services.

## Production deployment note

Do not attach a production hotspot until the central accounting and billing rules are validated in a staging environment.
