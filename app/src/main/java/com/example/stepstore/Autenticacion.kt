package com.example.stepstore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.stepstore.ui.theme.Acento
import com.example.stepstore.ui.theme.TextoSecundario
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.stepstore.ui.theme.Primario
import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.stepstore.ui.theme.Alerta

var ultimaRutaUsada = Rutas.COMPRADOR

private fun Context.buscarActivity(): FragmentActivity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is FragmentActivity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

private fun pedirHuella(
    activity: FragmentActivity,
    onExito: () -> Unit,
    onMensaje: (String) -> Unit
) {
    val permitido = BiometricManager.Authenticators.BIOMETRIC_WEAK
    if (BiometricManager.from(activity).canAuthenticate(permitido) != BiometricManager.BIOMETRIC_SUCCESS) {
        onMensaje("Este dispositivo no tiene una huella registrada")
        return
    }
    val prompt = BiometricPrompt(
        activity,
        ContextCompat.getMainExecutor(activity),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onExito()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                    errorCode != BiometricPrompt.ERROR_USER_CANCELED
                ) onMensaje(errString.toString())
            }
        }
    )
    val info = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Ingresar a StepStore")
        .setSubtitle("Usa tu huella para continuar")
        .setNegativeButtonText("Cancelar")
        .setAllowedAuthenticators(permitido)
        .build()
    prompt.authenticate(info)
}
@Composable
fun SplashScreen(onTerminar: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onTerminar()
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Primario),
        contentAlignment = Alignment.Center
    ) {
        Text("StepStore", color = Acento, style = MaterialTheme.typography.displayMedium)
    }
}

@Composable
fun LoginScreen(onRecuperar: () -> Unit, onEntrar: (String) -> Unit) {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    val activity = LocalContext.current.buscarActivity()

    fun entrar(ruta: String) {
        ultimaRutaUsada = ruta
        onEntrar(ruta)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = correo, onValueChange = { correo = it },
            label = { Text("Correo") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = clave, onValueChange = { clave = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        TextButton(onClick = onRecuperar) { Text("¿Olvidaste tu contraseña?") }

        Button(onClick = { entrar(Rutas.COMPRADOR) }, modifier = Modifier.fillMaxWidth()) {
            Text("Entrar como Comprador")
        }
        OutlinedButton(onClick = { entrar(Rutas.VENDEDOR) }, modifier = Modifier.fillMaxWidth()) {
            Text("Entrar como Vendedor")
        }
        OutlinedButton(onClick = { entrar(Rutas.ADMIN) }, modifier = Modifier.fillMaxWidth()) {
            Text("Entrar como Administrador")
        }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                mensaje = ""
                if (activity != null) {
                    pedirHuella(
                        activity = activity,
                        onExito = { entrar(ultimaRutaUsada) },
                        onMensaje = { mensaje = it }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Lock, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Ingresar con huella")
        }
        if (mensaje.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(mensaje, color = Alerta)
        }
    }
}

@Composable
fun RecuperarScreen(onVolver: () -> Unit) {
    var correo by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Recuperar contraseña", style = MaterialTheme.typography.headlineMedium)
        Text("Te enviaremos un enlace a tu correo.", color = TextoSecundario)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = correo, onValueChange = { correo = it },
            label = { Text("Correo") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Enviar enlace")
        }
        TextButton(onClick = onVolver) { Text("Volver") }
    }
}

@Composable
fun PantallaTemporal(titulo: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pantalla de $titulo", style = MaterialTheme.typography.headlineMedium)
    }
}
