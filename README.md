# JWTAUTH

A secure authentication backend built with Spring Boot, supporting 2FA, OAuth2, and a two-token auth system. Designed to evolve into a microservice architecture.

---

## 🔧 Tech Stack

- **Java 23**
- **Spring Boot 3.4.3**
- **PostgreSQL**
- **Spring Security**
- **JWT (Access + Refresh)**
- **OAuth2 (Google)**

---

## 🔐 Security Overview

**Two-token system** with optional **2FA** (code-based) tied to a device fingerprint.

### Tokens

- **Access Token** – short-lived, for protected endpoints.
- **Refresh Token** – longer-lived, used to reissue access tokens.

### Token Handling

- Stored in **HttpOnly** cookies.
- Cookies use **SameSite=Strict** to reduce CSRF risk.
- **Secure flag** should be enabled in production (HTTPS).
- Refresh tokens are currently **stateful** (changeable in future).

---

## 🔁 2FA Flow (Email-based and also very crappy and vulnerable)

1. Login/Register triggers code verification if device is unrecognized.
2. Code is sent to email.
3. Client verifies with `/code` endpoint.
4. Upon success, full tokens are issued.

---

## 📡 API Endpoints

- `POST /register` – Register new users.
- `POST /login` – Log in with credentials.
- `POST /code` – Submit 2FA code.
- `GET /refresh` – Issue new access token.
- `POST /reset` – Request password reset (sends code).
- `POST /newpassword` – Set new password using reset code.
- `POST /oauth` – Google OAuth2 login.

---

## ⚠️ Notes

- 2FA required on unknown devices.
- No frontend; assumes client handles storing cookies.
- Email must be configured for code delivery.
- Designed to become a reusable, modular **Auth microservice**.

---

## 🪪 License

MIT – use, fork, break, learn.
