package com.example.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.shoppinglist.components.SearchInput
import com.example.shoppinglist.components.ShoppingList
import com.example.shoppinglist.data.ShoppingItem
import com.example.shoppinglist.ui.theme.ShoppingListTheme
import java.text.NumberFormat
import java.util.*

sealed class Screen(val route: String, val title: String, val icon: (@Composable () -> Unit)? = null) {
    object Home : Screen("home", "Catatan Belanja", { Icon(Icons.Default.Home, contentDescription = null) })
    object Profile : Screen("profile", "Profil", { Icon(Icons.Default.Person, contentDescription = null) })
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val bottomNavScreens = listOf(Screen.Home, Screen.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val title = when (currentDestination?.route) {
                        Screen.Home.route -> Screen.Home.title
                        Screen.Profile.route -> Screen.Profile.title
                        else -> "Catatan Belanja"
                    }
                    Text(title, fontWeight = FontWeight.SemiBold)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                bottomNavScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = screen.icon!!,
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(Screen.Home.route) { ShoppingListScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}

@Composable
fun ShoppingListScreen() {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val shoppingItems = remember { mutableStateListOf<ShoppingItem>() }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredItems by remember(searchQuery, shoppingItems) {
        derivedStateOf {
            if (searchQuery.isBlank()) shoppingItems
            else shoppingItems.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    if (showAddDialog) {
        AddItemDialog(
            onDismiss = { showAddDialog = false },
            onAddItem = { newItem ->
                shoppingItems.add(0, newItem)
                showAddDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchInput(query = searchQuery, onQueryChange = { searchQuery = it })
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.tambah_item))
        }
        Spacer(modifier = Modifier.height(16.dp))
        ShoppingList(
            items = filteredItems,
            onRemoveItem = { item ->
                shoppingItems.remove(item)
            }
        )
    }
}

@Composable
fun AddItemDialog(onDismiss: () -> Unit, onAddItem: (ShoppingItem) -> Unit) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.tambah_item_baru), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.nama_barang)) }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = quantity, onValueChange = { if (it.all { char -> char.isDigit() }) quantity = it }, label = { Text("Jumlah (Qty)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = price, onValueChange = { if (it.all { char -> char.isDigit() }) price = it }, label = { Text("Harga Satuan (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.batal)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val qtyInt = quantity.toIntOrNull() ?: 0
                        val priceDouble = price.toDoubleOrNull() ?: 0.0
                        if (name.isNotBlank() && qtyInt > 0) {
                            onAddItem(ShoppingItem(name = name, quantity = qtyInt, price = priceDouble))
                        }
                    }) {
                        Text(stringResource(R.string.tambah))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Header Profil
        Image(
            painter = painterResource(id = R.drawable.foto_pp),
            contentDescription = stringResource(R.string.foto_profil),
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.Nama),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.mahasiswa_sistem_informasi),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Kartu Informasi Detail (tanpa ikon)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                ProfileInfoRow(label = "NIM", value = stringResource(R.string.NIM))
                HorizontalDivider()
                ProfileInfoRow(label = "TTL", value = stringResource(R.string.TTL))
                HorizontalDivider()
                ProfileInfoRow(label = "Hobi", value = stringResource(R.string.hobi))
                HorizontalDivider()
                ProfileInfoRow(label = "Peminatan", value = stringResource(R.string.mobile_programming))
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp), // Padding vertikal ditambah untuk spasi
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Kolom untuk label dan value agar rapi
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

fun formatRupiah(amount: Double): String {
    val localeID = Locale("in", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID)
    format.maximumFractionDigits = 0
    return format.format(amount)
}

