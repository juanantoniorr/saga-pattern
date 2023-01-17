# saga-pattern
-- Project that demonstrate the saga pattern in order to have transactional operations between microservices.
Docker 
- Axon  server Command Query Responsibility Segregation (CQRS)-> running on port: http://localhost:8024/
- Mysql database server -> port: 33060, root, admin

Project Structure
- Eureka server : http://localhost:8761/
- Gateway: http://localhost:8082/
- Product Microservice api rest: /api/products

Request sample -> POST: localhost:8082/products-service/api/products
BODY: {
    "title": "test2",
    "price":"20",
    "quantity": "40"
}
#SETUP
- MAKE SURE AXON SERVER AND MYSQL INSTANCES ARE RUNNING
- RUN EUREKA SERVER
- RUN GATEWAY
- RUN PRODUCTS MICROSERVICE
