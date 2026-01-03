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
where guest_id = :guestId
  and is_active = true
limit 1