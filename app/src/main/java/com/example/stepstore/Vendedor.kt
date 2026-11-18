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

private val destinosVendedor = listOf(
    Destino("productos", "Productos", Icons.Default.ShoppingCart),
    Destino("pedidos", "Pedidos", Icons.AutoMirrored.Filled.List),
    Destino("perfil", "Perfil", Icons.Default.Person)
)

private val paleta = listOf(Acento, Primario, Exito, Alerta, TextoSecundario)

@Composable
fun BarraInferior(nav: NavHostController, destinos: List<Destino>, rutaActual: String?) {
    NavigationBar(containerColor = Color.White) {
        destinos.forEach { destino ->
            NavigationBarItem(
                selected = rutaActual == destino.ruta,
                onClick = {
                    nav.navigate(destino.ruta) {
                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(destino.icono, contentDescription = destino.titulo) },
                label = { Text(destino.titulo) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Acento,
                    selectedTextColor = Acento,
                    indicatorColor = Color(0xFFFDE8DA),
                    unselectedIconColor = TextoSecundario,
                    unselectedTextColor = TextoSecundario
                )
            )
        }
    }
}

@Composable
fun VendedorScreen(onCerrarSesion: () -> Unit) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val misProductos = remember {
        mutableStateListOf<Producto>().apply { addAll(productosEjemplo.take(5)) }
    }
    val rutasConBarra = listOf("productos", "pedidos", "perfil")

    Scaffold(
        bottomBar = {
            if (rutaActual in rutasConBarra) BarraInferior(nav, destinosVendedor, rutaActual)
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "productos",
            modifier = Modifier.padding(padding)
        ) {
            composable("productos") {
                MisProductosScreen(
                    productos = misProductos,
                    onNuevo = { nav.navigate("formulario/-1") },
                    onEditar = { id -> nav.navigate("formulario/$id") },
                    onEliminar = { p -> misProductos.remove(p) }
                )
            }
            composable("pedidos") { PedidosScreen() }
            composable("perfil") { PerfilVendedorScreen(misProductos.size, onCerrarSesion) }
            composable(
                "formulario/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { e ->
                val id = e.arguments?.getInt("id") ?: -1
                val existente = misProductos.firstOrNull { it.id == id }
                FormularioProductoScreen(
                    inicial = existente,
                    onCancelar = { nav.popBackStack() },
                    onGuardar = { nombre, marca, precio, categoria ->
                        if (existente != null) {
                            val i = misProductos.indexOf(existente)
                            misProductos[i] = existente.copy(
                                nombre = nombre, marca = marca,
                                precio = precio, categoria = categoria
                            )
                        } else {
                            val nuevoId = (misProductos.maxOfOrNull { it.id } ?: 0) + 1
                            misProductos.add(
                                Producto(nuevoId, nombre, marca, precio, categoria, paleta[nuevoId % paleta.size])
                            )
                        }
                        nav.popBackStack()
                    }
                )
            }
        }
    }
}