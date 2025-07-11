# Project Documentation: Quidax Price Ticker

## 1. Project Overview
The Quidax Price Ticker is a microservice built with Java and the Spring Boot framework. Its sole purpose is to act as a bridge between a client application (like a web browser or another service) and the public Quidax API. It exposes two simple REST endpoints to fetch real-time price and market data for cryptocurrencies.

- `GET /api/v1/tickers`: Fetches data for all available markets.
- `GET /api/v1/tickers/{market}`: Fetches data for one specific market.

## 2. Core Dependencies
This project relies on two key dependencies defined in the `pom.xml` file.

- **spring-boot-starter-web**: This is a starter package that bundles all the necessary components for building web applications and REST APIs in Spring. It includes:
    - An embedded Tomcat server, so you don't need to configure a separate web server.
    - The Spring MVC framework for handling HTTP requests.
    - The Jackson library, which is the default for converting Java objects to and from JSON.

- **lombok**: This is a utility library that helps to significantly reduce boilerplate code. By using annotations, it automatically generates common methods like getters, setters, and constructors during the compilation phase.

## 3. Project Structure and Components
The code is organized into a standard, layered architecture within the `com.quidaxproject.ticker` package. This separation of concerns makes the application easier to understand, maintain, and test.

```
/dto/
 └── Ticker.java        (Data Model)
/service/
 └── QuidaxService.java   (Business Logic)
/controller/
 └── TickerController.java (API Endpoints)
```

## <u> Detailed Class Explanations </u>

## dto/Ticker.java - The Data Model

This class is a Data Transfer Object (DTO). Its only job is to define the "shape" or structure of the data we care about from the Quidax API.

```java
package com.quidaxproject.ticker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data // 1. Lombok Annotation
public class Ticker {

    // 2. Standard Fields
    private String low;
    private String high;
    private String volume;

    // 3. Mapped Fields
    @JsonProperty("last")
    private String price;

    @JsonProperty("sell")
    private String ask;

    @JsonProperty("buy")
    private String bid;
}
```

**@Data**: A powerful annotation from Lombok. It automatically generates essential methods like `getLow()`, `setLow(...)`, `getPrice()`, `setPrice(...)`, `toString()`, `equals()`, and `hashCode()` at compile time. This keeps our class clean and readable.

### Standard Fields

- low
- high
- volume

are declared here. Since their names exactly match the field names in the JSON response from the Quidax API, they don't need any special annotations.

### Mapped Fields

The **@JsonProperty("...")** annotation is from the Jackson library. It creates a mapping between the JSON field name and our Java field name. This is useful when the API's naming isn't ideal for our code.

- **@JsonProperty("last")**: Maps the JSON field named `last` to our Java field named `price`.
- **@JsonProperty("sell")**: Maps the JSON field named `sell` to our Java field named `ask`.
- **@JsonProperty("buy")**: Maps the JSON field named `buy` to our Java field named `bid`.

## service/QuidaxService.java - The Business Logic

This class contains the core logic of our application. It is responsible for communicating with the external Quidax API.

```java
package com.quidaxproject.ticker.service;
// ... imports
@Service // 1. Spring Annotation
public class QuidaxService {

    // 2. API Constant
    private static final String QUIDAX_API_BASE_URL = "https://api.quidax.com";

    // 3. HTTP Client
    private final RestTemplate restTemplate;

    // 4. Constructor
    public QuidaxService() {
        this.restTemplate = new RestTemplate();
    }

    // 5. Get All Tickers Method
    public Map<String, Ticker> getTickers() {
        String url = QUIDAX_API_BASE_URL + "/api/v1/tickers";
        
        ResponseEntity<Map<String, Ticker>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    // 6. Get Single Ticker Method
    public Ticker getTicker(String market) {
        String url = QUIDAX_API_BASE_URL + "/api/v1/tickers/" + market;
        return restTemplate.getForObject(url, Ticker.class);
    }
}
```

<Service>: This annotation tells the Spring Framework to treat this class as a service bean. Spring will create and manage a single instance (a "singleton") of this class for the entire application.

**API Constant**: A static final String to hold the base URL of the Quidax API. This is good practice to avoid hardcoding the same value in multiple places.

**RestTemplate**: This is Spring's primary tool for making HTTP requests to other services. We declare it as final to ensure it's initialized only once.

**Constructor**: The constructor initializes the RestTemplate instance when the QuidaxService is created by Spring.

### getTickers():

This method handles the request for all market data.

The Quidax API for all tickers returns a complex JSON object that looks like a Map (e.g., "btcn gn": {...}, "ethngn": {...}).

Because of this complex structure (`Map<String, Ticker>`), we use `restTemplate.exchange()` which provides more control. The `ParameterizedTypeReference` is crucial here; it gives the Jackson library the detailed type information it needs to correctly deserialize the JSON into our Java Map.

### getTicker(String market):

This method handles the request for a single market.

The URL is built by appending the specific market name (e.g., btcn gn).

Since the API returns a simple JSON object that directly matches our Ticker.java class, we can use the simpler `restTemplate.getForObject()` method. We provide the URL and the class type (`Ticker.class`), and Spring handles the conversion automatically.

## controller/TickerController.java - The API Layer

This class is the public face of our application. It defines the HTTP endpoints and connects incoming web requests to our service logic.

```java
package com.quidaxproject.ticker.controller;
// ... imports
@RestController // 1. Spring Annotation
@RequestMapping("/api/v1") // 2. Base Path
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

### @RequestMapping("/api/v1")

This annotation sets a base URL path for all endpoints defined within this class. This means every endpoint here will be prefixed with `/api/v1`.

### Dependency Declaration

We declare the `QuidaxService` that this controller needs to function. It is marked as final because its value will be set once and never changed.

### Constructor Injection

This is the modern, recommended way to handle dependencies in Spring. We require an instance of `QuidaxService` in the constructor. When Spring creates the `TickerController`, it sees this requirement and automatically "injects" the managed bean of `QuidaxService` that it created earlier.

### @GetMapping("/tickers")

This maps any HTTP GET request for `/api/v1/tickers` to the `getAllTickers` method. The method simply calls the corresponding service method and returns the result. Spring automatically converts the returned `Map` into a JSON string.

### @GetMapping("/tickers/{market}")

This maps any HTTP GET request to `/api/v1/tickers/` followed by a value. The `{market}` part is a path variable.

The `@PathVariable String market` annotation instructs Spring to extract the value from the `{market}` placeholder in the URL and pass it as an argument to the method. The method then uses this argument to call the service and returns the resulting `Ticker` object as JSON.