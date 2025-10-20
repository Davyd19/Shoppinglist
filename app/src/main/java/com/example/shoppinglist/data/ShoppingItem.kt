package com.example.shoppinglist.data

import java.util.UUID

// Data class untuk merepresentasikan satu item belanja.
// Menggunakan Parcelable agar bisa dikirim antar layar (meski di versi ini tidak dipakai, ini adalah best practice).
data class ShoppingItem(
    val id: String = UUID.randomUUID().toString(), // ID unik untuk setiap item
    val name: String,
    val quantity: Int,
    val price: Double
)
