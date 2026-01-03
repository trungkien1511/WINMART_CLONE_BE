insert into cart_items (id,
                        cart_id,
                        product_packaging_id,
                        quantity,
                        unit_price,
                        discount,
                        created_at,
                        updated_at)
values (gen_random_uuid(),
        :cartId,
        :productPackagingId,
        :quantity,
        :unitPrice,
        :discount,
        now(),
        now())
on conflict (cart_id, product_packaging_id)
    do update set quantity   = cart_items.quantity + excluded.quantity,
                  unit_price = excluded.unit_price,
                  discount   = excluded.discount,
                  updated_at = now()
returning
    id,
    cart_id,
    product_packaging_id,
    quantity,
    unit_price,
    discount,
    created_at,
    updated_at;
