# 360 Backend API Documentation

This project exposes REST APIs for the 360 backend system.

## OpenAPI / Swagger

When the application is running, API documentation is available at:

- Swagger UI: `/swagger-ui/index.html`
- OpenAPI JSON: `/v3/api-docs`

Example local URLs:

- `http://localhost:8081/swagger-ui/index.html`
- `http://localhost:8081/v3/api-docs`

## Authentication

- The API uses JWT bearer authentication (`Authorization: ******
- Authentication endpoints are under `/api/v1/auth`.
- You can obtain a token from the login endpoint and authorize in Swagger UI.

## API Reference

See `/docs/api.md` for endpoint groups and base paths.
