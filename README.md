# Back-end Architecture - M&A Sante Assurance

## 1. Overview
The backend is a Spring Boot application that exposes a REST API for insurance management.

It handles:
- authentication and sessions,
- client onboarding,
- pack and guarantee management,
- subscription lifecycle,
- insurance card generation,
- partner and convention data,
- payment flows,
- notifications by email and SMS,
- live agent tracking through WebSocket.

## 2. Technical Stack
- Java 21
- Spring Boot 3.5
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- WebSocket
- OpenAPI / Swagger
- JWT-based authentication
- BCrypt password hashing
- Mailjet for email
- Twilio for SMS
- Cloudinary for image upload
- PayDunya for payment integration

## 3. Architecture Style
The backend follows a layered architecture:

- `controller` layer: HTTP endpoints and request/response handling
- `service` layer: business rules and orchestration
- `repository` layer: persistence access
- `entity` layer: JPA models
- `dto` layer: request/response contracts
- `config` layer: security, CORS, WebSocket, external services
- `common` layer: shared enums, utils, exceptions, validation, audit

This structure keeps responsibilities separated and makes the code easier to test, extend, and maintain.

## 4. Domain Modules
The codebase is organized by business domain:

- `auth`
  - login, register, logout, refresh session, OTP flow
- `user`
  - base user management
- `client`
  - client profile, numero assurance, client-facing data
- `agent`
  - agent management and live location tracking
- `admin`
  - administration endpoints and dashboards
- `pack`
  - insurance packs / formules
- `garantie`
  - guarantees linked to packs
- `packgarantie`
  - many-to-many link between packs and guarantees
- `souscription`
  - subscription lifecycle
- `carte`
  - digital insurance card
- `paiement`
  - payment processing and status tracking
- `partenaire`
  - partner health structures
- `conventionpartenaire`
  - conventions with partners
- `beneficiaire`
  - beneficiaries linked to a client/subscription
- `otp`
  - OTP generation, storage, verification
- `upload`
  - file upload endpoint and Cloudinary integration
- `notification`
  - email and SMS notification adapters
- `security`
  - JWT filter, token handling, auth model
- `common`
  - enums, exceptions, audit logs, utilities, validation

## 5. Package Structure
```text
back_end/src/main/java/com/ma_sante_assurance/
├── auth/
├── admin/
├── agent/
├── beneficiaire/
├── carte/
├── client/
├── common/
├── config/
├── conventionpartenaire/
├── garantie/
├── notification/
├── otp/
├── pack/
├── packgarantie/
├── paiement/
├── partenaire/
├── security/
├── souscription/
├── upload/
└── user/
```

## 6. Request Flow
### 6.1 Authentication flow
1. The client calls `POST /api/auth/register`.
2. The backend creates the client account.
3. The backend generates an OTP.
4. The OTP is sent by email and SMS.
5. The user enters the OTP on the frontend.
6. The backend validates the OTP.
7. The account is activated.
8. The session is created and returned to the frontend.

### 6.2 Insurance card flow
1. A subscription is created for the client.
2. The backend generates the insurance card.
3. The card is linked to the subscription.
4. The frontend displays the digital card and QR code.

### 6.3 Notification flow
1. A business event occurs.
2. The service layer triggers the notification service.
3. The notification adapter sends the email or SMS.
4. The result is logged for traceability.

## 7. Security Architecture
The backend security is built around:

- JWT authentication filter,
- role-based authorization,
- bcrypt password encoding,
- CSRF protection for SPA compatibility,
- stateless session policy,
- CORS configuration,
- endpoint-level access rules,
- audit logs for sensitive actions.

### Protected examples
- `ADMIN` only endpoints for administration
- `AGENT` endpoints for agent management
- `CLIENT` endpoints for client data and insurance card
- public endpoints for login, register and selected catalog data

## 8. Database Layer
The persistence layer is based on PostgreSQL and JPA entities.

Main entities:
- `User`
- `Auth`
- `Client`
- `Agent`
- `Pack`
- `Garantie`
- `PackGarantie`
- `Souscription`
- `Carte`
- `Paiement`
- `Partenaire`
- `ConventionPartenaire`
- `Beneficiaire`
- `OtpCode`
- `AuditLog`

Flyway is used to version and evolve the schema safely.

## 9. External Integrations
### Email
- Mailjet is used for transactional email.
- Used for OTP and card-related notifications.

### SMS
- Twilio is used for SMS notifications.

### File upload
- Cloudinary is used to store uploaded profile photos.

### Payments
- PayDunya is integrated for payment workflows.

### Tracking
- WebSocket is used for live agent location broadcast.

## 10. API Layout
Main endpoint groups:
- `/api/auth`
- `/api/users`
- `/api/clients`
- `/api/agents`
- `/api/admin`
- `/api/packs`
- `/api/garanties`
- `/api/pack-garanties`
- `/api/souscriptions`
- `/api/cartes`
- `/api/paiements`
- `/api/partenaires`
- `/api/conventions-partenaires`
- `/api/beneficiaires`
- `/api/otp`
- `/api/uploads`

## 11. Runtime Configuration
The application is configured through `application.yml` and environment variables.

Important settings:
- database host, port, name, username, password
- JWT secret
- Mailjet keys
- Twilio keys

For local development, `run.sh` loads `back_end/.env` automatically before starting Spring Boot.
- PayDunya keys
- Cloudinary keys

## 12. Run The Backend
```bash
cd back_end
mvn spring-boot:run
```

## 13. Useful Notes
- The backend uses DTOs to avoid exposing entities directly.
- Services contain business rules and validation.
- Controllers stay thin and delegate work to services.
- Security rules are centralized in one configuration class.
- WebSocket is used only for live agent tracking.
