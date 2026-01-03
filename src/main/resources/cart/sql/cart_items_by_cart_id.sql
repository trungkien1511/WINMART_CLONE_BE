select id,
       cart_id,
       product_packaging_id,
       quantity,
       unit_price,
       discount,
       created_at,
       updated_at
from cart_items
where cart_id = :cartId
order by created_at;
