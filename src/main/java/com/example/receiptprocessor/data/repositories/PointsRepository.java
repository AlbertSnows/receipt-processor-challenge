package com.example.receiptprocessor.data.repositories;

import com.example.receiptprocessor.data.entities.Points;
import com.example.receiptprocessor.data.entities.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PointsRepository extends JpaRepository<Points, UUID> {
	Points findByReceipt(Receipt receipt);
}
