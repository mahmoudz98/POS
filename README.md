# Point-of-Sale (POS)

## Overview

### Purpose of the POS App
The purpose of the Point-of-Sale (POS) app is to streamline sales transactions for businesses. It serves as a digital cash register, enabling businesses to accept payments from customers and efficiently manage inventory. The app is designed to enhance the overall sales process by providing a user-friendly interface for both customers and business owners.

### Technology Stack

| Feature                 | Technology              | Description                                                          |
|-------------------------|-------------------------|----------------------------------------------------------------------|
| Programming Language    | Kotlin                  | Modern and concise language for Android development.                 |
| Dependency Injection    | Dagger Hilt             | Provides a compile-time safe way to manage dependencies.             |
| Asynchronous Operations | Kotlin Coroutines       | Simplifies asynchronous programming and improves app responsiveness. |
| Local Storage           | Room Database           | Persistent local storage for offline data access.                    |
| Cloud Storage           | Firebase Firestore      | Real-time cloud database for data synchronization and storage.       |
| Authentication          | Firebase Authentication | Secure user authentication and account management.                   |
| Image Storage           | Firebase Storage        | Cloud storage for images and other media files.                      |
| Testing                 | JUnit, MockK, Espresso  | Comprehensive testing frameworks for unit and UI testing.            |

## Project Structure

The project follows a modular architecture with the following modules:

| Module   | Description                                                                         |
|----------|-------------------------------------------------------------------------------------|
| `app`    | Contains the main application logic, UI components, and activities.                 |
| `data`   | Responsible for data access, including repositories, data sources, and data models. |
| `domain` | Defines the business logic and use cases of the application.                        |
| `di`     | Handles dependency injection setup using Dagger Hilt.                               |

## Firebase Authentication

Firebase Authentication is used to create and manage user accounts securely. This is particularly important for business owners and employees who need secure access to the POS system.

## Firestore Integration

Firestore is utilized as the database to store and retrieve crucial POS data. The `data` module handles interactions with Firestore, including managing inventory and sales data.

## Room Database

The Room persistence library provides an abstraction layer over SQLite, enabling efficient and
convenient local data storage.

## Dependency Injection

Dependency injection is implemented using Dagger Hilt. The `di` module provides the necessary setup for dependency injection throughout the project, ensuring scalable and maintainable code.

## Asynchronous Operations

Kotlin Coroutines are employed for handling asynchronous operations, enhancing the responsiveness and efficiency of the POS app.

## Testing

The project utilizes a combination of testing frameworks, including JUnit, MockK, and Espresso, to
ensure the correctness and reliability of the POS app.

## Getting Started

To get started with the POS app, follow these steps:

1. Clone the repository.
2. Open the project in Android Studio.
3. Build and run the app on an emulator or a physical device.

### Download

You can download the latest APK from the [GitHub Actions](https://github.com/Case-Code/POS/actions)
page.
    
## Contributing

If you would like to contribute to the project, follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and submit a pull request.


