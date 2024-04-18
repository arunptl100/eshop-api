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



## Database design
## Extensive testing strategy
## Transactional design
## Error handling
## Concurrency


