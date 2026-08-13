# YAPM
Yet another Password Manager
# Android Password Manager App

## 📌 Overview
The **Android Password Manager** is a secure mobile application designed to simplify password creation and management. Built using **Java** and backed by a **SQLite** database, the application serves as a safe vault for storing user login credentials while offering on-demand generation of strong, randomized passwords.

The primary objective of this project is to apply core **Object-Oriented Programming (OOP)** principles—such as Encapsulation, Abstraction, Inheritance, and Polymorphism—to deliver a clean, modular, and maintainable software architecture.

---

## 🚀 Key Features

* **Master Authentication:** Secure account creation and login mechanisms to protect user vaults.
* **On-Demand Password Generator:** Built-in tool that produces high-entropy random passwords based on custom rules (length, uppercase, lowercase, numbers, and symbols).
* **Credential Vault:** Safe storage for service names, usernames, and associated encrypted passwords.
* **Instant Retrieval & One-Tap Copy:** Fast access to retrieved passwords with clipboard support for a seamless user experience.
* **Search & Filter:** Capability to search through stored accounts by service or username.

---

## 🏗️ Object-Oriented Programming (OOP) Implementation

* **Encapsulation:** Sensitive data fields (such as stored passwords, master keys, and user details) are restricted to private scope within model classes, exposed only through validated accessor and mutator methods.
* **Abstraction:** Core components—including database connections, cryptographic functions, and password generation strategies—are declared via interfaces and abstract classes to decouple implementation details from the user interface.
* **Inheritance:** Shared properties and authentication behaviors are structured using parent classes to promote code reusability across different application modules.
* **Polymorphism:** Flexible design interfaces allow different password generation strategies and storage drivers to be swapped or extended without breaking existing business logic.

---

## 🛠️ Tech Stack

* **Language:** Java
* **Platform:** Android SDK
* **Database:** SQLite
* **UML Diagram:** Draw.io
* **Build Tool:** Gradle

---

## 🗄️ Database Architecture

The backend relational database consists of two core entities:

1. **Users Entity:** Hashed master passwords used for authentication.
2. **Credentials Entity:** Maps encrypted passwords and account names.

## 💻 Contributors

* **Aditya Vikram** 20251501190
* **Varun Prasad**  20251501189
