# Quidax Price Ticker API 🚀

A simple Spring Boot REST API that fetches and displays real-time market ticker data for all trading pairs from the public Quidax API.

## Features

- Get real-time ticker data for all available markets.
- Get real-time ticker data for a single, specified market.
- Built with a clean, layered architecture (Controller, Service, DTO).

## Technologies Used

- Java 17
- Spring Boot 3
- Maven
- Lombok

## API Endpoints

| Method | Endpoint                 | Description                                                 |
|--------|--------------------------|-------------------------------------------------------------|
| GET    | /api/v1/tickers          | Get real-time ticker data for all markets.                |
| GET    | /api/v1/tickers/{market} | Get real-time ticker data for a specific market.          |

## Export to Sheets
**Example Usage:**
- All tickers: `http://localhost:8080/api/v1/tickers`
- Specific ticker: `http://localhost:8080/api/v1/tickers/btcn`

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

- JDK (Java Development Kit) 17 or later
- Maven

### Installation & Running the App

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/quidax-price-ticker.git
   ```

2. Navigate to the project directory:
   ```bash
   cd quidax-price-ticker
   ```

3. Run the application using the Maven wrapper:
    - On macOS/Linux:
      ```bash
      ./mvnw spring-boot:run
      ```
    - On Windows:
      ```bash
      mvnw.cmd spring-boot:run
      ```

The application will start on `http://localhost:8080`.