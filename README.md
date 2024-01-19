# Point-of-Sale (POS)

## Overview

### Purpose of the POS App
The purpose of the Point-of-Sale (POS) app is to streamline sales transactions for businesses. It serves as a digital cash register, enabling businesses to accept payments from customers and efficiently manage inventory. The app is designed to enhance the overall sales process by providing a user-friendly interface for both customers and business owners.

### Technology Stack
- Language: Kotlin
- Dependency Injection: Dagger Hilt
- Asynchronous Operations: Kotlin Coroutines
- Testing: hamcrest, MockK, Fake Data, Espresso, testing with hilt

## Project Structure

The project follows a modular architecture with the following modules:

- `app`: User interface and presentation logic.
- `data`: Data sources, repositories, and data models.
- `domain`: Business logic and use cases.
- `di`: Dependency Injection setup using Hilt.
- `testing`: Test setup and utilities.

## Firebase Authentication

### User Creation
Firebase Authentication is used to create and manage user accounts securely. This is particularly important for business owners and employees who need secure access to the POS system.

## Firestore Integration

### Data Storage
Firestore is utilized as the database to store and retrieve crucial POS data. The `data` module handles interactions with Firestore, including managing inventory and sales data.

## Dependency Injection

### Dagger Hilt
Dependency injection is implemented using Dagger Hilt. The `di` module provides the necessary setup for dependency injection throughout the project, ensuring scalable and maintainable code.

## Asynchronous Operations

### Kotlin Coroutines
Kotlin Coroutines are employed for handling asynchronous operations, enhancing the responsiveness and efficiency of the POS app.

## Testing

### Assertion Library
hamcrest is used as the assertion library for testing. It provides convenient and expressive assertions for validating the correctness of POS app functionalities.

### Mocking Framework
MockK is employed for mocking objects in tests, making it easier to isolate components for unit testing and ensuring the reliability of the POS app.

### Fake Data Technique
Fake data techniques are used in testing to simulate realistic scenarios without relying on actual data. This approach ensures controlled and reproducible test cases.

## Getting Started

To get started with the POS app, follow these steps:

1. Clone the repository.
2. Open the project in Android Studio.
3. Build and run the app on an emulator or a physical device.

### Download
    * [POS APK from githubAction](https://github.com/Case-Code/POS/actions) 

## Contributing

If you would like to contribute to the project, follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and submit a pull request.


