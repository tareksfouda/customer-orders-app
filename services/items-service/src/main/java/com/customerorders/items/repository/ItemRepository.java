package com.customerorders.items.repository;

import com.customerorders.items.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByNameContainingIgnoreCase(String name);
    List<Item> findByQuantityGreaterThan(Integer quantity);
}
