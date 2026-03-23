# 🏛️ sb/blip System Architecture

This document outlines the real, enforced architecture of the `sb/blip` application based on the codebase structure and import graphs. 

The system consists of a **Spring Boot Backend** and an **Angular 18+ Frontend**.

---

## 1. Real Folder Structure

### Backend (`src/main/java/com/ceres/blip/`)
- `annotations/` — Custom functional annotations (e.g. `@RequiresAuthentication`).
- `api/` — HTTP Controllers defining REST endpoints.
- `config/` — Deployment and framework configurations (Redis, Swagger, WebSockets, JWT).
- `dtos/` — Data Transfer Objects for all API requests and responses.
- `exceptions/` — Custom exceptions and the global `@RestControllerAdvice` handler.
- `models/` — 
  - `database/` — JPA Entity models representing tables.
  - `enums/` — Shared system enumerations.
- `repositories/` — Spring Data JPA interface definitions.
- `services/` — Core business logic, computations, and database orchestration.
- `utils/` — Shared backend utilities (e.g., `LocalUtilsService`, `LocalFileManager`).

### Frontend (`frontend/src/app/`)
- `components/` — Feature modules structured as **Standalone Components**.
  - `bus-booking/`, `login/`, `parcels/`, `user-management/`, `subscriptions/`, etc.
  - `services/` — Core Angular services responsible for HTTP calls (`auth.service.ts`, `remoteService.ts`) and global events (`events.ts`, `socket.service.ts`).
  - `common/` / `shared-components/` — Reusable UI fragments.
- `guards/` — Route protection logic (`auth.guard.ts`).
- `interceptors/` — Network request interceptors (token injection).
- `pipes/` — Custom angular template pipes (`truncate-pipe.ts`).

---

## 2. Layer Responsibilities & Dependency Flow

### A. Backend Control Flow
Code must flow strictly downwards. Sibling or upward calls bypass the architectural framework.

1. **`api/` (Controllers)**: Receives HTTP requests, validates DTO inputs, and marshals them to the Service layer. **Cannot** call repositories directly.
2. **`services/` (Business Logic)**: Houses the complex application logic, throws validation exceptions, and invokes Database calls.
3. **`repositories/` (Data Access)**: Executes queries against the underlying database.
4. **`models/database/` (Entities)**: Structurally maps the DB rows.

#### ASCII Dependency Graph (Backend)
```text
[ Client Request ]
       │
       ▼
 [ JwtFilter (config/) ] ──(Extracts Token)
       │
       ▼
 [ Controller (api/) ] ──(Transforms JSON -> DTO)
       │
       ▼
 [ Service (services/) ] ──(Business Logic / Caching)
       │
       ▼
 [ Repository (repositories/) ] ──(Spring Data JPA)
       │
       ▼
[ Database (PostgreSQL/MySQL) ]
```

### B. Frontend Control Flow
The frontend operates on a component-service separation, heavily utilizing class inheritance for UI traits.

1. **`components/` (UI Layer)**: Binds to HTML templates. Handles user inputs and delegates state/API requests.
2. **`components/services/` (State & API)**: Communicates with the Backend APIs via `remoteService.ts` and holds persistent state (like `auth.service.ts`).
3. **`interceptors/` (Network Layer)**: Attaches authentication Bearer tokens to outbound requests natively.

#### ASCII Dependency Graph (Frontend)
```text
[ User Interaction ]
       │
       ▼
 [ Standalone Component (.ts / .html) ]
       │
       ▼
 [ Angular Service (components/services/) ]
       │
       ▼
 [ Auth Interceptor (interceptors/) ] ──(Attaches JWT)
       │
       ▼
 [ Backend REST API ]
```

---

## 3. Shared Utilities & Common Modules

The architecture relies heavily on base subclasses to distribute common dependencies rapidly.

### Backend: `LocalUtilsService`
- **Location:** `src/main/java/com/ceres/blip/utils/LocalUtilsService.java`
- **What it provides:** Almost all business services extend this base class. It offers immediate access to the currently authenticated user session (`authenticatedUser()`), standard localized timestamp generators, and request validation utilities (checking if required fields exist on dynamic JSON payloads).

### Frontend: `BaseComponent`
- **Location:** `frontend/src/app/components/services/base-component.ts`
- **What it provides:** Extended by nearly all UI components (e.g., `class LoginComponent extends BaseComponent`). It pre-injects common heavy UI services so child components don't have bloated constructors. It provides standard wrapper methods for displaying toast notifications (`this.showError()`), toggling the loader (`this.loaderService.display()`), and confirming actions via dialog arrays.

### Frontend: `RemoteService`
- **Location:** `frontend/src/app/components/services/remoteService.ts`
- **What it provides:** A central HTTP abstraction layer. Domain-specific services (like `vehicle.service.ts` or `auth.service.ts`) use this to cleanly fire GET, POST, and PUT commands to the backend without rewriting endpoint bases, header setups, or common error catching.
