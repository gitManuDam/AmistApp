package com.example.amistapp

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.amistapp.Login.LoginViewModel
import com.example.amistapp.ui.theme.AmistAppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.amistapp.DatosPerfil.DatosPerfilViewModel
import com.example.amistapp.DatosPerfil.VentanaDatosPerfil


class MainActivity : ComponentActivity() {
    val loginVM = LoginViewModel()
    val datosPerfilVM = DatosPerfilViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val contexto = LocalContext.current

            AmistAppTheme {
                val navController = rememberNavController()
                //Durante la creacion de la ventana Datos Perfil
                NavHost(navController = navController, startDestination = Rutas.perfil){
               // NavHost(navController = navController, startDestination = Rutas.login){
                    composable(Rutas.login){
                        LoginScreen(navController, loginVM)
                    }
                    composable(Rutas.estandar){

                    }
                    composable(Rutas.administrador){

                    }
                    composable(Rutas.perfil){
                        VentanaDatosPerfil(navController, loginVM, datosPerfilVM, contexto)
                    }
                }
            }
        }
    }
}

//https://firebase.google.com/docs/auth?hl=es-419
@Composable
fun LoginScreen(navController: NavHostController, loginVM: LoginViewModel) {
    val context = LocalContext.current

    val isLoading by loginVM.isLoading.collectAsState()
    val loginSuccess by loginVM.loginSuccess.collectAsState()
    val errorMessage by loginVM.errorMessage.collectAsState()

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var isRegistering by remember { mutableStateOf(false) } // Para alternar entre Login y Registro


    // lanza la pantalla con las cuentas de google para elegir
    // Una vez seleccionada la cuenta de google, obtiene el idToken para pasarlo a loginWithGoogle
    // que lo envia a firebase para autenticarse

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.result
            val idToken = account?.idToken
            if (idToken != null) {
                loginVM.loginWithGoogle(idToken)
            } else {
                Toast.makeText(context, "Error obteniendo token de Google", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun launchGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.client_id)) //Agrega tu client_id de Firebase, lo encontrarás en google-services.json, oauth-client/clinet_id
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isRegistering) "Registrar Cuenta" else "Iniciar Sesión",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isRegistering) {
                    loginVM.registerWithEmail(email.text, password.text)
                } else {
                    loginVM.loginWithEmail(email.text, password.text)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isRegistering) "Registrar" else "Iniciar Sesión")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { launchGoogleSignIn() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Iniciar Sesión con Google")
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { isRegistering = !isRegistering }) {
            Text(
                text = if (isRegistering) "¿Ya tienes cuenta? Inicia Sesión" else "¿No tienes cuenta? Regístrate"
            )
        }
//        TextButton(onClick = {
//            navController.navigate(Rutas.registro)
//        }) {
//            Text("¿No tienes cuenta? Regístrate")
//        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        LaunchedEffect(loginSuccess) { // comprobar si tiene el perfil completado !!
            if (loginSuccess) {
                Toast.makeText(context, "Login correcto", Toast.LENGTH_SHORT).show()
                // se guarda el email del usuario que se ha identificado en emailUsuario
                val emailUsuario = loginVM.getCurrentUser()?.email
                // si el usuario no está en la bd, lo añade
                if (!loginVM.existeUsuario()){
                    loginVM.addUsuario(emailUsuario!!)
                    // la primera vez le envia a rellenar el perfil
                    navController.navigate(Rutas.perfil){
                    }
                }
                // se guarda en role el role de usuario que se ha identificado
                // Ahora puedo tener dos roles !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                val role = loginVM.getRolePorEmail(emailUsuario!!)

                if (role == "estandar"){
                    navController.navigate(Rutas.estandar) {
                        popUpTo(Rutas.login) { inclusive = true } //Borra la pila de navegación
                    }
                }else if (role == "administrador"){
                    navController.navigate(Rutas.administrador) {
                        popUpTo(Rutas.login) { inclusive = true } //Borra la pila de navegación
                    }
                }

            }
        }
        /*
        LaunchedEffect evita múltiples ejecuciones

        LaunchedEffect está diseñado para manejar este tipo de situaciones porque:

            Se ejecuta una sola vez por cada cambio del valor clave (en este caso, loginSuccess).
            Una vez que el efecto se ejecuta, no se vuelve a ejecutar hasta que loginSuccess cambie nuevamente.

        Esto garantiza que las acciones, como mostrar un Toast o navegar, ocurran solo cuando realmente cambien las condiciones, evitando comportamientos inesperados debido a las recomposiciones.
         */
    }
}
