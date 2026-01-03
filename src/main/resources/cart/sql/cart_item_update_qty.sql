update cart_items
set quantity   = :quantity,
    updated_at = now()
where id = :itemId
  and cart_id = :cartId;
