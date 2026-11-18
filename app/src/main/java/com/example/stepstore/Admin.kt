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

data class Usuario(val id: Int, val nombre: String, val correo: String, val rol: String)

val usuariosEjemplo = listOf(
    Usuario(1, "Laura Gómez", "laura@correo.com", "Comprador"),
    Usuario(2, "Carlos Ruiz", "carlos@correo.com", "Comprador"),
    Usuario(3, "Calzado Express", "ventas@calzadoexpress.com", "Vendedor"),
    Usuario(4, "Ana Torres", "ana@correo.com", "Comprador"),
    Usuario(5, "Moda Urbana", "contacto@modaurbana.com", "Vendedor"),
    Usuario(6, "Andrés Admin", "admin@stepstore.com", "Administrador")
)

private val destinosAdmin = listOf(
    Destino("dashboard", "Panel", Icons.Default.Home),
    Destino("usuarios", "Usuarios", Icons.Default.Person),
    Destino("productos", "Productos", Icons.Default.ShoppingCart),
    Destino("reportes", "Reportes", Icons.Default.DateRange)
)

private val paletaAdmin = listOf(Acento, Primario, Exito, Alerta, TextoSecundario)

@Composable
fun AdminScreen(onCerrarSesion: () -> Unit) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val usuarios = remember { mutableStateListOf<Usuario>().apply { addAll(usuariosEjemplo) } }
    val productos = remember { mutableStateListOf<Producto>().apply { addAll(productosEjemplo) } }
    val rutasConBarra = listOf("dashboard", "usuarios", "productos", "reportes")

    Scaffold(
        bottomBar = {
            if (rutaActual in rutasConBarra) BarraInferior(nav, destinosAdmin, rutaActual)
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "dashboard",
            modifier = Modifier.padding(padding)
        ) {
            composable("dashboard") {
                DashboardScreen(usuarios.size, productos.size, onCerrarSesion)
            }
            composable("usuarios") {
                UsuariosScreen(
                    usuarios = usuarios,
                    onNuevo = { nav.navigate("formulario_usuario/-1") },
                    onEditar = { id -> nav.navigate("formulario_usuario/$id") },
                    onEliminar = { u -> usuarios.remove(u) }
                )
            }
            composable(
                "formulario_usuario/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { e ->
                val id = e.arguments?.getInt("id") ?: -1
                val existente = usuarios.firstOrNull { it.id == id }
                FormularioUsuarioScreen(
                    inicial = existente,
                    onCancelar = { nav.popBackStack() },
                    onGuardar = { nombre, correo, rol ->
                        if (existente != null) {
                            val i = usuarios.indexOf(existente)
                            usuarios[i] = existente.copy(nombre = nombre, correo = correo, rol = rol)
                        } else {
                            val nuevoId = (usuarios.maxOfOrNull { it.id } ?: 0) + 1
                            usuarios.add(Usuario(nuevoId, nombre, correo, rol))
                        }
                        nav.popBackStack()
                    }
                )
            }
            composable("productos") {
                MisProductosScreen(
                    productos = productos,
                    onNuevo = { nav.navigate("formulario_producto/-1") },
                    onEditar = { id -> nav.navigate("formulario_producto/$id") },
                    onEliminar = { p -> productos.remove(p) },
                    titulo = "Gestión de productos"
                )
            }
            composable(
                "formulario_producto/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { e ->
                val id = e.arguments?.getInt("id") ?: -1
                val existente = productos.firstOrNull { it.id == id }
                FormularioProductoScreen(
                    inicial = existente,
                    onCancelar = { nav.popBackStack() },
                    onGuardar = { nombre, marca, precio, categoria ->
                        if (existente != null) {
                            val i = productos.indexOf(existente)
                            productos[i] = existente.copy(
                                nombre = nombre, marca = marca,
                                precio = precio, categoria = categoria
                            )
                        } else {
                            val nuevoId = (productos.maxOfOrNull { it.id } ?: 0) + 1
                            productos.add(
                                Producto(nuevoId, nombre, marca, precio, categoria, paletaAdmin[nuevoId % paletaAdmin.size])
                            )
                        }
                        nav.popBackStack()
                    }
                )
            }
            composable("reportes") { ReportesScreen() }
        }
    }
}