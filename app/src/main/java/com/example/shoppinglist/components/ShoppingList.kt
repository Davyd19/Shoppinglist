package com.example.shoppinglist.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun ShoppingList(items: List<String>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items) { item ->
            ShoppingListItem(item)
        }
    }
}

@Composable
fun ShoppingListItem(item: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListPreview() {
    ShoppingListTheme {
        ShoppingList(items = listOf("Milk", "Bread", "Eggs", "Apples"))
    }
}