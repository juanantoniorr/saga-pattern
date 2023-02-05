# saga-pattern
-- Project that demonstrate the saga pattern in order to have transactional operations between microservices.
Docker 
- Axon  server Command Query Responsibility Segregation (CQRS)-> running on port: http://localhost:8024/
- Mysql database server -> port: 33060, root, admin

Project Structure
- Eureka server : http://localhost:8761/
- Gateway: http://localhost:8082/
- Product Microservice api rest: /api/products
- Order Microservice api rest: /api/orders
-core: project that is a dependency on orders and products microservices

1 Request sample -> POST: localhost:8082/products-service/api/products
BODY: {
    "title": "test2",
    "price":"20",
    "quantity": "40"
}
2 --Return orderId
2.1 ///Request to order a product: localhost:8082/orders-service/api/orders
{
"productId":"idFromFirstRequest",
"quantity":1,
"addressId":"afbb5881-a872-4d13-993c-faeb8350eea4"
}

#SETUP
- MAKE SURE AXON SERVER AND MYSQL INSTANCES ARE RUNNING
- RUN EUREKA SERVER
- RUN GATEWAY
- RUN PRODUCTS MICROSERVICE
- RUN ORDERS MICROSERVICE
- RUN PAYMENT SERVICE
- RUN USER-DETAILS SERVICE

#NOTES
-SAGA CLASS IS IN ORDERS MICROSERVICE

