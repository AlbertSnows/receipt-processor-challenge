package com.example.receiptprocessor.data.repositories;

import com.example.receiptprocessor.data.entities.Points;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PointsRepository extends JpaRepository<Points, UUID> {

}
