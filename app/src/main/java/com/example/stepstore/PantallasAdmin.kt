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

private fun colorRol(rol: String) = when (rol) {
    "Administrador" -> Primario
    "Vendedor" -> Acento
    else -> Exito
}

@Composable
private fun TarjetaDato(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp)) {
            Text(valor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Acento)
            Text(titulo, color = TextoSecundario)
        }
    }
}

@Composable
fun DashboardScreen(totalUsuarios: Int, totalProductos: Int, onCerrarSesion: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Panel de administración", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Resumen general de StepStore", color = TextoSecundario)
            }
            IconButton(onClick = onCerrarSesion) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión", tint = Alerta)
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaDato("Usuarios", totalUsuarios.toString(), Modifier.weight(1f))
            TarjetaDato("Productos", totalProductos.toString(), Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaDato("Pedidos del mes", "86", Modifier.weight(1f))
            TarjetaDato("Ventas del mes", "$16 M", Modifier.weight(1f))
        }
        Spacer(Modifier.height(24.dp))
        Text("Actividad reciente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        listOf(
            "Nuevo usuario registrado: Ana Torres",
            "Pedido #1021 recibido por Calzado Express",
            "Producto \"Pegasus\" actualizado",
            "Pago confirmado en el pedido #1020"
        ).forEach { texto ->
            ListItem(
                headlineContent = { Text(texto) },
                leadingContent = { Icon(Icons.Default.Notifications, contentDescription = null, tint = Acento) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun UsuariosScreen(
    usuarios: List<Usuario>,
    onNuevo: () -> Unit,
    onEditar: (Int) -> Unit,
    onEliminar: (Usuario) -> Unit
) {
    var porEliminar by remember { mutableStateOf<Usuario?>(null) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Usuarios", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(usuarios, key = { it.id }) { u ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(48.dp).background(colorRol(u.rol), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(u.nombre.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(u.nombre, fontWeight = FontWeight.Bold)
                                Text(u.correo, color = TextoSecundario, style = MaterialTheme.typography.bodySmall)
                                Text(u.rol, color = colorRol(u.rol), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { onEditar(u.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Primario)
                            }
                            IconButton(onClick = { porEliminar = u }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Alerta)
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(72.dp)) }
            }
        }
        FloatingActionButton(
            onClick = onNuevo,
            containerColor = Acento,
            contentColor = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Default.Add, contentDescription = "Nuevo usuario") }
    }

    porEliminar?.let { u ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("Eliminar usuario") },
            text = { Text("¿Seguro que quieres eliminar a \"${u.nombre}\"?") },
            confirmButton = {
                TextButton(onClick = { onEliminar(u); porEliminar = null }) {
                    Text("Eliminar", color = Alerta)
                }
            },
            dismissButton = { TextButton(onClick = { porEliminar = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun FormularioUsuarioScreen(
    inicial: Usuario?,
    onGuardar: (String, String, String) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by remember { mutableStateOf(inicial?.nombre ?: "") }
    var correo by remember { mutableStateOf(inicial?.correo ?: "") }
    var rol by remember { mutableStateOf(inicial?.rol ?: "Comprador") }
    val valido = nombre.isNotBlank() && correo.contains("@")

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onCancelar) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                if (inicial == null) "Nuevo usuario" else "Editar usuario",
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
            value = correo, onValueChange = { correo = it },
            label = { Text("Correo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Text("Rol", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Comprador", "Vendedor", "Administrador").forEach { r ->
                FilterChip(
                    selected = rol == r,
                    onClick = { rol = r },
                    label = { Text(r) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Acento,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onGuardar(nombre.trim(), correo.trim(), rol) },
            enabled = valido,
            colors = ButtonDefaults.buttonColors(containerColor = Acento),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar") }
        TextButton(onClick = onCancelar, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
    }
}

@Composable
fun ReportesScreen() {
    val ventas = listOf("Jun" to 8, "Jul" to 11, "Ago" to 9, "Sep" to 14, "Oct" to 12, "Nov" to 16)
    val top = listOf("Air Runner" to 42, "Old Skool" to 35, "Pegasus" to 28, "Chuck Taylor" to 21)
    val maxTop = top.maxOf { it.second }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Reporte de ventas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Primario)) {
            Column(Modifier.padding(16.dp)) {
                Text("Ventas totales (6 meses)", color = Color.White)
                Text("$70 millones", color = Acento, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("Ventas por mes (millones)", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth().height(200.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            ventas.forEach { (mes, valor) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(valor.toString(), style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                    Box(
                        Modifier
                            .width(32.dp)
                            .height((valor * 10).dp)
                            .background(Acento, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                    )
                    Text(mes, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("Productos más vendidos", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        top.forEach { (nombre, unidades) ->
            Column(Modifier.padding(vertical = 6.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(nombre)
                    Text("$unidades uds", color = TextoSecundario)
                }
                Spacer(Modifier.height(4.dp))
                Box(Modifier.fillMaxWidth().height(8.dp).background(Color(0xFFE5E7EB), RoundedCornerShape(4.dp))) {
                    Box(
                        Modifier
                            .fillMaxWidth(unidades / maxTop.toFloat())
                            .height(8.dp)
                            .background(Primario, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}