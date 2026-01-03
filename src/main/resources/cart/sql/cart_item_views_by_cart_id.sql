select
    -- cart item
    ci.id,
    ci.cart_id,
    ci.product_packaging_id,
    ci.quantity,
    ci.unit_price,
    ci.discount,
    ci.created_at,
    ci.updated_at,

    -- packaging
    pp.product_id,
    pp.price,
    pp.original_price,
    pp.stock_quantity,
    pp.packaging_type_id,

    -- product
    p.name  as product_name,
    p.slug  as product_slug,

    -- packaging type
    pt.name as packaging_type_name

from cart_items ci
         join product_packagings pp
              on pp.id = ci.product_packaging_id
         join products p
              on p.id = pp.product_id
         left join packaging_types pt
                   on pt.id = pp.packaging_type_id
where ci.cart_id = :cartId
order by ci.created_at desc;
