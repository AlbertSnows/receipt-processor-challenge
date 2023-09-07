package com.example.receiptprocessor.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface Receipt extends JpaRepository<com.example.receiptprocessor.data.entities.Receipt, Long> {
}
