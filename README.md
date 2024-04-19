# E-Shop API Java Challenge
The solution developed for this challenge is a Spring Boot REST API, together with a SQL-Lite database for persistence.

This is a Dockerized version of the e-shop Spring Boot application. The application is containerized using Docker, which simplifies deployment and ensures consistency across different environments.

## Prerequisites

- Install [Docker](https://www.docker.com/products/docker-desktop) on your machine.

## Building the Docker Image

To build the Docker image, navigate to the root directory of the project where the Dockerfile `Dockerfile` is located and run the following command:

```bash
docker build -t eshop-api .
```

## Running the Application
Once the image is built, you can run the application using Docker:

```bash
docker run -p 8081:8080 eshop-api
```

## Eshop API Endpoints
The following REST API endpoints have been exposed, which implement the functionality of the system:
If you have started the containerised version, the endpoints are available at the base URL: `http://localhost:8081/`
### GET /products
- **URL**: `/products` (all products)
- **URL**: `/products/:id` (specific product with id)
- **Method**: `GET`
- **Description**: Returns all products stored
- **Responses**:
    - **200 OK** : Products/Product
- **Sample Response**:
   ```  
  [
    {
        "productId": 1,
        "name": "Fancy IPA Beer",
        "price": 5.99,
        "addedAt": "2022/09/01",
        "labels": [
        "drink",
        "limited"
    }
  ]
  ```
### POST /products
- **URL**: `/products`
- **Method**: `POST`
- **Description**: Creates a new product
- **Responses**:
    - **201 OK**: Product created successfully 
    - **400 BAD REQUEST**: Label not valid
    - **400 BAD REQUEST**: Product name already exists
- - **Sample Request body**:
```
{
    "name": "Cheap and cheerful lager beer",
    "price": 3.99,
    "labels": [
        "drink"
    ]
}
```
- **Sample Response**:
```  
{
    "productId": 7,
    "name": "Cheap and cheerful lager beer",
    "price": 3.99,
    "addedAt": "2024/04/18",
    "labels": [
        "drink"
    ]
}
```
### DELETE /products
- **URL**: `/products/:id` 
- **Method**: `DELETE`
- **Description**: Deletes an existing product
- **Responses**:
    - **204 OK**: Product :id deleted

### POST /carts
- **URL**: `/carts`
- **Method**: `POST`
- **Description**: Creates a new shopping cart
- **Responses**:
    - **201 OK** : Products/Product
    - **404 NOT FOUND** : Product could not be found
- **Sample Request body**:
```
[
  {"productId": 1, "quantity": 3},
  {"productId": 2, "quantity": 3}
]
```
- **Sample Response**:
```  
{
  "cartId": 4,
  "products": [
  {
    "productId": 1,
    "quantity": 3
  },
  {
    "productId": 2,
    "quantity": 3
  }
  ],
  "checkedOut": false
}
```
### GET /carts
- **URL**: `/carts`
- **Method**: `GET`
- **Description**: Returns all carts
- **Responses**:
  - **200 OK** : Products/Product
- **Sample Response**:
   ```  
  [
    {
        "productId": 1,
        "name": "Fancy IPA Beer",
        "price": 5.99,
        "addedAt": "2022/09/01",
        "labels": [
        "drink",
        "limited"
    }
  ]
  ```
### PUT /carts
- **URL**: `/carts/:id`
- **Method**: `PUT`
- **Description**: Modifies an existing shopping cart
- **Responses**:
  - **200 OK** : Cart modified
  - **404 NOT FOUND** : Cart could not be found
  - **404 NOT FOUND** : Product could not be found
  - **400 BAD REQUEST** : Cart already checked out
- **Sample Response**:
   ```  
  [
    {
        "productId": 1,
        "name": "Fancy IPA Beer",
        "price": 5.99,
        "addedAt": "2022/09/01",
        "labels": [
        "drink",
        "limited"
    }
  ]
  ```
### POST /carts/:id/checkout
- **URL**: `/carts/:id/checkout`
- **Method**: `POST`
- **Description**: Marks a cart as checked out
- **Responses**:
  - **200 OK** : Cart checked out and total cost added
  - **404 NOT FOUND** : Cart could not be found
  - **400 BAD REQUEST** : Cart already checked out
- **Sample Response**:
```  
{
  "cart": {
    "cartId": 1,
    "products": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 3,
        "quantity": 1
      }
    ],
    "checkedOut": true
  },
  "totalCost": 16.23
}
```


## Database design
The design of the database schema was built in mind of normalisation principles to reduce data redundancy and ensure data integrity, effectively segregating product details and cart information into distinct tables that are interconnected via a relationship table (cart_items).  
This design optimizes queries with primary and foreign keys and enhances scalability through the use of JSON for product labels, allowing easy future expansions.

## Extensive testing strategy
The tests implemented ensure the robustness and functionality of the eshop-api through comprehensive coverage of various scenarios:

### Basic Operations: 
Tests cover all CRUD operations on carts and products, including GET, POST, PUT, and DELETE requests to validate standard functionalities.

### Edge Cases: 
Includes tests for scenarios such as submissions with non-existent products, requests with negative or zero quantities, and operations with missing mandatory fields (e.g., product ID, quantity).

### Error Handling:
Validates the system's response to erroneous inputs and exceptional conditions, such as attempting operations on checked-out carts, deleting products that are currently in a cart, or submitting invalid labels and price formats.

### Validation Checks: 
Ensures that all input validations are enforced, including checks against empty, null, non-numerical price values, and improper label validations.

This extensive testing approach guarantees not only the correctness of basic operations but also the system's resilience and graceful error handling under various conditions, ensuring integrity and enhancing user experience.

## Transactional design
The api employs the use of the  @Transactional annotation in the service layer, enhancing data integrity and consistency across operations. 
This approach simplifies development by managing transaction boundaries automatically, ensuring that database operations either complete successfully as a whole or rollback in case of an error, providing robust application behavior and preventing data corruption.

## Error handling
ControllerAdvice has been used to centralise the management of exceptions, streamlining responses with detailed ErrorResponseDTO objects that enhance the clarity of API errors.

## Concurrency
SQLite only supports one write transaction at a time. When multiple transactions attempt to access the database simultaneously, SQLite may lock, leading to potential SQLITE_BUSY errors. To manage this, the api utilises the @Retryable annotation to automatically retry the transaction up to three times.

## Code formatting
Spotless code formatter using googleJavaFormat has been employed to maintain consistent and clean code formatting.
