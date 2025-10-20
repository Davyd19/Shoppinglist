package com.example.shoppinglist.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoppinglist.data.ShoppingItem
import com.example.shoppinglist.formatRupiah
import com.example.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.delay

// Menggunakan anotasi Material 3 yang benar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingList(items: List<ShoppingItem>, onRemoveItem: (ShoppingItem) -> Unit) {
    val totalCost = items.sumOf { it.price * it.quantity }

    if (items.isEmpty()) {
        EmptyState()
    } else {
        Column {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    // Menggunakan 'rememberSwipeToDismissBoxState' dari Material 3
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            // Cek apakah item digeser penuh ke salah satu sisi
                            if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                                onRemoveItem(item)
                                true // Konfirmasi penghapusan
                            } else {
                                false // Jangan hapus jika tidak digeser penuh
                            }
                        }
                    )

                    var isVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(item.id) {
                        delay(50) // Delay kecil untuk animasi
                        isVisible = true
                    }

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
                    ) {
                        // Menggunakan 'SwipeToDismissBox' dari Material 3
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = { DismissBackground(dismissState = dismissState) },
                            content = { ShoppingListItem(item = item) },
                            // Hanya izinkan geser dari kanan ke kiri
                            enableDismissFromStartToEnd = false
                        )
                    }
                }
            }
            TotalCostCard(totalCost = totalCost)
        }
    }
}

@Composable
fun ShoppingListItem(item: ShoppingItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.quantity} x ${formatRupiah(item.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatRupiah(item.price * item.quantity),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color by animateColorAsState(
        // Ganti warna jika targetnya bukan 'Settled' (posisi default)
        targetValue = if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) MaterialTheme.colorScheme.errorContainer else Color.Transparent,
        label = "Dismiss Background Color"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp),
        // Selalu letakkan ikon di ujung kanan
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            Icons.Default.Delete,
            contentDescription = "Hapus",
            tint = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
fun TotalCostCard(totalCost: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Total Belanja", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = formatRupiah(totalCost), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Daftar belanja Anda kosong",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Text(
            text = "Mulai tambahkan item baru di atas.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListPreview() {
    ShoppingListTheme {
        val previewItems = listOf(
            ShoppingItem(name = "Susu UHT", quantity = 2, price = 15000.0),
            ShoppingItem(name = "Roti Tawar", quantity = 1, price = 12000.0)
        )
        ShoppingList(items = previewItems, onRemoveItem = {})
    }
}

