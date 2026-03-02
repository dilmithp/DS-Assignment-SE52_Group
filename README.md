# AI-Enabled Smart Healthcare Platform

## Technologies
- Java 21, Spring Boot 3.3.0, Spring Cloud 2023.0.1
- PostgreSQL, Kafka, Keycloak, Consul

## How to run infrastructure
```bash
docker-compose up -d
```

## Keycloak Setup Instructions
After starting the infrastructure with `docker-compose up -d`, follow these steps to configure Keycloak:

1. **Access Keycloak Admin Console**:
   - Go to `http://localhost:8180/`
   - Click "Admin Console"
   - Login with `admin` / `admin`

2. **Create Realm**:
   - In the top-left dropdown (where it says "master"), click "Create Realm"
   - Realm name: `healthcare`
   - Click "Create"

3. **Create Realm Roles**:
   - In the left menu, go to `Realm roles`
   - Click "Create role"
   - Create the following roles one by one:
     - `PATIENT`
     - `DOCTOR`
     - `ADMIN`

4. **Create Client**:
   - In the left menu, go to `Clients`
   - Click "Create client"
   - Client type: `OpenID Connect`
   - Client ID: `healthcare-client`
   - Click "Next"
   - Client authentication: Toggle ON (this makes it a confidential client)
   - Authorization: Toggle OFF (unless fine-grained authorization is needed)
   - Standard flow: Checked
   - Direct access grants: Checked
   - Click "Next"
   - Valid redirect URIs: `*` (for dev purposes, restrict in production)
   - Web origins: `*`
   - Click "Save"

5. **Create Test Users**:
   - In the left menu, go to `Users`
   - Click "Add user"
   - Create `patient1`:
     - Username: `patient1`
     - Email: `patient1@example.com`
     - First name: `Patient`
     - Last name: `One`
     - Email verified: Yes
     - Click "Create"
     - Go to "Credentials" tab -> "Set password", enter a password, toggle "Temporary" to OFF, click "Save"
     - Go to "Role mapping" tab -> "Assign role", select `PATIENT`
   - Repeat the process to create `doctor1` (Assign role `DOCTOR`)
   - Repeat the process to create `admin1` (Assign role `ADMIN`)
