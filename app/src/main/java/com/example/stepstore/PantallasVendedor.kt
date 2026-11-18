package com.example.stepstore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.stepstore.ui.theme.*

data class Pedido(val id: Int, val cliente: String, val producto: String, val total: Int, val estado: String)

private val pedidosEjemplo = listOf(
    Pedido(1021, "Laura Gómez", "Air Runner", 389000, "Pendiente"),
    Pedido(1020, "Carlos Ruiz", "Old Skool", 249000, "Enviado"),
    Pedido(1019, "Ana Torres", "Classic Leather", 279000, "Entregado"),
    Pedido(1018, "Juan Pérez", "Chuck Taylor", 269000, "Entregado"),
    Pedido(1017, "Marta Díaz", "Pegasus", 429000, "Enviado")
)

@Composable
fun MisProductosScreen(
    productos: List<Producto>,
    onNuevo: () -> Unit,
    onEditar: (Int) -> Unit,
    onEliminar: (Producto) -> Unit,
    titulo: String = "Mis productos"
) {
    var porEliminar by remember { mutableStateOf<Producto?>(null) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            if (productos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes productos", color = TextoSecundario)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(productos, key = { it.id }) { p ->
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(p.color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(p.marca.take(1), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = p.color)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(p.nombre, fontWeight = FontWeight.Bold)
                                    Text("${p.marca} · ${p.categoria}", color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
                                    Text(formatoPrecio(p.precio), color = Acento, fontWeight = FontWeight.Bold)
                                }
                                IconButton(onClick = { onEditar(p.id) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Primario)
                                }
                                IconButton(onClick = { porEliminar = p }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Alerta)
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }
        FloatingActionButton(
            onClick = onNuevo,
            containerColor = Acento,
            contentColor = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Default.Add, contentDescription = "Nuevo producto") }
    }

    porEliminar?.let { p ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("Eliminar producto") },
            text = { Text("¿Seguro que quieres eliminar \"${p.nombre}\"?") },
            confirmButton = {
                TextButton(onClick = { onEliminar(p); porEliminar = null }) {
                    Text("Eliminar", color = Alerta)
                }
            },
            dismissButton = { TextButton(onClick = { porEliminar = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun FormularioProductoScreen(
    inicial: Producto?,
    onGuardar: (String, String, Int, String) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf(inicial?.nombre ?: "") }
    var marca by remember { mutableStateOf(inicial?.marca ?: "") }
    var precio by remember { mutableStateOf(inicial?.precio?.toString() ?: "") }
    var categoria by remember { mutableStateOf(inicial?.categoria ?: "Deportivo") }
    val valido = nombre.isNotBlank() && marca.isNotBlank() && precio.toIntOrNull() != null

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onCancelar) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                if (inicial == null) "Nuevo producto" else "Editar producto",
                style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = nombre, onValueChange = { nombre = it },
            label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = marca, onValueChange = { marca = it },
            label = { Text("Marca") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = precio, onValueChange = { precio = it },
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Text("Categoría", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Deportivo", "Casual", "Formal").forEach { cat ->
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
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onGuardar(nombre.trim(), marca.trim(), precio.toInt(), categoria) },
            enabled = valido,
            colors = ButtonDefaults.buttonColors(containerColor = Acento),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar") }
        TextButton(onClick = onCancelar, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
    }
}

@Composable
fun PedidosScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Pedidos recibidos", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(pedidosEjemplo) { p ->
                val colorEstado = when (p.estado) {
                    "Pendiente" -> Acento
                    "Enviado" -> Primario
                    else -> Exito
                }
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pedido #${p.id}", fontWeight = FontWeight.Bold)
                            Text(p.estado, color = colorEstado, fontWeight = FontWeight.Bold)
                        }
                        Text(p.cliente, color = TextoSecundario)
                        Spacer(Modifier.height(4.dp))
                        Text("${p.producto} · ${formatoPrecio(p.total)}")
                    }
                }
            }
        }
    }
}

@Composable
fun PerfilVendedorScreen(totalProductos: Int, onCerrarSesion: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier.size(96.dp).background(Acento, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("C", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Text("Calzado Express", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("ventas@calzadoexpress.com", color = TextoSecundario)
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(totalProductos.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Acento)
                    Text("Productos", color = TextoSecundario)
                }
            }
            Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(pedidosEjemplo.size.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Acento)
                    Text("Pedidos", color = TextoSecundario)
                }
            }
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