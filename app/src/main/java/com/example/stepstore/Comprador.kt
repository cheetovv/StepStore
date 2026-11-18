package com.example.stepstore

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stepstore.ui.theme.Acento
import com.example.stepstore.ui.theme.TextoSecundario
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.navArgument

data class Destino(val ruta: String, val titulo: String, val icono: ImageVector)

private val destinosComprador = listOf(
    Destino("inicio", "Inicio", Icons.Default.Home),
    Destino("catalogo", "Catálogo", Icons.Default.Search),
    Destino("carrito", "Carrito", Icons.Default.ShoppingCart),
    Destino("perfil", "Perfil", Icons.Default.Person)
)

@Composable
fun CompradorScreen(onCerrarSesion: () -> Unit) {
    val navInterno = rememberNavController()
    val entrada by navInterno.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val carrito = remember { mutableStateListOf<ItemCarrito>() }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                destinosComprador.forEach { destino ->
                    NavigationBarItem(
                        selected = rutaActual == destino.ruta,
                        onClick = {
                            navInterno.navigate(destino.ruta) {
                                popUpTo(navInterno.graph.findStartDestination().id) {
                                    saveState = true
                                }
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
    ) { padding ->
        NavHost(
            navController = navInterno,
            startDestination = "inicio",
            modifier = Modifier.padding(padding)
        ) {
            composable("inicio") {
                InicioScreen(onProductoClick = { id -> navInterno.navigate("detalle/$id") })
            }
            composable("catalogo") {
                CatalogoScreen(onProductoClick = { id -> navInterno.navigate("detalle/$id") })
            }
            composable("carrito") {
                CarritoScreen(items = carrito, onPagar = { navInterno.navigate("pago") })
            }
            composable("perfil") { PerfilCompradorScreen(onCerrarSesion) }
            composable("pago") {
                PagoScreen(
                    total = carrito.sumOf { it.producto.precio * it.cantidad },
                    onVolver = { navInterno.popBackStack() },
                    onPagoExitoso = {
                        carrito.clear()
                        navInterno.navigate("inicio") {
                            popUpTo("inicio")
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(
                "detalle/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt("id") ?: 0
                val producto = productosEjemplo.first { it.id == id }
                DetalleScreen(
                    producto = producto,
                    onVolver = { navInterno.popBackStack() },
                    onAgregar = { p, talla ->
                        val i = carrito.indexOfFirst { it.producto.id == p.id && it.talla == talla }
                        if (i >= 0) carrito[i] = carrito[i].copy(cantidad = carrito[i].cantidad + 1)
                        else carrito.add(ItemCarrito(p, talla, 1))
                    }
                )
            }
        }
    }
}