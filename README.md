# Back-end Spring Boot - MA Sante Assurance

## Stack
- Spring Boot 3
- Java 17+
- Spring Web + Validation
- Spring Data JPA
- PostgreSQL
- Spring WebSocket (temps reel tracking agents)

## Configuration base de donnees
Le backend lit la configuration PostgreSQL via variables d'environnement:

```bash
export DB_HOST=dpg-d6nc2ptactks738k1gr0-a.oregon-postgres.render.com
export DB_PORT=5432
export DB_NAME=ma_sante_assurance
export DB_USERNAME=ma_sante_assurance_user
export DB_PASSWORD='***'
```

## Lancer
```bash
cd back_end
mvn spring-boot:run
```

## Endpoints principaux
- Auth:
  - `POST /api/auth/register`
  - `POST /api/auth/login`
  - `GET /api/auth/me`
  - `POST /api/auth/logout`
- Users: `GET /api/users`, `POST /api/users`
- Clients: `GET /api/clients`, `GET /api/clients/{id}`, `POST /api/clients`, `PATCH /api/clients/{id}`
- Agents:
  - `GET /api/agents`
  - `POST /api/agents`
  - `GET /api/agents/live-locations`
  - `POST /api/agents/{agentId}/location`
- Packs: `GET /api/packs`, `POST /api/packs`
- Garanties: `GET /api/garanties?packId=...`, `POST /api/garanties`
- Souscriptions: `GET /api/souscriptions`, `POST /api/souscriptions`
- Paiements: `GET /api/paiements`, `POST /api/paiements`
- Partenaires: `GET /api/partenaires`, `POST /api/partenaires`
- Cartes: `GET /api/cartes/client/{clientId}`, `POST /api/cartes`

## WebSocket live
- URL: `ws://localhost:8080/ws/agent-locations`
- Payload: snapshot JSON de toutes les localisations agents.
