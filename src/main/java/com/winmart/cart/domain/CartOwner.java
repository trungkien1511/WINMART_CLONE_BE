package com.winmart.cart.domain;

import java.util.UUID;

public sealed interface CartOwner permits CartOwner.User, CartOwner.Guest {
    record User(UUID userId) implements CartOwner {
    }

    record Guest(UUID guestId) implements CartOwner {
    }
}
