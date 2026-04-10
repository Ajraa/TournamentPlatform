# API Documentation — User Module

Base path: `/api/v1`

---

## Authentication

### POST /api/v1/auth/register

Registers a new user account.

- **Auth:** Public
- **Status:** `201 Created` | `400 Bad Request`

**Request body**

```json
{
  "email": "hrac@example.com",
  "password": "SuperTajneHeslo123",
  "username": "ProGamer42"
}
```

| Field      | Type   | Constraints                        |
|------------|--------|------------------------------------|
| `email`    | string | Required, valid email format       |
| `password` | string | Required, min 8 characters         |
| `username` | string | Required                           |

**Response body** (`201`)

```json
{
  "id": 1,
  "email": "hrac@example.com",
  "username": "ProGamer42",
  "role": "USER"
}
```

**Error responses**

| Status | Condition              |
|--------|------------------------|
| `400`  | Email already in use or validation failed |

---

### POST /api/v1/auth/login

Authenticates a user and issues a JWT token.

- **Auth:** Public
- **Status:** `200 OK` | `401 Unauthorized`

**Request body**

```json
{
  "email": "hrac@example.com",
  "password": "SuperTajneHeslo123"
}
```

**Response body** (`200`)

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Error responses**

| Status | Condition           |
|--------|---------------------|
| `401`  | Invalid credentials |

---

## Users

### GET /api/v1/users/me

Returns the profile of the currently authenticated user.

- **Auth:** Bearer token required
- **Status:** `200 OK`

**Request:** No body. User identity is read from the JWT.

**Response body** (`200`)

```json
{
  "id": 1,
  "email": "hrac@example.com",
  "username": "ProGamer42",
  "registeredAt": "2026-04-07T18:00:00Z"
}
```

---

### PUT /api/v1/users/me

Updates the profile of the currently authenticated user.

- **Auth:** Bearer token required
- **Status:** `200 OK`

> Note: Password and email changes are out of scope for MVP and require a separate confirmation flow.

**Request body**

```json
{
  "username": "ProGamer42_V2"
}
```

| Field      | Type   | Constraints |
|------------|--------|-------------|
| `username` | string | Required    |

**Response body** (`200`) — updated profile

```json
{
  "id": 1,
  "email": "hrac@example.com",
  "username": "ProGamer42_V2",
  "registeredAt": "2026-04-07T18:00:00Z"
}
```

---

### GET /api/v1/users/{userId}

Returns the public profile of any user. Email and other sensitive fields are intentionally excluded.

- **Auth:** Public
- **Status:** `200 OK` | `404 Not Found`

**Path parameters**

| Parameter | Type    | Description       |
|-----------|---------|-------------------|
| `userId`  | integer | ID of the user    |

**Response body** (`200`)

```json
{
  "id": 5,
  "username": "CiziHrac",
  "joinedAt": "2025-01-01"
}
```

**Error responses**

| Status | Condition           |
|--------|---------------------|
| `404`  | User does not exist |

---

## DTOs

| DTO                   | Used in                        | Fields                                              |
|-----------------------|--------------------------------|-----------------------------------------------------|
| `UserRegistrationDto` | `POST /auth/register` request  | `email`, `password`, `username`                     |
| `LoginRequestDto`     | `POST /auth/login` request     | `email`, `password`                                 |
| `AuthResponseDto`     | `POST /auth/login` response    | `token`                                             |
| `UserResponseDto`     | `POST /auth/register` response | `id`, `email`, `username`, `role`                   |
| `UserProfileDto`      | `GET/PUT /users/me` response   | `id`, `email`, `username`, `registeredAt`           |
| `UpdateProfileDto`    | `PUT /users/me` request        | `username`                                          |
| `PublicUserDto`       | `GET /users/{userId}` response | `id`, `username`, `joinedAt`                        |
