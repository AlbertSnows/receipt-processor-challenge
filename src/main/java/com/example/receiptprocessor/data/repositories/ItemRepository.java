package com.example.receiptprocessor.data.repositories;

import com.example.receiptprocessor.data.entities.Item;
import com.example.receiptprocessor.data.entities.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<com.example.receiptprocessor.data.entities.Item, UUID> {
}
