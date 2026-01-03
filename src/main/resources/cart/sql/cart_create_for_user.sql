insert into carts (
    user_id,
    is_active,
    total_amount,
    total_discount,
    created_at,
    updated_at
)
values (
           :userId,
           true,
           0,
           0,
           now(),
           now()
       )
    returning
  id,
  user_id,
  guest_id,
  is_active,
  total_amount,
  total_discount,
  voucher_id,
  created_at,
  updated_at;
