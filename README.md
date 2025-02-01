

```markdown
# Scriptorium API

Scriptorium API is a RESTful API designed to manage documents and folders, with functionality for creating, retrieving, updating, and deleting (CRUD) documents and folders. The API also supports hierarchical structures and OpenAPI documentation.

## Features
- Create, retrieve, update, and delete documents and folders.
- Support for hierarchical parent-child relationships between documents.
- OpenAPI documentation with Swagger UI.
- Basic security configurations using Spring Security.
- Cross-Origin Resource Sharing (CORS) support.

---

## Table of Contents
1. [Technologies Used](#technologies-used)
2. [Setup and Installation](#setup-and-installation)
3. [Environment Variables](#environment-variables)
4. [Endpoints](#endpoints)
5. [Development](#development)
6. [Testing](#testing)
7. [Docker Deployment](#docker-deployment)
8. [License](#license)

---

## Technologies Used
- **Java 21**
- **Spring Boot 3.4.1**
  - Spring Data JPA
  - Spring Security
  - Spring Web
- **PostgreSQL**
- **Hibernate**
- **Lombok**
- **Docker**
- **OpenAPI / Swagger**

---

## Setup and Installation

### Prerequisites
1. **Java 21**: Install [Java 21](https://adoptium.net/).
2. **PostgreSQL**: Install [PostgreSQL](https://www.postgresql.org/).
3. **Docker**: Install [Docker](https://www.docker.com/).

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/scriptorium-api.git
   cd scriptorium-api
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

4. Access the API Swagger documentation:
    - Open your browser and navigate to `http://localhost:8081/swagger-ui.html`.

---

## Environment Variables
Set the following environment variables in your development environment:
- `DB_PASSWORD`: The password for your PostgreSQL database.

These can also be configured in the `application.yaml` file (not recommended for production).

---

## Endpoints

### Document Endpoints
| Method | Endpoint                         | Description                                 |
|--------|----------------------------------|---------------------------------------------|
| `POST` | `/api/documents`                 | Create a new document or folder.           |
| `GET`  | `/api/documents`                 | Retrieve all documents and folders.        |
| `GET`  | `/api/documents/{id}`            | Retrieve a specific document or folder.    |
| `GET`  | `/api/documents/{parentId}/children` | Retrieve child documents by parent ID.     |
| `GET`  | `/api/documents/children`        | Retrieve all root-level documents.         |
| `PUT`  | `/api/documents/{id}/rename`     | Rename a document or folder.               |
| `PUT`  | `/api/documents/{id}/move`       | Move a document or folder to a new parent. |
| `DELETE` | `/api/documents/{id}`          | Delete a document or folder.               |

---

## Development

### Project Structure
- **`config/`**: Contains application configurations (e.g., OpenAPI, security, and CORS).
- **`controller/`**: RESTful controllers for handling API endpoints.
- **`dto/`**: Data Transfer Objects (DTOs) for API responses.
- **`exception/`**: Custom exception handling.
- **`model/`**: JPA entity classes for documents.
- **`repository/`**: Interfaces for interacting with the database.
- **`service/`**: Business logic and data manipulation services.

---

## Testing
Run unit tests using:
```bash
./gradlew test
```

---

## Docker Deployment

### Build Docker Image
1. Build the Docker image:
   ```bash
   docker build -t scriptorium-api:latest .
   ```

2. Run the Docker container:
   ```bash
   docker run --rm -p 8081:8081 --name scriptorium-api-container scriptorium-api:latest
   ```

### Deploy to Render
1. Push your project to a Git repository (e.g., GitHub).
2. Go to [Render](https://render.com/) and create a new web service.
3. Select your repository and configure the following:
    - Environment: `Docker`.
    - Dockerfile Path: `Dockerfile`.
    - Port: `8081`.
4. Add environment variables (e.g., `DB_PASSWORD`).
5. Deploy and access your API via the public Render URL.

---

## License
This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).

---

## Author
Chris Young  
[Email](mailto:chris@example.com) | [Website](https://example.com)
