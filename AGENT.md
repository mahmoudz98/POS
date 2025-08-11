# POS (Point of Sale) Project

POS is a native Android mobile application written in Kotlin. It is an offline-first, multi-device Point of Sale system designed for small to medium-sized businesses.

## Architecture

This project is a modern Android application that follows the official architecture guidance from Google, with a strong emphasis on offline-first capabilities and robust data synchronization. It is a reactive, single-activity app that uses the following:

-   **UI:** Built entirely with Jetpack Compose, including Material 3 components and adaptive layouts.
-   **State Management:** Unidirectional Data Flow (UDF) is implemented using Kotlin Coroutines and `Flow`s. `ViewModel`s act as state holders, exposing UI state as a single `StateFlow` object.
-   **Dependency Injection:** Hilt is used for dependency injection throughout the app, simplifying the management of dependencies and improving testability.
-   **Navigation:** Navigation is handled by Jetpack Navigation for Compose, allowing for a declarative and type-safe way to navigate between screens.
-   **Data Layer (Offline-First):** The data layer is implemented using the repository pattern with a strict offline-first approach.
    -   **Local Data:** Room is the primary source of truth for all data read by the UI. `ProtoDataStore` is used for user session preferences.
    -   **Remote Data:** Firestore is used as the backend for data storage and synchronization.
-   **Synchronization:** The app uses a sophisticated, two-way synchronization pattern.
    -   **Outbox Pattern (`syncUp`):** Local changes are written to a local `OutboxCommand` queue and reliably pushed to the server in the background by a `WorkManager` worker.
    -   **Inbox Pattern (`syncDown`):** Changes from other devices are signaled via a Firebase Realtime Database "inbox". A listener queues these signals locally, and a `WorkManager` worker pulls the updated data from Firestore to reconcile the local database.
-   **Background Processing:** `WorkManager` is used for all deferrable background tasks, including data synchronization.

## Modules

The main Android app lives in the `app/` folder. Feature modules live in `feature/` and core/shared modules in `core/`.

-   **`app`**: The main application module, responsible for tying together the feature modules and handling top-level navigation.
-   **`feature/*`**: Self-contained feature modules (e.g., `feature:onboarding`, `feature:sale`, `feature:item`). They depend on `core` modules but never on each other.
-   **`core/*`**: Shared library modules providing foundational capabilities:
    -   `core:model`: Contains the pure Kotlin data classes for the entire project.
    -   `core:domain`: Contains repository interfaces and use cases, defining the core business logic.
    -   `core:data`: Contains repository implementations, orchestrating local and remote data sources.
    -   `core:database`: Manages the Room database, DAOs, and entities.
    -   `core:datastore`: Manages the `ProtoDataStore` for user preferences.
    -   `core:firebase`: Handles all network interactions with Firestore and Realtime Database.
    -   `core:designsystem`: Provides shared Jetpack Compose themes and components.
    -   `core:testing`: Contains fakes, test doubles, and utilities for unit and instrumented tests.
-   **`sync`**: Contains the `WorkManager` workers responsible for background data synchronization.
-   **`build-logic`**: Contains custom Gradle convention plugins for consistent module setup.

## Commands to Build & Test

The app and Android libraries have two product flavors: `demo` and `prod`, and two build types: `debug` and `release`.

-   **Build:** `./gradlew assembleProdDebug`
-   **Fix Formatting:** `./gradlew spotlessApply`
-   **Run Local Tests:** `./gradlew testProdDebugUnitTest`
-   **Run Single Test:** `./gradlew testProdDebugUnitTest --tests "com.casecode.pos.core.data.repository.business.BusinessRepositoryImplTest"`
-   **Run Instrumented Tests:** `./gradlew connectedProdDebugAndroidTest`

### Creating Tests

#### Local Tests (`test` source set)
-   Use the fake repositories and DAOs from the `:core:testing` module.
-   Instantiate real `UseCase` classes with the fake repositories.
-   Use `kotlinx-coroutines-test` and the `MainDispatcherRule` for coroutine testing.
-   Use `kotlin.test` (`assertEquals`, `assertTrue`, etc.) for assertions.

#### Instrumented Tests (`androidTest` source set)
-   Tests for UI features should use `ComposeTestRule`.
-   Use Hilt's testing support for injecting dependencies in E2E test.

## Continuous Integration

-   The workflows are defined in `.github/workflows/*.yaml`.
-   CI runs checks for linting, formatting, building, and executing all unit and instrumented tests.
