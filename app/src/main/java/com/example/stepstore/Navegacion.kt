package com.example.stepstore

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val RECUPERAR = "recuperar"
    const val COMPRADOR = "comprador"
    const val VENDEDOR = "vendedor"
    const val ADMIN = "admin"
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) {
            SplashScreen(onTerminar = {
                nav.navigate(Rutas.LOGIN) { popUpTo(Rutas.SPLASH) { inclusive = true } }
            })
        }
        composable(Rutas.LOGIN) {
            LoginScreen(
                onRecuperar = { nav.navigate(Rutas.RECUPERAR) },
                onEntrar = { ruta ->
                    nav.navigate(ruta) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                }
            )
        }
        composable(Rutas.RECUPERAR) {
            RecuperarScreen(onVolver = { nav.popBackStack() })
        }
        composable(Rutas.COMPRADOR) {
            CompradorScreen(onCerrarSesion = {
                nav.navigate(Rutas.LOGIN) { popUpTo(Rutas.COMPRADOR) { inclusive = true } }
            })
        }
        composable(Rutas.VENDEDOR) {
            VendedorScreen(onCerrarSesion = {
                nav.navigate(Rutas.LOGIN) { popUpTo(Rutas.VENDEDOR) { inclusive = true } }
            })
        }
        composable(Rutas.ADMIN) {
            AdminScreen(onCerrarSesion = {
                nav.navigate(Rutas.LOGIN) { popUpTo(Rutas.ADMIN) { inclusive = true } }
            })
        }
    }
}

