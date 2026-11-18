# StepStore

Tienda virtual de calzado para Android, desarrollada de forma nativa con Kotlin y Jetpack Compose.

Proyecto de la asignatura **Desarrollo de Aplicaciones Móviles Nativas** (UCompensar, Tecnología en Desarrollo de Aplicaciones de Software).

> **Estado:** prototipo de interfaz. Los datos (productos, usuarios, pedidos) son de ejemplo y se pierden al cerrar la app. No hay servidor ni base de datos, y el pago es simulado.

## Qué incluye

**Transversal:** splash, inicio de sesión, ingreso con huella (BiometricPrompt), recuperación de contraseña y navegación con barra inferior.

| Rol | Pantallas |
| --- | --- |
| Comprador | Inicio, catálogo con búsqueda y filtros, detalle de producto, carrito, pasarela de pagos (simulada) y perfil |
| Vendedor | Productos (crear, editar, eliminar), pedidos recibidos y perfil |
| Administrador | Panel, usuarios (crear, editar, eliminar), productos y reporte de ventas |

## Tecnologías

- Kotlin
- Jetpack Compose y Material Design 3
- Navigation Compose
- AndroidX Biometric
- Android 8.0 (API 26) o superior

## Cómo ejecutarlo

1. Clona el repositorio.
2. Ábrelo con Android Studio y espera a que termine el *Gradle sync*.
3. Ejecuta la app en un emulador o en un celular con Android 8.0 o superior.

En el login no se validan credenciales: usa el botón del rol que quieras probar (Comprador, Vendedor o Administrador). Para probar la huella, el dispositivo necesita un bloqueo de pantalla y una huella registrada; en el emulador se simula desde los controles extendidos (**Fingerprint → Touch the sensor**).

## Estructura

```
app/src/main/java/com/example/stepstore/
├── MainActivity.kt
├── Navegacion.kt          # rutas y navegación principal
├── Autenticacion.kt       # splash, login, huella, recuperar contraseña
├── Productos.kt           # modelo de producto y tarjeta reutilizable
├── Comprador.kt           # marco del Comprador (barra inferior)
├── PantallasComprador.kt
├── Vendedor.kt            # marco del Vendedor
├── PantallasVendedor.kt
├── Admin.kt               # marco del Administrador
├── PantallasAdmin.kt
└── ui/theme/              # colores, tema y tipografía
```

## Autor

Andres Felipe Baraguera Ruda
