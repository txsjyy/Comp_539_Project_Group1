# SnapLink

SnapLink is a full-stack URL management platform built with Spring Boot and Vue. It supports short-link creation, custom aliases, expiration rules, user account flows, password reset email templates, and click analytics backed by Google Cloud Bigtable.

This repository is currently being cleaned up and rebranded from its original course-project name into a portfolio-grade backend/full-stack case study.

## Why This Project Matters

SnapLink is useful as a backend-focused portfolio project because it contains real product concerns beyond a simple URL shortener:

- user registration and login
- password hashing and reset-token flow
- short-link creation and redirection
- custom alias collision handling
- link expiration and active/inactive states
- one-time link metadata
- click analytics with request metadata
- Google Cloud Bigtable persistence
- separate Vue frontend and Spring Boot backend

## Architecture

```text
Frontend_Vue/                    Vue 3 + Vite + Pinia client
  src/views                      Login, signup, link management, statistics
  src/stores                     User and URL state

src/main/java/com/snaplink
  controller/AuthController      signup, login, password reset, delete user
  controller/UrlShortenerController
                                  shorten, redirect, search, analytics, update code
  service/UrlShortenerService    short-code generation, lifecycle logic, click capture
  service/EmailService           Postmark template emails
  repository/BigtableRepository  Bigtable reads/writes for users, URLs, analytics
  model/                         request, user, short-url, DTO models
```

## Data Model

Current Bigtable tables and row patterns:

- `user_profiles`
  - row key: `user#{id}`
  - families: `personal`, `security`, `subscription`, `metadata`
  - stores username, email, password hash, subscription plan, reset token metadata
- `url_tracking`
  - row key: `url#{shortCode}`
  - families: `url_info`, `user_info`
  - stores long URL, creation/expiration dates, one-time flag, active flag, user id
- `url_analytics`
  - row key: `click#{shortCode}#{timestamp}`
  - family: `click_info`
  - stores IP address, referrer, user agent, geo placeholder, click count marker

## API Surface

Authentication:

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/auth/signup` | Register a new user |
| `POST` | `/api/auth/login` | Log in with email and password |
| `POST` | `/api/auth/forgot-password` | Issue a password reset token and send email |
| `POST` | `/api/auth/reset-password` | Reset password using a token |
| `DELETE` | `/api/auth/delete-user?email=...` | Delete a user |

URL management:

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/shorten` | Create an auto-generated or custom short URL |
| `GET` | `/{shortCode}` | Redirect to the long URL and record click metadata |
| `GET` | `/search?query=...` | Search links and return click counts |
| `DELETE` | `/{shortCode}` | Delete a short URL |
| `POST` | `/analytics/details` | Return click-level analytics for a short code |
| `PUT` | `/update-shortcode` | Replace an existing short code |

## Tech Stack

Backend:

- Java 17
- Spring Boot 3.1
- Spring Web
- Spring Security
- Google Cloud Bigtable
- Postmark email templates
- Maven

Frontend:

- Vue 3
- Vite
- TypeScript
- Pinia
- Vue Router
- Axios
- Chart.js

## Local Setup

### Backend

Prerequisites:

- Java 17
- Maven
- Google Cloud credentials with access to a Bigtable instance

Create local environment variables:

```bash
cp .env.example .env
```

Then export the variables in your shell or configure them in your IDE. At minimum, the backend needs:

```bash
export GCP_PROJECT_ID=your-gcp-project-id
export GCP_BIGTABLE_INSTANCE_ID=your-bigtable-instance-id
export POSTMARK_SERVER_TOKEN=your-postmark-server-token
```

Run the backend:

```bash
mvn spring-boot:run
```

The backend defaults to:

```text
http://localhost:8080
```

### Frontend

```bash
cd Frontend_Vue
cp .env.example .env
npm install
npm run dev
```

The frontend defaults to:

```text
http://localhost:5173
```

## Validation

Backend:

```bash
mvn test
```

Frontend:

```bash
cd Frontend_Vue
npm run type-check
npm run build
```

## Known Rebuild Work

This project is intentionally marked as `In Rebuild` in the portfolio. The next cleanup pass should focus on:

- replacing wildcard CORS with configured frontend origins
- removing hardcoded URL rules and demo-only special cases
- adding local persistence or emulator setup for reviewers without GCP access
- improving short-code generation collision handling
- making one-time links deactivate after first successful redirect
- adding focused tests for alias collision, expiration, redirect, and analytics
- documenting screenshots and a short demo path
- separating production secrets from repository history

## Portfolio Positioning

SnapLink should be presented as:

> A full-stack URL management platform with Spring Boot, Vue, authentication, custom aliases, expiration rules, and click analytics.

It should not be presented as a large-scale production service unless deployment and measured usage are added later.

