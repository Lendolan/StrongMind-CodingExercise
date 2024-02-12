# Pizza Manager Application
This project is a full-stack web application for managing pizza orders. The backend is built with Spring Boot, and the frontend is developed using React.

## Prerequisites
Before you begin, ensure you have installed the following on your local machine:

Java JDK 17  
Maven   
Node.js and npm  
You can check the installations by running the following commands in your terminal  
```
java -version
mvn -v
node -v
npm -v
```
## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
### Cloning the Repository
Clone the repository to your local machine:
```
git clone https://github.com/Lendolan/StrongMind-CodingExercise.git
cd springboot-pizza-manager
```
### Backend Setup
#### 1. Build the application:
Navigate to the root directory of the backend project and run:
```
./mvnw clean install
```
or if you are using Windows:
```
mvnw.cmd clean install
```
This will compile the Java code, run tests, and package the application.
#### 2. Run the Spring Boot application:
```
./mvnw spring-boot:run
```
or if you are using Windows:
```
mvnw.cmd spring-boot:run
```
The backend server should start on http://localhost:8080.
### Frontend Setup
#### 1. Navigate to the frontend directory:
```
cd front-end
```
#### 2. Install dependencies:
```
npm install
```
#### 3. Run the React application:
```
npm start
```
The frontend server should start and open http://localhost:3000 in your default web browser.
## Running Tests
To run the tests, navigate to the root directory of the backend project and execute:
```
./mvnw test
```
or on Windows
```
mvnw.cmd test
```
## Built With
- Spring Boot - The backend framework used  
- Maven - Dependency Management for the backend  
- React - The frontend library used  
- Node.js and npm - Frontend dependency management and scripts  
