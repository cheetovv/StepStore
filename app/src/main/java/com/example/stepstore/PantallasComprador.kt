package com.example.stepstore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stepstore.ui.theme.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun InicioScreen(onProductoClick: (Int) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Hola 👋", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Encuentra tu próximo par", color = TextoSecundario)
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Primario, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text("Nueva colección", color = Acento, fontWeight = FontWeight.Bold)
                    Text("Hasta 30% de descuento", color = Color.White, style = MaterialTheme.typography.titleLarge)
                }
            }
        }
        item {
            Text("Destacados", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(productosEjemplo.take(4)) { p ->
                    ProductoCard(p, onClick = { onProductoClick(p.id) }, modifier = Modifier.width(160.dp))
                }
            }
        }
    }
}

@Composable
fun CatalogoScreen(onProductoClick: (Int) -> Unit) {
    var busqueda by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Todos") }
    val categorias = listOf("Todos", "Deportivo", "Casual", "Formal")

    val filtrados = productosEjemplo.filter {
        (categoria == "Todos" || it.categoria == categoria) &&
                it.nombre.contains(busqueda, ignoreCase = true)
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            placeholder = { Text("Buscar zapatos") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categorias) { cat ->
                FilterChip(
                    selected = categoria == cat,
                    onClick = { categoria = cat },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Acento,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtrados) { p ->
                ProductoCard(p, onClick = { onProductoClick(p.id) })
            }
        }
    }
}

data class ItemCarrito(val producto: Producto, val talla: Int, val cantidad: Int)

@Composable
fun DetalleScreen(
    producto: Producto,
    onVolver: () -> Unit,
    onAgregar: (Producto, Int) -> Unit
) {
    val tallas = listOf(36, 37, 38, 39, 40, 41, 42)
    var talla by remember { mutableStateOf(40) }
    var agregado by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(producto.color.copy(alpha = 0.15f))
        ) {
            IconButton(onClick = onVolver, modifier = Modifier.padding(8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                producto.marca.take(1),
                fontSize = 96.sp,
                fontWeight = FontWeight.Bold,
                color = producto.color,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Column(Modifier.padding(16.dp)) {
            Text(producto.marca, color = TextoSecundario)
            Text(producto.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(formatoPrecio(producto.precio), color = Acento, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Text("Talla", fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tallas) { t ->
                    FilterChip(
                        selected = talla == t,
                        onClick = { talla = t },
                        label = { Text(t.toString()) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Acento,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Zapato de categoría ${producto.categoria.lowercase()}, cómodo y resistente para el uso diario.",
                color = TextoSecundario
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onAgregar(producto, talla); agregado = true },
                colors = ButtonDefaults.buttonColors(containerColor = Acento),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Agregar al carrito") }
            if (agregado) {
                Spacer(Modifier.height(8.dp))
                Text("Agregado al carrito ✓", color = Exito)
            }
        }
    }
}

@Composable
fun CarritoScreen(items: MutableList<ItemCarrito>, onPagar: () -> Unit) {
    if (items.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tu carrito está vacío", color = TextoSecundario)
        }
        return
    }
    val total = items.sumOf { it.producto.precio * it.cantidad }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mi carrito", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(items) { index, item ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(item.producto.color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(item.producto.marca.take(1), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = item.producto.color)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(item.producto.nombre, fontWeight = FontWeight.Bold)
                            Text("Talla ${item.talla}", color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
                            Text(formatoPrecio(item.producto.precio), color = Acento, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = { items.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Alerta)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = {
                                    if (item.cantidad > 1) items[index] = item.copy(cantidad = item.cantidad - 1)
                                }) { Text("−", fontSize = 20.sp) }
                                Text(item.cantidad.toString(), fontWeight = FontWeight.Bold)
                                TextButton(onClick = {
                                    items[index] = item.copy(cantidad = item.cantidad + 1)
                                }) { Text("+", fontSize = 20.sp) }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total", style = MaterialTheme.typography.titleMedium)
            Text(formatoPrecio(total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onPagar, modifier = Modifier.fillMaxWidth()) { Text("Ir a pagar") }
    }
}

@Composable
fun PagoScreen(total: Int, onVolver: () -> Unit, onPagoExitoso: () -> Unit) {
    var metodo by remember { mutableStateOf("Tarjeta") }
    var numero by remember { mutableStateOf("") }
    var titular by remember { mutableStateOf("") }
    var vence by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var confirmado by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text("Pago", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Primario), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Total a pagar", color = Color.White)
                Text(formatoPrecio(total), color = Acento, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Método de pago", fontWeight = FontWeight.Bold)
        listOf("Tarjeta", "PSE", "Contra entrega").forEach { opcion ->
            Row(
                Modifier.fillMaxWidth().clickable { metodo = opcion },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = metodo == opcion,
                    onClick = { metodo = opcion },
                    colors = RadioButtonDefaults.colors(selectedColor = Acento)
                )
                Text(opcion)
            }
        }
        if (metodo == "Tarjeta") {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = numero, onValueChange = { numero = it },
                label = { Text("Número de tarjeta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = titular, onValueChange = { titular = it },
                label = { Text("Nombre del titular") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = vence, onValueChange = { vence = it },
                    label = { Text("MM/AA") }, modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = cvv, onValueChange = { cvv = it },
                    label = { Text("CVV") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { confirmado = true },
            colors = ButtonDefaults.buttonColors(containerColor = Acento),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Pagar ${formatoPrecio(total)}") }
    }

    if (confirmado) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("¡Pago exitoso!") },
            text = { Text("Tu pedido fue registrado correctamente.") },
            confirmButton = { TextButton(onClick = onPagoExitoso) { Text("Aceptar") } }
        )
    }
}

@Composable
fun PerfilCompradorScreen(onCerrarSesion: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier.size(96.dp).background(Primario, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("L", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Text("Laura Gómez", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("laura@correo.com", color = TextoSecundario)
        Spacer(Modifier.height(24.dp))

        val opciones = listOf(
            Icons.Default.ShoppingCart to "Mis pedidos",
            Icons.Default.LocationOn to "Direcciones de envío",
            Icons.Default.Edit to "Editar perfil"
        )
        opciones.forEach { (icono, texto) ->
            ListItem(
                headlineContent = { Text(texto) },
                leadingContent = { Icon(icono, contentDescription = null, tint = Acento) },
                modifier = Modifier.clickable { }
            )
            HorizontalDivider()
        }

        Spacer(Modifier.weight(1f))
        OutlinedButton(
            onClick = onCerrarSesion,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Alerta),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión")
        }
    }
}