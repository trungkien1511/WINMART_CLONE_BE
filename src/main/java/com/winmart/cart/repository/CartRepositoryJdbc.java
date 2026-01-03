package com.winmart.cart.repository;

import com.winmart.cart.model.CartRow;
import com.winmart.common.SqlLoader;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CartRepositoryJdbc {

    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sql; // util load file sql

    public CartRepositoryJdbc(
            NamedParameterJdbcTemplate jdbc,
            SqlLoader sql
    ) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    public Optional<CartRow> findActiveByUserId(UUID userId) {
        var sqlText = sql.load("cart/sql/cart_get_active_by_user.sql");

        var carts = jdbc.query(
                sqlText,
                Map.of("userId", userId),
                cartRowMapper()
        );

        return carts.stream().findFirst();
    }

    public Optional<CartRow> findActiveByGuestId(UUID guestId) {
        var sqlText = sql.load("cart/sql/cart_get_active_by_guest.sql");
        var carts = jdbc.query(sqlText, Map.of("guestId", guestId), cartRowMapper());
        return carts.stream().findFirst();
    }

    /**
     * LẤY HOẶC TẠO cart active cho user
     * – an toàn concurrency
     */

    public CartRow getOrCreateActiveCartForUser(UUID userId) {

        // 1️⃣ thử lấy cart active trước
        var existing = findActiveByUserId(userId);
        if (existing.isPresent()) {
            return existing.get();
        }

        // 2️⃣ chưa có → thử tạo
        try {
            var createSql = sql.load("cart/sql/cart_create_for_user.sql");

            return jdbc.queryForObject(
                    createSql,
                    Map.of("userId", userId),
                    cartRowMapper()
            );

        } catch (DuplicateKeyException ex) {
            // 3️⃣ nếu bị conflict (do request khác vừa tạo)
            // → query lại
            return findActiveByUserId(userId)
                    .orElseThrow(() ->
                            new IllegalStateException("Active cart not found after duplicate key"));
        }
    }

    @Transactional
    public CartRow getOrCreateActiveCartForGuest(UUID guestId) {
        var existing = findActiveByGuestId(guestId);
        if (existing.isPresent()) return existing.get();

        try {
            var createSql = sql.load("cart/sql/cart_create_for_guest.sql");
            return jdbc.queryForObject(createSql, Map.of("guestId", guestId), cartRowMapper());
        } catch (DuplicateKeyException ex) {
            return findActiveByGuestId(guestId)
                    .orElseThrow(() -> new IllegalStateException("Active cart not found after duplicate key"));
        }
    }

    private static RowMapper<CartRow> cartRowMapper() {
        return (rs, rowNum) -> new CartRow(
                rs.getObject("id", UUID.class),
                rs.getObject("user_id", UUID.class),
                rs.getObject("guest_id", UUID.class),
                rs.getBoolean("is_active"),
                rs.getBigDecimal("total_amount"),
                rs.getBigDecimal("total_discount"),
                rs.getObject("voucher_id", UUID.class),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant()
        );
    }

    public CartRow findById(UUID cartId) {
        var sqlCartId = sql.load("cart/sql/cart_find_by_id.sql");
        return jdbc.queryForObject(
                sqlCartId,
                Map.of("cartId", cartId),
                cartRowMapper()
        );
    }

    public void recalcTotals(UUID cartId) {
        var sqlCal = sql.load("cart/sql/cart_recalc_totals.sql");
        jdbc.update(sqlCal, Map.of("cartId", cartId));
    }


}


