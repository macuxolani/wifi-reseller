# Hotspot onboarding

YourWiFi treats each router as a hotspot endpoint. Customer balance and package entitlement remain central, so a customer can move between registered hotspots without creating a second account or balance.

## 1. Prepare the platform

Set these values in the deployment environment:

- `RADIUS_SERVER`: public DNS name or IP of the RADIUS service
- `RADIUS_AUTHENTICATION_PORT`: normally `1812/udp`
- `RADIUS_ACCOUNTING_PORT`: normally `1813/udp`
- `RADIUS_SHARED_SECRET`: store this in the RADIUS/router secret manager; never commit or return it from the API
- `CAPTIVE_PORTAL_URL`: the HTTPS portal URL shown to WiFi users

The API connection guide intentionally returns the secret variable name, not the secret itself.

## 2. Register the hotspot

Authenticate as an operator and create the endpoint:

```http
POST /api/hotspots
Authorization: Bearer <access-token>
Content-Type: application/json
```

```json
{
  "name": "Sandton Office",
  "code": "SANDTON-01",
  "locationName": "Sandton",
  "address": "Example Street",
  "routerIp": "10.10.0.1",
  "routerApiPort": 8728,
  "maxUsers": 250,
  "bandwidthCapacity": 1000000000
}
```

The response contains the hotspot ID, RADIUS host and ports, and captive portal URL. Store the hotspot ID in your router integration records.

## 3. Configure MikroTik or another controller

At minimum, the controller must:

1. Use the RADIUS server and shared secret from the deployment secret store.
2. Send `Access-Request` packets to the authentication port.
3. Send `Accounting-Start`, interim updates, and `Accounting-Stop` to the accounting port.
4. Redirect unauthenticated clients to `CAPTIVE_PORTAL_URL`.
5. Preserve the client MAC address, session ID, hotspot code, and byte counters in accounting events.

Allow UDP `1812` and `1813` from each router to the RADIUS service. Do not expose PostgreSQL to the public internet. Put the portal and API behind HTTPS and a reverse proxy in production.

## 4. Verify before opening the hotspot

- Register a test customer and purchase or redeem a package.
- Confirm the captive portal can discover `GET /api/hotspots/available`.
- Confirm the router receives Access-Accept only for a customer with remaining entitlement.
- Confirm interim and stop accounting reduce the central balance correctly.
- Test a second hotspot with the same customer account.

The current application provides the onboarding contract and domain services. A production deployment still needs a real RADIUS daemon or gateway wired to the `RadiusAuthenticationService` and accounting event handlers before public traffic is enabled.