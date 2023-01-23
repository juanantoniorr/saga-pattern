package com.app.estore.ProductService.repo;

import com.app.estore.ProductService.entity.ProductLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductLookupRepository extends JpaRepository<ProductLookupEntity,String> {
    Optional<ProductLookupEntity> findByProductIdOrTitle(String productId, String title);
}
