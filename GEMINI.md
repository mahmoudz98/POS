# Gemini Configuration: POS Application

This document provides a comprehensive architectural overview and strategic plan for the Point of Sale (POS) Android application. It serves as the primary context guide for Gemini to ensure all contributions are aligned with the project's standards and vision.

## 1. Project Vision & Business Goals

- **Core Mission:** To build a robust, offline-first Android POS application tailored for small to medium-sized businesses (e.g., cafes, retail shops).
- **Key Competitive Advantage:** Uninterrupted functionality even with intermittent or no network connectivity. This is a primary selling point.
- **Strategic Imperative:** Achieve a Minimum Viable Product (MVP) by completing the foundational epics (Onboarding, User Access) to enable beta testing and attract early customers.

## 2. Core Architectural Principles

### 2.1. Modularization Strategy (Now in Android Style)

The project is strictly divided into three types of modules to ensure separation of concerns, scalability, and faster build times.

- **`:app` Module:** The main application module. It is responsible for assembling the final application, including dependency injection setup and navigating between feature modules. It should contain minimal business logic.
- **`:feature` Modules:** Self-contained, vertical slices of functionality (e.g., `:feature:employee`, `:feature:login`).
  - **Rule:** Feature modules must *never* depend on each other directly.
  - **Rule:** Feature modules depend on `:core` modules for shared functionality.
- **`:core` Modules:** Shared library modules providing foundational capabilities.
  - `core:model`: Pure Kotlin data classes representing the domain (e.g., `Employee`, `Business`).
  - `core:domain`: Use cases and repository interfaces. This layer is pure Kotlin with no Android dependencies.
  - `core:data`: Implementations of repositories, orchestrating local and network data sources.
  - `core:database`: Room DB, DAOs, and `@Entity` classes.
  - `core:firebase`: Network data sources using Firestore and Realtime Database.
  - `core:datastore`: `PosPreferencesDataSource` for session management using Jetpack DataStore.
  - `core:designsystem`: Shared Composables, themes, and icons.
  - `core:testing`: Fake repositories, test data, and utilities for unit and instrumentation tests.

### 2.2. Clean Architecture & MVI

The project follows a strict Clean Architecture with a Model-View-Intent (MVI) pattern in the UI layer.

- **UI Layer (Compose):**
  - **Pattern:** MVI.
  - **ViewModel:** One `ViewModel` per feature screen. It is the single source of truth for the screen's state.
  - **State:** The ViewModel exposes a single `StateFlow<UiState>`. The `UiState` is an immutable `data class` representing the entire state of the screen. For complex screens, the `UiState` is composed of smaller, SRP-compliant state objects (e.g., `EmployeeListState`, `AddEmployeeState`).
  - **Events:** User interactions are modeled as a `sealed interface` of `Events`, which are passed to the ViewModel's `onEvent` function.
  - **Composables:** UI Composables should be as stateless as possible, receiving all data from the `UiState` and dispatching all actions via the `onEvent` lambda.

- **Domain Layer:**
  - Contains use cases for complex business logic (e.g., `CreateEmployeeUseCase`).
  - Contains repository interfaces defining the data contract.
  - **Rule:** This layer must remain pure Kotlin.

- **Data Layer:**
  - Implements the repository interfaces from the domain layer.
  - Acts as the single source of truth, orchestrating data from local (`DAOs`) and remote (`NetworkDataSources`) sources.
  - **Rule:** All implementation details, such as password hashing, are handled here.

### 2.3. Synchronization Strategy ("Signal-First" Offline-First)

This is the project's killer feature. It provides near-real-time updates while being fully resilient to network loss.

- **`syncUp` (Outbox Pattern):**
  1. A local change (e.g., creating an employee) creates an `OutboxCommandEntity` in the local database.
  2. A generic `OutboxSyncWorker` is triggered.
  3. The worker calls `syncUp()` on all `Syncable` repositories (provided via Hilt `@Binds @IntoSet`).
  4. The repository pushes the change to Firestore.
  5. Upon success, it posts a signal to a remote Realtime Database "inbox" for other devices.

