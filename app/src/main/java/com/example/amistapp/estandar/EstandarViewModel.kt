package com.example.amistapp.estandar

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.amistapp.Colecciones
import com.example.amistapp.Perfil
import com.example.amistapp.Usuario
import com.example.amistapp.UsuarioCompatibilidad
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow

class EstandarViewModel:ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val TAG = "Manuel"

    // Para la base de datos que contendrá  a los usuarios
    val db = Firebase.firestore

    private val _listadoUsuarios = mutableStateListOf<Usuario>()
    val listadoUsuarios: SnapshotStateList<Usuario> get() = _listadoUsuarios

    private val _listadoAmigos = mutableStateListOf<Usuario>()
    val listadoAmigos: SnapshotStateList<Usuario> get() = _listadoAmigos

    private val _listadoCompatibles = mutableStateListOf<UsuarioCompatibilidad>()
    val listadoCompatibles: SnapshotStateList<UsuarioCompatibilidad> get() = _listadoCompatibles

    private val _usuarioActual = mutableStateOf<Usuario?>(null)
    val usuarioActual: State<Usuario?> get() = _usuarioActual

    private val _fotoPerfil = mutableStateOf("")
    val fotoPerfil: State<String> get() = _fotoPerfil

    //Variables para los estados...
    val isLoading = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)
    val loginSuccess = MutableStateFlow(false)

    init {
        obtenerUsuarioActual()
    }

    fun obtenerUsuarios(){

        isLoading.value = true
        errorMessage.value = null
        db.collection(Colecciones.Usuarios)
            .get()
            .addOnSuccessListener { result ->
                val usuarios = result.documents.mapNotNull { doc ->
                    doc.toObject(Usuario::class.java)?.copy(idUsuario = doc.id)
                }
                // convierte la lista de usauraios s SnapshotStateList

                _listadoUsuarios.clear()// limpia la lista
                _listadoUsuarios.addAll(usuarios)// añade todos los usuarios a la lista
                isLoading.value = false
            }
            .addOnFailureListener { exception ->
                errorMessage.value = exception.message
                isLoading.value = false
            }
    }

    fun obtenerFotoPerfil(email: String){
        val usuariosRef = db.collection(Colecciones.Usuarios)

        usuariosRef.document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val perfil = document.get("perfil") as? Map<String, Any>
                    val fotoUrl = perfil?.get("fotoPerfil") as? String ?: ""

                    _fotoPerfil.value = fotoUrl // Actualiza el estado
                    Log.e("Izaskun", "Foto de perfil obtenida: $fotoUrl")
                } else {
                    Log.e("Izaskun", "No se encontró el perfil para el email: $email")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Izaskun", "Error al obtener la foto de perfil: $e")
            }
    }

    fun obtenerCompatibles() {
        Log.d(TAG, "Obteniendo compatibles...")
        val currentUser = auth.currentUser
        val email = currentUser?.email

        if (email == null) {
            Log.e(TAG, "No se encontró usuario autenticado.")
            return
        }

        isLoading.value = true

        // Obtener el documento del usuario actual
        db.collection(Colecciones.Usuarios).document(email)
            .get()
            .addOnSuccessListener { usuarioDoc ->
                val usuario = usuarioDoc.toObject(Usuario::class.java)
                val amigosIds = usuario?.perfil!!.amigos
                Log.d(TAG, "CompatiblesAmigosIds: $amigosIds")
                // Obtener lista de usuarios filtrando los amigos y el propio usuario
                db.collection(Colecciones.Usuarios)
                    .get()
                    .addOnSuccessListener { usuariosResult ->
                        val posibles = usuariosResult.documents.mapNotNull { doc ->
                            val usuario = doc.toObject(Usuario::class.java)?.copy(idUsuario = doc.id)
                            if (usuario != null && usuario.perfil!!.completado && usuario.activado && usuario.idUsuario !in amigosIds && usuario.email != email) {
                                usuario
                            } else {
                                null
                            }
                        }
                        Log.d(TAG, "Usuarios posibles: $posibles")
                        val compatibles = getCompatibilidad(posibles)
                        Log.d(TAG, "Usuarios compatibles: $compatibles")
                        _listadoCompatibles.clear()
                        _listadoCompatibles.addAll(compatibles)
                        isLoading.value = false
                    }
                    .addOnFailureListener { exception ->
                        errorMessage.value = exception.message
                        isLoading.value = false
                    }
            }
            .addOnFailureListener { exception ->
                errorMessage.value = exception.message
                isLoading.value = false
            }
    }


    fun obtenerAmigos(){
        Log.d(TAG, "Obteniendo amigos...")
        val currentUser = auth.currentUser
        val email = currentUser?.email

        if (email == null) {
            Log.e(TAG, "No se encontró usuario autenticado.")
            return
        }

        isLoading.value = true

        // Obtener el documento del usuario actual
        db.collection(Colecciones.Usuarios).document(email)
            .get()
            .addOnSuccessListener { usuarioDoc ->
                val usuario = usuarioDoc.toObject(Usuario::class.java)
                val amigosIds = usuario?.perfil!!.amigos
                Log.d(TAG, "AmigosIds: $amigosIds")

                // Obtener lista de usuarios filtrando los amigos y el propio usuario
                db.collection(Colecciones.Usuarios)
                    .get()
                    .addOnSuccessListener { usuariosResult ->
                        val amigos = usuariosResult.documents.mapNotNull { doc ->
                            val usuario = doc.toObject(Usuario::class.java)?.copy(idUsuario = doc.id)
                            if (usuario != null && usuario.perfil!!.completado && usuario.activado  && usuario.idUsuario in amigosIds && usuario.email != email) {
                                usuario
                            } else {
                                null
                            }
                        }
                        Log.d(TAG, "Usuarios amigos: $amigos")

                        _listadoAmigos.clear()
                        _listadoAmigos.addAll(amigos)
                        isLoading.value = false
                    }
                    .addOnFailureListener { exception ->
                        errorMessage.value = exception.message
                        isLoading.value = false
                    }
            }
            .addOnFailureListener { exception ->
                errorMessage.value = exception.message
                isLoading.value = false
            }
    }

    fun obtenerUsuarioActual() {
        val currentUser = auth.currentUser
        val email = currentUser?.email
        Log.d(TAG, "Email del usuario actual: $email")

        if (email == null) {
            Log.e(TAG, "No se encontró usuario autenticado.")
            return
        }

        isLoading.value = true

        db.collection(Colecciones.Usuarios)
            .whereEqualTo("email", email) // Filtrar por el email del usuario actual
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val usuario = result.documents.first().toObject(Usuario::class.java)
                    _usuarioActual.value = usuario
                    Log.d(TAG, "Usuario encontrado: $usuario")
                } else {
                    Log.e(TAG, "No se encontró el usuario en la base de datos.")
                }
                isLoading.value = false
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error al obtener el usuario: ${exception.message}")
                isLoading.value = false
            }
    }

    private fun getCompatibilidad(usuarios : List<Usuario>): List<UsuarioCompatibilidad> {

        var compatibles = mutableListOf<UsuarioCompatibilidad>()

        for (usuario in usuarios) {
            var compatibilidad= getCompatibilidad(usuarioActual.value!!.perfil!!, usuario.perfil!!)
            if (compatibilidad > 10) {
                compatibles.add(UsuarioCompatibilidad(usuario, compatibilidad))
            }
        }

        return compatibles
    }

    private fun getCompatibilidad(usuario1: Perfil, usuario2: Perfil): Int {
        var puntos = 0
        val maxPuntos = 90


        val diferenciaEdad = kotlin.math.abs(usuario1.edad - usuario2.edad)
        puntos += when {
            diferenciaEdad <= 2 -> 20
            diferenciaEdad <= 5 -> 15
            diferenciaEdad <= 10 -> 10
            else -> 5
        }


        puntos += (100 - kotlin.math.abs(usuario1.interesDeporte - usuario2.interesDeporte)) / 10
        puntos += (100 - kotlin.math.abs(usuario1.interesArte - usuario2.interesArte)) / 10
        puntos += (100 - kotlin.math.abs(usuario1.interesPolitica - usuario2.interesPolitica)) / 10


        if (usuario1.relacionSeria == usuario2.relacionSeria) {
            puntos += 10
        }

        if (usuario1.tieneHijos == usuario2.tieneHijos) puntos += 5
        if (usuario1.quiereHijos == usuario2.quiereHijos) puntos += 5


        if (usuario1.interesadoEn == "Ambos" || usuario1.interesadoEn == usuario2.genero) {
            puntos += 10
        }
        if (usuario2.interesadoEn == "Ambos" || usuario2.interesadoEn == usuario1.genero) {
            puntos += 10
        }

        Log.d(TAG, "Puntos: ${puntos} nick 1: ${usuario1.nick} nick 2: ${usuario2.nick}" )

        return (puntos * 100 / maxPuntos).coerceIn(0, 100)
    }

    fun buscarPeticionEntreUsuarios(usuarioLogueadoEmail: String, receptorEmail: String, callback: (Boolean) -> Unit) {

        db.collection(Colecciones.Peticiones)
            .whereEqualTo("emisor", usuarioLogueadoEmail)
            .whereEqualTo("receptor", receptorEmail)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    Log.d(TAG, "Petición encontrada: ${result.documents.first().id}")
                    callback(true) // Hay una petición en la primera dirección
                } else {
                    // Buscar en la dirección inversa
                    db.collection(Colecciones.Peticiones)
                        .whereEqualTo("emisor", receptorEmail)
                        .whereEqualTo("receptor", usuarioLogueadoEmail)
                        .get()
                        .addOnSuccessListener { result2 ->
                            if (!result2.isEmpty) {
                                Log.d(TAG, "Petición encontrada en la dirección inversa: ${result2.documents.first().id}")
                                callback(true) // Hay una petición en la dirección inversa
                            } else {
                                Log.d(TAG, "No hay peticiones entre los usuarios")
                                callback(false) // No hay peticiones
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error al obtener las peticiones en dirección inversa", e)
                            callback(false) // Devolver falso en caso de error
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al obtener las peticiones", e)
                callback(false) // Devolver falso en caso de error
            }
    }

    fun enviarPeticion(emisor: String, receptor: String) {
        val peticion = hashMapOf(
            "emisor" to emisor,
            "receptor" to receptor,
            "estado" to 0
        )

        db.collection(Colecciones.Peticiones)
            .add(peticion)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Petición enviada con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al enviar la petición", e)
            }
    }






}