package com.example.shoppinglist.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoppinglist.R
import com.example.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun SearchInput(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(stringResource(R.string.search_items)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SearchInputPreview() {
    ShoppingListTheme {
        SearchInput(query = "Bananas", onQueryChange = {})
    }
}