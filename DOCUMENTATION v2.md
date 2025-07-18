# Project Documentation: Quidax Price Ticker

## 1. Project Overview
The Quidax Price Ticker is a microservice built with Java and the Spring Boot framework. Its sole purpose is to act as a bridge between a client application (like a web browser or another service) and the public Quidax API. It exposes two simple REST endpoints to fetch real-time price and market data for cryptocurrencies.

- `GET /api/v1/markets/tickers`: Fetches data for all available markets.
- `GET /api/v1/markets/tickers/{market}`: Fetches data for one specific market.

## 2. Core Dependencies
This project relies on two key dependencies defined in the `pom.xml` file.

- **spring-boot-starter-web**: This is a starter package that bundles all the necessary components for building web applications and REST APIs in Spring. It includes:
    - An embedded Tomcat server, so you don't need to configure a separate web server.
    - The Spring MVC framework for handling HTTP requests.
    - The Jackson library, which is the default for converting Java objects to and from JSON.

- **lombok**: This is a utility library that helps to significantly reduce boilerplate code. By using annotations, it automatically generates common methods like getters, setters, and constructors during the compilation phase.

# 3. Project Structure and Components

The code is organized into a layered architecture. The DTO package is now more detailed to accurately model the complex, nested responses from the Quidax API.

```
/dto/
 ├── Ticker.java              (Innermost: The actual ticker data)
 ├── MarketData.java          (Middle Layer: Contains Ticker and timestamp)
 ├── QuidaxResponse.java      (Wrapper for the 'all tickers' endpoint)
 └── SingleTickerResponse.java (Wrapper for the 'single ticker' endpoint)
/service/
 └── QuidaxService.java       (Business Logic and API communication)
/controller/
 └── TickerController.java    (API Endpoint Layer)
```

# 4. Detailed Class Explanations

## The DTO Layer (The Data Models)

Our DTOs are designed to exactly match the structure of the JSON responses from Quidax.

### 📄 Ticker.java

**Purpose:** Represents the innermost object containing the actual price and volume data.

**Code:**

```java
@Data
public class Ticker {
    private String low;
    private String high;
    @JsonProperty("vol")
    private String volume;
    @JsonProperty("last")
    private String price;
    @JsonProperty("sell")
    private String ask;
    @JsonProperty("buy")
    private String bid;
}
```

### 📄 MarketData.java

**Purpose:** Represents the middle layer in the JSON response. It holds the Ticker object and its corresponding timestamp.

**Code:**

```java
@Data
public class MarketData {
    private long at;
    private Ticker ticker;
    private String market; // Included in single-ticker responses
}
```

### 📄 QuidaxResponse.java

**Purpose:** Represents the top-level wrapper object for the /markets/tickers (all tickers) endpoint response.

**Code:**

```java
@Data
public class QuidaxResponse {
    private String status;
    private Map<String, MarketData> data;
}
```

### 📄 SingleTickerResponse.java

**Purpose:** Represents the top-level wrapper object for the /markets/{market}/ticker (single ticker) endpoint response.

**Code:**

```java
@Data
public class SingleTickerResponse {
    private String status;
    private String message;
    private MarketData data;
}
```

## service/QuidaxService.java - The Business Logic

This class communicates with the Quidax API and performs the crucial logic of "unwrapping" the nested JSON to extract the data we need.

### getTickers() Method:

**Action:** Calls the https://app.quidax.io/api/v1/markets/markets/tickers endpoint.

**Logic:** It expects a QuidaxResponse object. It then iterates through the data map, extracts the Ticker object from within each MarketData object, and builds a new, clean Map<String, Ticker> to return to the controller. This "unwrapping" is the key to handling the nested structure.

**Code:**

```java
public Map<String, Ticker> getTickers() {
    String url = "https://app.quidax.io/api/v1/markets/markets/tickers";
    ResponseEntity<QuidaxResponse> response = restTemplate.exchange(
            url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
    QuidaxResponse body = response.getBody();
    Map<String, Ticker> unwrappedTickers = new LinkedHashMap<>();
    if (body != null && "success".equals(body.getStatus())) {
        for (Map.Entry<String, MarketData> entry : body.getData().entrySet()) {
            unwrappedTickers.put(entry.getKey(), entry.getValue().getTicker());
        }
    }
    return unwrappedTickers;
}
```

### getTicker(String market) Method:

**Action:** Calls the https://app.quidax.io/api/v1/markets/markets/{market}/ticker endpoint.

**Logic:** It expects a SingleTickerResponse object. It then drills down through the layers (response.getData().getTicker()) to extract and return the final, innermost Ticker object.

**Code:**

```java
public Ticker getTicker(String market) {
    String url = "https://app.quidax.io/api/v1/markets/markets/" + market + "/ticker";
    SingleTickerResponse response = restTemplate.getForObject(url, SingleTickerResponse.class);
    if (response != null && "success".equals(response.getStatus())) {
        MarketData marketData = response.getData();
        if (marketData != null) {
            return marketData.getTicker();
        }
    }
    return null;
}
```

## controller/TickerController.java - The API Layer

This class is the public face of our application. It defines the HTTP endpoints and connects incoming web requests to our service logic.

```java
package com.quidaxproject.ticker.controller;
// ... imports
@RestController // 1. Spring Annotation
@RequestMapping("/api/v1/markets") // 2. Base Path
public class TickerController {

    // 3. Dependency Declaration
    private final QuidaxService quidaxService;

    // 4. Constructor Injection
    public TickerController(QuidaxService quidaxService) {
        this.quidaxService = quidaxService;
    }

    @GetMapping("/tickers") // 5. Endpoint for all tickers
    public Map<String, Ticker> getAllTickers() {
        return quidaxService.getTickers();
    }

    @GetMapping("/tickers/{market}") // 6. Endpoint for a single ticker
    public Ticker getTickerByMarket(@PathVariable String market) {
        return quidaxService.getTicker(market);
    }
}
```

### @RestController

This is a specialized controller annotation that combines `@Controller` and `@ResponseBody`. It tells Spring that this class will handle web requests and that its methods will return data (like JSON) directly in the response body, rather than returning a view template.

### @RequestMapping("/api/v1/markets")

This annotation sets a base URL path for all endpoints defined within this class. This means every endpoint here will be prefixed with `/api/v1/markets`.

### Dependency Declaration

We declare the `QuidaxService` that this controller needs to function. It is marked as final because its value will be set once and never changed.

### Constructor Injection

This is the modern, recommended way to handle dependencies in Spring. We require an instance of `QuidaxService` in the constructor. When Spring creates the `TickerController`, it sees this requirement and automatically "injects" the managed bean of `QuidaxService` that it created earlier.

### @GetMapping("/tickers")

This maps any HTTP GET request for `/api/v1/markets/tickers` to the `getAllTickers` method. The method simply calls the corresponding service method and returns the result. Spring automatically converts the returned `Map` into a JSON string.

### @GetMapping("/tickers/{market}")

This maps any HTTP GET request to `/api/v1/markets/tickers/` followed by a value. The `{market}` part is a path variable.

The `@PathVariable String market` annotation instructs Spring to extract the value from the `{market}` placeholder in the URL and pass it as an argument to the method. The method then uses this argument to call the service and returns the resulting `Ticker` object as JSON.
