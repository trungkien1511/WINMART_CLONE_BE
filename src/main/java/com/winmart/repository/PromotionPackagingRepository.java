package com.winmart.repository;

import com.winmart.entity.Promotion;
import com.winmart.entity.PromotionPackaging;
import com.winmart.entity.PromotionPackagingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PromotionPackagingRepository extends JpaRepository<PromotionPackaging, PromotionPackagingId> {

    @Query("""
                SELECT pr FROM PromotionPackaging pp
                JOIN pp.promotion pr
                WHERE pp.packaging.id = :packagingId
                  AND pr.isActive = true
                  AND CURRENT_TIMESTAMP BETWEEN pr.startAt AND pr.endAt
            """)
    List<Promotion> findValidPromotionsForPackaging(UUID packagingId);
}

