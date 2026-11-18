package com.example.stepstore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
data class Producto(
    val id: Int,
    val nombre: String,
    val marca: String,
    val precio: Int,
    val categoria: String,
    val color: Color
)

val productosEjemplo = listOf(
    Producto(1, "Air Runner", "Nike", 389000, "Deportivo", Color(0xFFF26B21)),
    Producto(2, "Ultra Boost", "Adidas", 459000, "Deportivo", Color(0xFF1F2A44)),
    Producto(3, "Classic Leather", "Reebok", 279000, "Casual", Color(0xFF2E9E6B)),
    Producto(4, "Old Skool", "Vans", 249000, "Casual", Color(0xFFD64545)),
    Producto(5, "Oxford Cuero", "Bosi", 319000, "Formal", Color(0xFF6B7280)),
    Producto(6, "Mocasín Clásico", "Gino", 289000, "Formal", Color(0xFF1F2A44)),
    Producto(7, "Pegasus", "Nike", 429000, "Deportivo", Color(0xFFF26B21)),
    Producto(8, "Chuck Taylor", "Converse", 269000, "Casual", Color(0xFFD64545))
)

fun formatoPrecio(precio: Int): String =
    "\$" + "%,d".format(precio).replace(',', '.')

@Composable
fun ProductoCard(producto: Producto, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(producto.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                producto.marca.take(1),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = producto.color
            )
        }
        Column(Modifier.padding(12.dp)) {
            Text(producto.marca, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
            Text(producto.nombre, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(formatoPrecio(producto.precio), color = Acento, fontWeight = FontWeight.Bold)
        }
    }
}