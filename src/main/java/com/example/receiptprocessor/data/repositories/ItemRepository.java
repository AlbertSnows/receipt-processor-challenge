package com.example.receiptprocessor.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemRepository extends JpaRepository<com.example.receiptprocessor.data.entities.Item, UUID> {
}