- **`syncDown` (Inbox Pattern):**
  1. A long-running `InboxSyncWorker` listens to the remote RTDB inbox.
  2. When a signal arrives, it checks a local `ProcessedSignalDao` to prevent duplicates.
  3. If the signal is new, it creates a `LocalSignalEntity` (a local "to-do" item).
  4. It then triggers the `OutboxSyncWorker`.
  5. The `OutboxSyncWorker` calls `syncDown()` on all `Syncable` repositories.
  6. Each repository processes its relevant items from the `LocalSignalDao` queue, fetches the full data from Firestore, and updates its local tables.

- **Full Sync Fallback (Architectural Goal):** A periodic (e.g., daily) full sync job should be implemented as a self-healing mechanism to ensure eventual consistency if signals are missed.

### 2.4. First-Time Sync ("Prefetch")

- **User Experience:** A user's first login on a new device must be smooth. The app must be populated with all necessary business data before the main UI is usable.
- **Flow:**
  1. Login use cases (`SignInOwnerUseCase`, `SignInEmployeeUseCase`) check if foundational local data exists.
  2. If not, they return a `LoginResult.PrefetchRequired` state.
  3. The UI shows a blocking "Initializing your device..." screen.
  4. A `OneTimeWorkRequest` for the `OutboxSyncWorker` (with a `syncDown`-only flag) is enqueued.
  5. Upon success, the user session is started, and the user is navigated to the main app.

## 3. Current Strategic Plan: Epic 2 - User Access & Session Management

**Goal:** To build a robust, secure, and flexible system for both owners and employees to log into an existing, configured business.

### Phase 1: Solidify the Foundation (Engineer & Architect)

1.  **Implement `GetCurrentSessionUseCase`:**
    - **Why:** To provide a clean, domain-layer abstraction for accessing session data (`businessId`, `branchId`, etc.), which is needed by many other use cases.
    - **Action:** Create the use case and refactor existing code to use it.
2.  **Refactor `EmployeeViewModelTest.kt`:**
    - **Why:** To ensure our tests are as robust as our production code.
    - **Action:** Rewrite the test suite to strictly follow the "Given-When-Then" pattern and cover all edge cases.

### Phase 2: Complete Employee Creation UI (US-2.1 - Engineer & Business)

1.  **Build the Stateless `AddEmployeeDialog`:**
    - **Why:** To provide a clean, reusable UI component for the form, following MVI best practices.
    - **Action:** Create the `AddEmployeeDialog.kt` composable that is fully stateless, receiving `AddEmployeeState` and `onEvent` as parameters.
2.  **Integrate into `EmployeesScreen`:**
    - **Why:** To connect the UI to the ViewModel.
    - **Action:** Refactor `EmployeesScreen.kt` to use the new dialog, driven by the ViewModel's state.
3.  **UI Testing:**
    - **Why:** To ensure the UI is correct and robust.
    - **Action:** Create `EmployeeScreenTest.kt` and test the dialog visibility and form interactions, using a fake ViewModel.

### Phase 3: Implement Employee Login (US-2.3 - All Personas)

1.  **Test-Drive `EmployeeRepository.authenticateEmployee`:**
    - **Why:** This is the core of the employee login flow.
    - **Action:** Use TDD to implement the Firestore query and password verification.
2.  **Test-Drive `SignInEmployeeUseCase`:**
    - **Why:** To encapsulate the business logic of employee login.
    - **Action:** Use TDD to implement the use case, including success, failure, and "user not found" scenarios.

### Phase 4: Implement Prefetch & Session Flows (US-2.2, 2.4, 2.6 - Architect & Business)

1.  **Test-Drive Prefetch Logic:**
    - **Why:** To ensure a smooth first-time user experience.
    - **Action:** Add tests to `SignInOwnerUseCaseTest` and `SignInEmployeeUseCaseTest` to verify the `PrefetchRequired` state is returned. Implement the logic in the use cases.
2.  **Build the "Initializing" UI:**
    - **Why:** To provide clear feedback to the user during prefetch.
    - **Action:** Create a new screen for this state.
3.  **Test-Drive Branch Selection Logic:**
    - **Why:** To support businesses with multiple locations.
    - **Action:** Add a new `BranchSelectionRequired` state to `LoginResult`. Test-drive the logic in the login use cases to return this state when a user has multiple branches. Build the branch selection UI.