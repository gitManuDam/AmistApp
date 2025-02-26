package com.example.amistapp.Login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.amistapp.Parametros.Colecciones
import com.example.amistapp.R
import com.example.amistapp.Modelos.Usuario

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
// Autora: Izaskun
class LoginViewModel: ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val TAG = "Izaskun"

    // Para la base de datos que contendrá  a los usuarios
    val db = Firebase.firestore

    private val _usuarios = mutableStateListOf<Usuario>()
    val usuarios: List<Usuario> get() = _usuarios

    private val _email = mutableStateOf("")
    val email: State<String> get() = _email

    private val _role = mutableStateOf("")
    val role: State<String> get() = _role

    private val _Error = MutableLiveData<String?>()
    val Error : LiveData<String?> = _Error

    private val _activado= mutableStateOf(false)
    val activado:  State<Boolean> = _activado

    private val _completado= mutableStateOf(false)
    val completado:  State<Boolean> = _completado

    //Variables para los estados...
    val isLoading = MutableStateFlow(false)
    val loginSuccess = MutableStateFlow(false)
    val loginGoogleSuccess = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)

    val isUserLoggedIn: Boolean
    get() = auth.currentUser != null



    fun loginWithEmail(email: String, password: String) {
        isLoading.value = true
        errorMessage.value = null
        loginSuccess.value = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading.value = false
                if (task.isSuccessful) {
                    loginSuccess.value = true
                } else {
                    errorMessage.value = task.exception?.message ?: "Error desconocido"
                }
            }
    }

    fun registerWithEmail(email: String, password: String) {
        isLoading.value = true
        errorMessage.value = null
        loginSuccess.value = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading.value = false
                if (task.isSuccessful) {
                    loginSuccess.value = true
                } else {
                    errorMessage.value = task.exception?.message ?: "Error desconocido"
                }
            }
    }

    // Permite realizar el inicio de sesión de un usuario en Firebase utilizando Google Sing-In
    // idToken es el token de identificación proporcionado por Google despues de que el usuario
    // selecciona una cuenta de Google para la autenticación
    fun loginWithGoogle(idToken: String) {
        isLoading.value = true
        errorMessage.value = null
        loginSuccess.value = false

        // se utiliza el token proporcionado por Google para generar un objeto AuthCredential de Firebase
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        // Se llama al método singInWithCredential de Firebase Authentication para autenticar al usuario
        // con las credenciales obtenidas
        auth.signInWithCredential(credential)
            // Se verifican los resultados de la operación
            .addOnCompleteListener { task ->
                isLoading.value = false
                if (task.isSuccessful) {
                    loginSuccess.value = true
                    loginGoogleSuccess.value = true
                } else {
                    errorMessage.value = task.exception?.message ?: "Error desconocido"
                }
            }
    }


    fun signOut(context: Context) {
        Log.d(TAG, "signOut() llamado ${loginGoogleSuccess.value}")
        if (loginGoogleSuccess.value) {
            //El usuario inició sesión con Google
            val googleSignInClient = GoogleSignIn.getClient(
                context,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            )

            googleSignInClient.revokeAccess().addOnCompleteListener { revokeTask ->
                if (revokeTask.isSuccessful) {
                    Log.d(TAG, "Acceso revocado correctamente")
                    auth.signOut()
                    resetLoginState()
                    Log.d(TAG, "Sesión cerrada correctamente")
                } else {
                    Log.e(TAG, "Error al revocar el acceso")
                }
            }
        } else {
            //El usuario no inició sesión con Google (email/contraseña u otro proveedor)
            auth.signOut()
            Log.d(TAG, "Sesión cerrada para usuario no Google")
        }

        //Actualizar el estado de las variables de UI
        loginGoogleSuccess.value = false
        loginSuccess.value = false
    }

    private fun resetLoginState() {
        loginGoogleSuccess.value = false
        loginSuccess.value = false
        errorMessage.value = null
        _email.value = ""
        _role.value = ""
        _activado.value = false
        _completado.value = false
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun setEmail(nuevoEmail: String){
        if (nuevoEmail.isEmpty()){
            _Error.value= "El campo no puede estar vacio"
        }else{
            _email.value= nuevoEmail
            _Error.value = null
        }
    }

    fun setRole(nuevoRole: String){
        if (nuevoRole.isEmpty()){
            _Error.value= "El campo no puede estar vacio"
        }else{
            _role.value= nuevoRole
            _Error.value = null
        }
    }

    // esta función comprueba si existe algún usuario en la colección Usuarios con ese email
    suspend fun existeUsuario():Boolean{
        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email
        return try{
            val querySnapshot = db.collection(Colecciones.Usuarios)
                .whereEqualTo("email", email)
                .get()
                .await()
            !querySnapshot.isEmpty
        }catch (e: Exception){
            false
        }
    }

    suspend fun getRolesPorEmail(email: String):List<String>?{
        val usuariosRef = db.collection(Colecciones.Usuarios)
        val querySnapshot = usuariosRef
            .whereEqualTo("email", email)
            .get()
            .await()
        return if (querySnapshot.isEmpty){
            null
        }else{
            val usuario = querySnapshot.documents[0]
            usuario.get("role") as? List<String>
        }
    }

    suspend fun getActivadoPorEmail(email: String):Boolean{
        val usuariosRef = db.collection(Colecciones.Usuarios)
        val querySnapshot = usuariosRef
            .whereEqualTo("email", email)
            .get()
            .await()

        val activado = querySnapshot.documents.firstOrNull()?.getBoolean("activado") ?: false
        _activado.value = activado
        return activado

    }

    suspend fun getCompletadoPorEmail(email: String):Boolean{
        val usuariosRef = db.collection(Colecciones.Usuarios)
        val querySnapshot = usuariosRef
            .whereEqualTo("email", email)
            .get()
            .await()
        var completado:Boolean = false
        // el primero que coincide
        val usuarioBuscado = querySnapshot.documents.firstOrNull()
        if (usuarioBuscado != null){
            // obtiene el subdocumento
            val perfil = usuarioBuscado.get("perfil") as? Map<String, Any>
            completado = perfil?.get("completado") as? Boolean ?: false
            _completado.value = completado
        }

        return completado
    }

    // esta función añade un usuario a la bd si no existe ya
    fun addUsuario (email: String){
        val usuariosRef = db.collection(Colecciones.Usuarios)

        // se comprueba si existe el usuario en la bd
        // si ya existe no se añade
        usuariosRef
            .whereEqualTo("email", email)
           // .whereEqualTo("role", usuario.role)
            .get()
            .addOnSuccessListener { consulta ->
                if(!consulta.isEmpty){
                    Log.e("Izaskun", "El usuario ya existe")
                }else{
                    // perfil inicial
                    val perfilInicial = mapOf(
                        "completado" to false,
                        "nick" to "",
                        "fotoPerfil" to "",
                        "edad" to 0,
                        "amigos" to emptyList<String>(),
                        "relacionSeria" to false,
                        "interesDeporte" to 0,
                        "interesArte" to 0,
                        "interesPolitica" to 0,
                        "tieneHijos" to false,
                        "quiereHijos" to false,
                        "interesadoEn" to "",
                        "genero" to ""
                    )
                    val nuevoUsuario = hashMapOf(
                        "activado" to false,
                        "email" to email,
                        "role" to listOf("estandar"),
                        "perfil"  to perfilInicial
                    )
                    usuariosRef.document(email)
                        .set(nuevoUsuario)
                        .addOnSuccessListener { Log.e("Izaskun", "Usuario añadido con exito") }
                        .addOnFailureListener { Log.e("Izaskun", "Error al añadir el usuario") }
                }
            }
            .addOnFailureListener {  Log.e("Izaskun", "Error al consultar al usuario") }
    }

    // esta función añade un usuario a la bd si no existe ya y como le da de alta el administrador
    // ya está activado
    fun addUsuarioAdmin (email: String){
        val usuariosRef = db.collection(Colecciones.Usuarios)

        // se comprueba si existe el usuario en la bd
        // si ya existe no se añade
        usuariosRef
            .whereEqualTo("email", email)
            // .whereEqualTo("role", usuario.role)
            .get()
            .addOnSuccessListener { consulta ->
                if(!consulta.isEmpty){
                    Log.e("Izaskun", "El usuario ya existe")
                }else{
                    // perfil inicial
                    val perfilInicial = mapOf(
                        "completado" to false,
                        "nick" to "",
                        "fotoPerfil" to "",
                        "edad" to 0,
                        "amigos" to emptyList<String>(),
                        "relacionSeria" to false,
                        "interesDeporte" to 0,
                        "interesArte" to 0,
                        "interesPolitica" to 0,
                        "tieneHijos" to false,
                        "quiereHijos" to false,
                        "interesadoEn" to "",
                        "genero" to ""
                    )
                    val nuevoUsuario = hashMapOf(
                        "activado" to true,
                        "email" to email,
                        "role" to listOf("estandar"),
                        "perfil"  to perfilInicial
                    )
                    usuariosRef.document(email)
                        .set(nuevoUsuario)
                        .addOnSuccessListener { Log.e("Izaskun", "Usuario añadido con exito") }
                        .addOnFailureListener { Log.e("Izaskun", "Error al añadir el usuario") }
                }
            }
            .addOnFailureListener {  Log.e("Izaskun", "Error al consultar al usuario") }
    }

}