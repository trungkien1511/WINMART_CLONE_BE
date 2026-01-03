select id,
       user_id,
       guest_id,
       is_active,
       total_amount,
       total_discount,
       voucher_id,
       created_at,
       updated_at
from carts
where id = :cartId
limit 1;
