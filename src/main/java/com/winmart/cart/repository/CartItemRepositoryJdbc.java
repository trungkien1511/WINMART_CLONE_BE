package com.winmart.cart.repository;

import com.winmart.cart.dto.CartItemView;
import com.winmart.cart.model.CartItemRow;
import com.winmart.common.SqlLoader;
import com.winmart.product.query.dto.ProductPackagingDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class CartItemRepositoryJdbc {

    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sqlLoader;

    public CartItemRepositoryJdbc(NamedParameterJdbcTemplate jdbc, SqlLoader sqlLoader) {
        this.jdbc = jdbc;
        this.sqlLoader = sqlLoader;
    }


    public List<CartItemView> findViewsByCartId(UUID cartId) {
        var sqlText = sqlLoader.load("cart/sql/cart_item_views_by_cart_id.sql");

        return jdbc.query(sqlText, Map.of("cartId", cartId), (rs, rowNum) -> {
            var unitPrice = rs.getBigDecimal("unit_price");
            var discount = rs.getBigDecimal("discount");
            if (discount == null) discount = BigDecimal.ZERO;

            var snapshotFinal = unitPrice.subtract(discount);

            var price = rs.getBigDecimal("price");
            var originalPrice = rs.getBigDecimal("original_price");
            int stockQty = rs.getInt("stock_quantity");
            boolean stock = stockQty > 0;

            boolean onSale = snapshotFinal.compareTo(originalPrice) < 0;

            var packaging = new ProductPackagingDto(
                    rs.getObject("product_packaging_id", UUID.class),
                    rs.getString("packaging_type_name"),
                    price,
                    originalPrice,
                    stockQty,
                    stock,
                    onSale
            );

            return new CartItemView(
                    rs.getObject("id", UUID.class),
                    rs.getInt("quantity"),
                    unitPrice,
                    discount,
                    snapshotFinal,

                    rs.getString("product_name"),
                    rs.getString("product_slug"),

                    packaging
            );
        });
    }

    /**
     * Add item: nếu đã tồn tại thì cộng quantity (UPSERT)
     */
    public void upsertIncreaseQty(
            UUID cartId,
            UUID productPackagingId,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal discount
    ) {
        var sql = sqlLoader.load("cart/sql/cart_item_upsert.sql");
        jdbc.queryForObject(
                sql,
                Map.of(
                        "cartId", cartId,
                        "productPackagingId", productPackagingId,
                        "quantity", quantity,
                        "unitPrice", unitPrice,
                        "discount", discount
                ),
                rowMapper()
        );
    }

    /**
     * Set quantity tuyệt đối (0 thì bạn có thể gọi delete thay vì update)
     */
    public int updateQuantity(UUID cartId, UUID itemId, int quantity) {
        var sql = sqlLoader.load("cart/sql/cart_item_update_qty.sql");
        return jdbc.update(sql, Map.of("cartId", cartId, "itemId", itemId, "quantity", quantity));
    }

    public int deleteById(UUID cartId, UUID itemId) {
        var sql = sqlLoader.load("cart/sql/cart_item_delete.sql");
        return jdbc.update(sql, Map.of("cartId", cartId, "itemId", itemId));
    }

    public void clear(UUID cartId) {
        var sql = sqlLoader.load("cart/sql/cart_items_clear.sql");
        jdbc.update(sql, Map.of("cartId", cartId));
    }

    private RowMapper<CartItemRow> rowMapper() {
        return (rs, rowNum) -> new CartItemRow(
                rs.getObject("id", UUID.class),
                rs.getObject("cart_id", UUID.class),
                rs.getObject("product_packaging_id", UUID.class),
                rs.getInt("quantity"),
                rs.getBigDecimal("unit_price"),
                rs.getBigDecimal("discount"),
                toInstant(rs.getTimestamp("created_at")),
                toInstant(rs.getTimestamp("updated_at"))
        );
    }

    private Instant toInstant(Timestamp ts) {
        return ts == null ? null : ts.toInstant();
    }


}
