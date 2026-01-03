update carts
set total_amount   = coalesce((select sum(quantity * unit_price)
                               from cart_items
                               where cart_id = :cartId), 0),
    total_discount = coalesce((select sum(quantity * discount)
                               from cart_items
                               where cart_id = :cartId), 0),
    updated_at     = now()
where id = :cartId;
