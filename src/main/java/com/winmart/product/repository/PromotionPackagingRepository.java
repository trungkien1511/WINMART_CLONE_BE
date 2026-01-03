package com.winmart.product.repository;

import com.winmart.product.domain.PromotionPackaging;
import com.winmart.product.domain.PromotionPackagingId;
import com.winmart.product.query.dto.PromotionRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PromotionPackagingRepository extends JpaRepository<PromotionPackaging, PromotionPackagingId> {

    @Query("""
                select new com.winmart.product.query.dto.PromotionRow(
                    pp.packaging.id,
                    pr.discountType,
                    pr.discountValue
                )
                from PromotionPackaging pp
                join pp.promotion pr
                where pp.packaging.id = :packagingId
                  and pr.isActive = true
                  and current_timestamp between pr.startAt and pr.endAt
            """)
    List<PromotionRow> findValidPromotionsForPackaging(
            @Param("packagingId") UUID packagingId
    );


    @Query("""
                select new com.winmart.product.query.dto.PromotionRow(
                    pp.packaging.id,
                    pr.discountType,
                    pr.discountValue
                )
                from PromotionPackaging pp
                join pp.promotion pr
                where pp.packaging.id in :packagingIds
                  and pr.isActive = true
                  and current_timestamp between pr.startAt and pr.endAt
            """)
    List<PromotionRow> findValidPromotionsForPackagings(
            @Param("packagingIds") List<UUID> packagingIds
    );

}

