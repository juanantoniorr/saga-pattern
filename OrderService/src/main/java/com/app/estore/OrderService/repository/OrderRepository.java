package com.app.estore.OrderService.repository;

import com.app.estore.OrderService.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,String> {
}
