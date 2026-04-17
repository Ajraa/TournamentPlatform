# API Documentation — User Module

Base path: `/api/v1`

---

## Authentication

### POST /api/v1/auth/register

Registers a new user account. The `role` field is optional and defaults to `PLAYER`.

- **Auth:** Public
- **Status:** `201 Created` | `400 Bad Request`

#### Role: PLAYER (default)

Required fields: `email`, `nickname`, `password`. A solo user team is automatically created for the new player.

**Request body**

```json
{
  "email": "hrac@example.com",
  "password": "SuperTajneHeslo123",
  "nickname": "ProGamer42",
  "role": "PLAYER"
}
```

#### Role: FOUNDER

Required fields: `email`, `nickname`, `password`, `firstName`, `lastName`, `street`, `houseNumber`, `city`, `postcode`, `country`, `bankAccount`. No team is created automatically.

**Request body**

```json
{
  "email": "founder@example.com",
  "password": "SuperTajneHeslo123",
  "nickname": "TournamentOrg",
  "role": "FOUNDER",
  "firstName": "Jan",
  "lastName": "Novák",
  "street": "Náměstí Míru",
  "houseNumber": "1",
  "city": "Praha",
  "postcode": "12000",
  "country": "CZ",
  "bankAccount": "1234567890/0800"
}
```

#### Field reference

| Field         | Type   | Constraints                                      |
|---------------|--------|--------------------------------------------------|
| `email`       | string | Required, valid email format                     |
| `password`    | string | Required, min 8 characters                       |
| `nickname`    | string | Required                                         |
| `role`        | string | Optional, `PLAYER` (default) or `FOUNDER`        |
| `firstName`   | string | Required when `role = FOUNDER`                   |
| `lastName`    | string | Required when `role = FOUNDER`                   |
| `street`      | string | Required when `role = FOUNDER`                   |
| `houseNumber` | string | Required when `role = FOUNDER`                   |
| `city`        | string | Required when `role = FOUNDER`                   |
| `postcode`    | string | Required when `role = FOUNDER`                   |
| `country`     | string | Required when `role = FOUNDER`                   |
| `bankAccount` | string | Required when `role = FOUNDER`                   |

**Response body** (`201`)

```json
{
  "userId": 1,
  "message": "Registrace proběhla úspěšně."
}
```

**Error responses**

| Status | Condition                                                          |
|--------|--------------------------------------------------------------------|
| `400`  | Email already in use, validation failed, or founder fields missing |

---

### POST /api/v1/auth/login

Authenticates a user. JWT token is set as an HttpOnly cookie, response body contains the user profile.

- **Auth:** Public
- **Status:** `200 OK` | `401 Unauthorized`

**Request body**

```json
{
  "nickname": "ProGamer42",
  "password": "SuperTajneHeslo123"
}
```

**Response**

Sets cookie: `jwt` (HttpOnly, Secure, SameSite=Strict, maxAge=86400s).

**Response body** (`200`) — `UserDto`; sensitive fields (`email`, address, `bankNumber`) are `null` in login response.

```json
{
  "userId": 1,
  "nickname": "ProGamer42",
  "email": null,
  "firstName": null,
  "lastName": null,
  "rating": 1500,
  "winrate": 0.65,
  "roles": ["PLAYER"],
  "street": null,
  "houseNumber": null,
  "city": null,
  "postcode": null,
  "country": null,
  "bankNumber": null
}
```

**Error responses**

| Status | Condition           |
|--------|---------------------|
| `401`  | Invalid credentials |

---

## Users

### GET /api/v1/users/me

Returns the full profile of the currently authenticated user.

- **Auth:** JWT cookie required (`jwt`)
- **Status:** `200 OK` | `401 Unauthorized`

**Request:** No body. User identity is read from the JWT cookie.

**Response body** (`200`)

```json
{
  "userId": 1,
  "nickname": "ProGamer42",
  "email": "hrac@example.com",
  "firstName": null,
  "lastName": null,
  "rating": 1500,
  "winrate": 0.65,
  "roles": ["PLAYER"],
  "street": null,
  "houseNumber": null,
  "city": null,
  "postcode": null,
  "country": null,
  "bankNumber": null
}
```

> Note: For users with role `FOUNDER`, all personal and address fields are filled in.

---

### PUT /api/v1/users/me

Updates personal, address and bank details of the currently authenticated user.

- **Auth:** JWT cookie required (`jwt`)
- **Status:** `200 OK` | `400 Bad Request` | `401 Unauthorized`

> Note: For users with role `FOUNDER`, all fields are required (enforced at service layer).

**Request body**

```json
{
  "firstName": "Jan",
  "lastName": "Novák",
  "street": "Náměstí Míru",
  "houseNumber": "1",
  "city": "Praha",
  "postcode": "12000",
  "country": "CZ",
  "bankNumber": "1234567890/0800"
}
```

#### Field reference

| Field         | Type   | Constraints                              |
|---------------|--------|------------------------------------------|
| `firstName`   | string | Optional (required for FOUNDER)          |
| `lastName`    | string | Optional (required for FOUNDER)          |
| `street`      | string | Optional (required for FOUNDER)          |
| `houseNumber` | string | Optional (required for FOUNDER)          |
| `city`        | string | Optional (required for FOUNDER)          |
| `postcode`    | string | Optional (required for FOUNDER)          |
| `country`     | string | Optional (required for FOUNDER)          |
| `bankNumber`  | string | Optional (required for FOUNDER)          |

**Response body** (`200`) — updated `UserDto`, same structure as `GET /users/me`.

---

### PUT /api/v1/users/me/password

Changes the password of the currently authenticated user.

- **Auth:** JWT cookie required (`jwt`)
- **Status:** `204 No Content` | `400 Bad Request` | `401 Unauthorized`

**Request body**

```json
{
  "oldPassword": "StaréHeslo123",
  "newPassword": "NovéHeslo456"
}
```

#### Field reference

| Field         | Type   | Constraints        |
|---------------|--------|--------------------|
| `oldPassword` | string | Required, min 8 characters |
| `newPassword` | string | Required, min 8 characters |

**Error responses**

| Status | Condition                              |
|--------|----------------------------------------|
| `400`  | Old password incorrect, or validation failed |

---

## DTOs

| DTO                   | Used in                              | Fields                                                                                                                                                        |
|-----------------------|--------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `UserRegistrationDto` | `POST /auth/register` request        | `email`, `password`, `nickname`, `role`; + `firstName`, `lastName`, `street`, `houseNumber`, `city`, `postcode`, `country`, `bankAccount` for FOUNDER         |
| `LoginDto`            | `POST /auth/login` request           | `nickname`, `password`                                                                                                                                        |
| `AuthResponseDto`     | `POST /auth/register` response       | `userId`, `message`                                                                                                                                           |
| `UserDto`             | `POST /auth/login` response, `GET/PUT /users/me` response | `userId`, `nickname`, `email`, `firstName`, `lastName`, `rating`, `winrate`, `roles`, `street`, `houseNumber`, `city`, `postcode`, `country`, `bankNumber` |
| `UpdateUserDto`       | `PUT /users/me` request              | `firstName`, `lastName`, `street`, `houseNumber`, `city`, `postcode`, `country`, `bankNumber`                                                                 |
| `ChangePasswordDto`   | `PUT /users/me/password` request     | `oldPassword`, `newPassword`                                                                                                                                  |
