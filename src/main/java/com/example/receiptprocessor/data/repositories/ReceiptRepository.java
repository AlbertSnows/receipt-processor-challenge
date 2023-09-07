package com.example.receiptprocessor.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReceiptRepository extends JpaRepository<com.example.receiptprocessor.data.entities.Receipt, UUID> {
}
