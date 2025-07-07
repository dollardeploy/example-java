# Java Spring Boot Web App Boilerplate

This is a Java Spring Boot web application boilerplate. It includes a Gradle build system for common development and build tasks.

## Features

* Starts HTTP server serving static files and React components.
* Listens on port 8080 by default, configurable via the `server.port` property.
* Gradle build system for easy building, running, and development.
* Supports hot reloading for development.

## Prerequisites

* Java JDK 17 or later
* Gradle (included via wrapper)

## Getting Started

1. **Clone the repository:**  

```bash
git clone https://github.com/dollardeploy/example-java/
cd example-java
```

2. **Build the jar:**  

```bash
./gradlew build
```

## Usage

1. **Run the jar:**  

```bash
java -jar -Dserver.port=8080 build/libs/example-java.jar
```

### Development

To run the application in development mode:

```bash
./gradlew bootRun
```

The server will start on port 8080. You can configure the port by setting the `server.port` property in `application.properties` or via environment variable.

### Building

The Gradle build system provides several tasks for building and running the application:

* **Build the application:**  

```bash
./gradlew bootJar
```

This creates a JAR file in the `build/libs/` directory.

* **Run tests:**  

```bash
./gradlew test
```

* **Clean build artifacts:**  

```bash
./gradlew clean
```

### Running the Application

* **Run using Gradle (Recommended for development):**  

```bash
./gradlew bootRun
```

This command compiles and runs the application directly.

* **Run the built JAR:**  

After building (`./gradlew build`), you can run the JAR file:
```bash
java -jar build/libs/example-java.jar
```
  
To specify a different port:
```bash
java -jar -Dserver.port=9000 build/libs/example-java.jar
```

## Project Structure

```
.
├── build.gradle              # Gradle build configuration
├── gradle/                   # Gradle wrapper files
├── gradlew                   # Gradle wrapper script
├── gradlew.bat              # Gradle wrapper script (Windows)
├── src/
│   ├── main/
│   │   ├── java/            # Java source code
│   │   │   └── com/dollardeploy/web/
│   │   │       ├── Application.java
│   │   │       ├── config/  # Configuration classes
│   │   │       └── controller/  # Spring controllers
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/      # Static resources
│   │       └── templates/   # Thymeleaf templates
│   └── test/                # Test files
└── README.md               # This file
```

## About

Boilerplate for a Java Spring Boot webapp which is easy to deploy to any cloud provider with DollarDeploy.
