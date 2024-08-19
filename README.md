# spring-boot-react-boilerplate
This boilerplate project demonstrates how to render ReactJS components in a Spring Boot web application using Thymeleaf.

## Overview
This project demonstrates how to integrate ReactJS with a Spring Boot web application using Thymeleaf. The core concept is to copy the static build output from the ReactJS project into the static resources folder of the Spring Boot application. React components are rendered by attaching multiple root elements to corresponding HTML DOM nodes, each identified by an `section-*` prefix.

## Technologies
* Backend
  * Spring Boot
  * Thymeleaf Template Engine
* Frontend
  * React
  * TypeScript

## Requirements
* JDK 17+
* Node 20+

## Project Structure
* react-common
  * A common React project that copies its build output to both `spring-boot-java-react` and `spring-boot-kotlin-react`.
* spring-boot-java-react
  * A Java-based Spring Boot project that uses the build output from `react-common`.
* spring-boot-kotlin-react
  * A Kotlin-based Spring Boot project that uses the build output from `react-common`.

## Run Instruction
### Overview
You can run either the Java or Kotlin project, depending on your preference.

### Frontend
```bash
npm install -g yarn # If yarn is not installed

cd react-common
yarn install
yarn build
```

### Backend (Java)
* Default server port: 8080

```bash
cd spring-boot-java-react
./gradlew bootRun
```

### Backend (Kotlin)
* Default server port: 8081

```bash
cd spring-boot-kotlin-react
./gradlew bootRun
```

## Related Projects
* https://github.com/AkiaCode/python-react-boilerplate