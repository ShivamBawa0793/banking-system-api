🏦 Banking System Backend API
This repository hosts a robust Banking System Backend API developed using Spring Boot and Maven. It provides core banking functionalities, containerized with Docker, and features an automated CI/CD pipeline for deployment to AWS EC2 via GitHub Actions.

✨ Features
Account Management:

Create new bank accounts.

Retrieve details for a specific account.

List all accounts in the system.

Transaction Management:

Deposit funds into an account.

Transfer funds between accounts with transactional integrity.

RESTful API: All functionalities are exposed via a clean and intuitive RESTful API.

🚀 Technologies Used
Backend:

Java 17

Spring Boot 3.x

Maven (Build Tool)

Spring JDBC (for database interaction)

Spring Transaction Management (@Transactional)

Database:

H2 Database (in-memory for development/testing, configurable for file-based persistence)

Containerization:

Docker

Deployment:

AWS EC2

CI/CD:

GitHub Actions (for automated build, Docker image creation, push, and deployment)

📦 Project Structure
The project follows a standard Spring Boot layered architecture:

BankingSystemProject/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── bankingsystem/
│   │   │               ├── BankingSystemApplication.java # Spring Boot Entry Point
│   │   │               ├── account/                     # Domain Model
│   │   │               │   └── Account.java
│   │   │               ├── controller/                  # REST API Endpoints
│   │   │               │   └── BankingController.java
│   │   │               ├── dto/                         # Data Transfer Objects (Request/Response)
│   │   │               │   ├── ApiResponse.java
│   │   │               │   ├── BalanceResponse.java
│   │   │               │   ├── DepositRequest.java
│   │   │               │   ├── AccountDetailsResponse.java
│   │   │               │   ├── TopSpenderDetails.java
│   │   │               │   └── TransferRequest.java
│   │   │               ├── repository/                  # Data Access Layer Interface
│   │   │               │   └── AccountRepository.java
│   │   │               └── repository/impl/             # Data Access Layer Implementation
│   │   │                   └── H2AccountRepositoryImpl.java
│   │   │               └── service/                     # Business Logic Layer Interface
│   │   │                   └── BankingSystem.java
│   │   │               └── service/impl/                # Business Logic Layer Implementation
│   │   │                   └── BankingSystemImpl.java
│   │   └── resources/
│   │       ├── application.properties # Spring Boot configuration
│   │       └── schema.sql             # H2 Database schema initialization
├── .github/
│   └── workflows/
│       └── deploy-backend.yml         # GitHub Actions CI/CD Workflow
├── Dockerfile                       # Docker build instructions
└── pom.xml                          # Maven Project Object Model

⚙️ Local Setup & Running the Backend
Prerequisites:

Java 17 JDK

Maven

Docker Desktop (optional, for local containerization testing)

Your preferred IDE (e.g., IntelliJ IDEA, VS Code)

Clone the Repository:

git clone https://github.com/ShivamBawa0793/banking-system-api.git
cd BankingSystemProject # Or whatever your root folder is named

Build the Project:

mvn clean package

This will compile the code, run tests (if any), and package the application into an executable JAR file in the target/ directory.

Run the Spring Boot Application:

java -jar target/banking-system-2.0.0-SNAPSHOT.jar # Adjust JAR name if different

The application will start on http://localhost:8080 by default.

Access H2 Console (Optional):
While the application is running, you can access the H2 database console at http://localhost:8080/h2-console.

JDBC URL: jdbc:h2:mem:bankingsystem;DB_CLOSE_DELAY=-1;

User Name: sa

Password: (leave blank)

🌐 API Endpoints (via Postman/cURL)
The API base URL is http://localhost:8080/api/bank (for local testing).

Method

Endpoint

Description

Request Body (JSON)

Example Response (JSON)

POST

/accounts?accountId={id}

Create a new bank account.

(None)

{"success": true, "message": "Account '...' created."}

POST

/deposit

Deposit funds into an account.

{"accountId": "...", "amount": 100}

{"success": true, "message": "Deposit successful.", "newBalance": 100}

POST

/transfer

Transfer funds between two accounts.

{"sourceAccountId": "...", "targetAccountId": "...", "amount": 50}

{"success": true, "message": "Transfer successful.", "newBalance": ...}

GET

/accounts/{accountId}

Get details of a specific account.

(None)

{"id": "...", "balance": ..., "totalOutgoing": ...}

GET

/accounts

Get details of all accounts.

(None)

[{"id": "...", "balance": ...}, ...]

GET

/accounts/{accountId}/balance

Check the current balance of an account.

(None)

{"success": true, "message": "Balance retrieved.", "newBalance": ...}

🚀 Deployment to AWS EC2
This application is designed for automated deployment to an AWS EC2 instance.

Dockerization: The application is containerized using the provided Dockerfile.

AWS EC2 Setup: A t2.micro (Free Tier) EC2 instance running Amazon Linux 2023 (or Ubuntu) is used, with Docker and Nginx installed.

Nginx as Reverse Proxy: Nginx is configured to:

Serve the static React frontend files (from a separate frontend repository).

Proxy API requests (/api/bank/) to the Spring Boot backend running in a Docker container on localhost:8080.

🔄 CI/CD with GitHub Actions
A GitHub Actions workflow (.github/workflows/deploy-backend.yml) automates the deployment process:

Trigger: The workflow is configured to run on pushes to the develop branch and can also be triggered manually via workflow_dispatch.

Steps:

Checkout code.

Set up Java JDK.

Log in to Docker Hub.

Build the Maven project (JAR).

Build the Docker image.

Push the Docker image to Docker Hub.

SSH into the EC2 instance (using SSH key stored as a GitHub Secret).

Pull the latest Docker image on EC2.

Stop and remove the old running container.

Start a new Docker container with the updated image.

Important Note on EC2 Public IP: As this deployment utilizes a free-tier EC2 instance, its public IP address may change upon instance restart or stop/start cycles. The application, however, remains fully functional on localhost and when accessed via the current public IP.

🤝 Contribution
Feel free to fork this repository, explore the code, and suggest improvements!
