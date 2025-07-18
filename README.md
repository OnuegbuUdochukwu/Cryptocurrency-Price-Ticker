# Quidax Price Ticker API (v2) 🚀

A robust Spring Boot REST API that fetches and displays real-time market ticker data from the public Quidax API.

This project correctly handles the complex, nested JSON structure of the Quidax API by using a layered DTO (Data Transfer Object) approach to parse the responses and provide a clean, simplified output.

## Features
- Get real-time ticker data for all available markets.
- Get real-time ticker data for a single, specified market.
- Accurately models and unwraps nested JSON responses from the external API.
- Built with a clean, layered architecture (Controller, Service, DTO).

## Technologies Used
- Java 17
- Spring Boot 3
- Maven
- Lombok

## API Endpoints
| Method | Endpoint                       | Description                                        |
|--------|-------------------------------|----------------------------------------------------|
| GET    | `/api/v1/tickers`            | Get real-time ticker data for all markets.         |
| GET    | `/api/v1/tickers/{market}`   | Get real-time ticker data for a specific market.   |

## Export to Sheets
### Example Usage:
- All tickers: `http://localhost:8080/api/v1/tickers`
- Specific ticker: `http://localhost:8080/api/v1/tickers/btcngn`

A successful request to the specific ticker endpoint will return a clean, unwrapped JSON object:

```json
{
    "low": "178876454.0",
    "high": "184179782.0",
    "volume": "0.7871311",
    "price": "179333769.0",
    "ask": "180077100.0",
    "bid": "179447144.0"
}
```

## Getting Started
To get a local copy up and running, follow these simple steps.

### Prerequisites
- JDK (Java Development Kit) 17 or later
- Maven

### Installation & Running the App
Clone the repository:

```bash
git clone https://github.com/your-username/quidax-price-ticker.git
```
Navigate to the project directory:

```bash
cd quidax-price-ticker
```
Run the application using the Maven wrapper:

On macOS/Linux:

```bash
./mvnw spring-boot:run
```
On Windows:

```bash
mvnw.cmd spring-boot:run
```
The application will start on `http://localhost:8080`.