package com.example.inventoryservice.repository;

import com.example.inventoryservice.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {

   List<Inventory> findBySkuCodeIn(List<String> skuCode);
}
