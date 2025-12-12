package com.winmart.repository;

import com.winmart.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

    @Query("""
                SELECT p FROM Promotion p
                WHERE p.isActive = true
                  AND CURRENT_TIMESTAMP BETWEEN p.startAt AND p.endAt
            """)
    List<Promotion> findActivePromotions();
}

