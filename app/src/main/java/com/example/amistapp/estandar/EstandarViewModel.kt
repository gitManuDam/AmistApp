package com.example.amistapp.estandar

import android.location.Location
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.amistapp.Colecciones
import com.example.amistapp.Modelos.Evento
import com.example.amistapp.Modelos.Usuario
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EstandarViewModel:ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val TAG = "Izaskun"

    private val coleccion = "eventos"
    private val database = FirebaseDatabase.getInstance().getReference(coleccion)

    // Para la base de datos que contendrá  a los usuarios
    val db = Firebase.firestore

    private val _listadoUsuarios = mutableStateListOf<Usuario>()
    val listadoUsuarios: SnapshotStateList<Usuario> get() = _listadoUsuarios

    private val _listadoAmigos = mutableStateListOf<Usuario>()
    val listadoAmigos: SnapshotStateList<Usuario> get() = _listadoAmigos

    private val _listadoCompatibles = mutableStateListOf<Usuario>()
    val listadoCompatibles: SnapshotStateList<Usuario> get() = _listadoCompatibles

    private val _misEventos = MutableStateFlow<List<Evento>>(emptyList())
    val misEventos: StateFlow<List<Evento>> get() = _misEventos

    private val _fotoPerfil = mutableStateOf("")
    val fotoPerfil: State<String> get() = _fotoPerfil

    private val _latitudUsuario = MutableStateFlow(0.0)
    val latitudUsuario: StateFlow<Double> get() = _latitudUsuario

    private val _longitudUsuario = MutableStateFlow(0.0)
    val longitudUsuario: StateFlow<Double> get() = _longitudUsuario

    private val _emailLogeado = mutableStateOf("")
    val emailLogeado: State<String> get() = _emailLogeado

    private val _Error = MutableLiveData<String?>()
    val Error: LiveData<String?> = _Error

    //Variables para los estados...
    val isLoading = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)
    val loginSuccess = MutableStateFlow(false)

    fun setLatitudUsuario(nuevaLatitud: Double) {
        _latitudUsuario.value = nuevaLatitud
        _Error.value = null
    }

    fun setLongitudUsuario(nuevaLongitud: Double) {
        _longitudUsuario.value = nuevaLongitud
        _Error.value = null
    }

    fun setEmailLogeado(emailLogeado: String) {
        _emailLogeado.value = emailLogeado
        _Error.value = null
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

    fun obtenerMisEventos(emailUsuario: String){
        database.get().addOnSuccessListener { me ->
            if (me.exists()) {
                val eventosFiltrados = me.children.mapNotNull { dataSnapshot ->
                    val evento = dataSnapshot.getValue(Evento::class.java)
                    // Filtra eventos donde el usuario está inscrito
                    evento?.takeIf { it.inscritos.contains(emailUsuario) }
                }

                _misEventos.value = eventosFiltrados // Asigna la lista filtrada a StateFlow
                Log.d("EstandarViewModel", "Eventos obtenidos con éxito: ${_misEventos.value.size}")
            } else {
                Log.d("EstandarViewModel", "No hay eventos en la base de datos")
                _misEventos.value = emptyList() // Asegura que el StateFlow se vacíe si no hay eventos
            }
        }.addOnFailureListener { exception ->
            Log.e("EstandarViewModel", "Error al obtener eventos inscritos", exception)
        }
    }

    fun fechaActualEsMenor (fechaEvento: String):Boolean{
        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val fechaEventoFormateada = LocalDate.parse(fechaEvento, formato)
        val fechaActual = LocalDate.now()

        return fechaActual.isBefore(fechaEventoFormateada)
    }

    fun suficienteCerca(latitudEvento: Double, longitudEvento:Double, latitudUsuario: Double, longitudUsuario:Double):Boolean{
        val eventoLocation = Location("Evento").apply {
            latitude = latitudEvento
            longitude = longitudEvento
        }

        val usuarioLocation = Location("Usuario").apply {
            latitude = latitudUsuario
            longitude = longitudUsuario
        }

        val distancia = eventoLocation.distanceTo(usuarioLocation)

        return distancia <= 20 //
    }
}


