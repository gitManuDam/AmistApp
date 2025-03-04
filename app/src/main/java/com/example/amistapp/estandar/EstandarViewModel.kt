package com.example.amistapp.estandar

import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.amistapp.MainActivity
import com.example.amistapp.Parametros.Colecciones

import com.example.amistapp.Modelos.Perfil

import com.example.amistapp.Modelos.UsuarioCompatibilidad
import com.example.amistapp.Modelos.Evento
import com.example.amistapp.Modelos.Peticion
import com.example.amistapp.Modelos.Usuario
import com.example.amistapp.R

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EstandarViewModel:ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val TAG = "Manuel"

    private val coleccion = "eventos"
    private val database = FirebaseDatabase.getInstance().getReference(coleccion)

    // Para la base de datos que contendrá  a los usuarios
    val db = Firebase.firestore

    private val _listadoUsuarios = mutableStateListOf<Usuario>()
    val listadoUsuarios: SnapshotStateList<Usuario> get() = _listadoUsuarios

    private val _listadoAmigos = mutableStateListOf<Usuario>()
    val listadoAmigos: SnapshotStateList<Usuario> get() = _listadoAmigos

    private val _listadoCompatibles = mutableStateListOf<UsuarioCompatibilidad>()
    val listadoCompatibles: SnapshotStateList<UsuarioCompatibilidad> get() = _listadoCompatibles

    private val _listadoPeticiones = mutableStateListOf<Peticion>()
    val listadoPeticiones: SnapshotStateList<Peticion> get() = _listadoPeticiones

    private val _usuarioActual = mutableStateOf<Usuario?>(null)
    val usuarioActual: State<Usuario?> get() = _usuarioActual

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


    init {
        obtenerUsuarioActual()
    }

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
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

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

        usuarioActual.value?.perfil?.let { perfilActual ->
            for (usuario in usuarios) {
                usuario.perfil?.let { perfilUsuario ->
                    val compatibilidad = getCompatibilidad(perfilActual, perfilUsuario)
                    if (compatibilidad > 10) {
                        compatibles.add(UsuarioCompatibilidad(usuario, compatibilidad))
                    }
                }
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

    fun mensajesPendientes(email: String, callback: (Boolean) -> Unit) {
        val mensajesRef = FirebaseDatabase.getInstance().getReference("chats")

        mensajesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var hayMensajesNoLeidos = false

                // Recorre todos los chats en la base de datos
                for (chatSnapshot in snapshot.children) {
                    // Obtiene los usuarios del chat
                    val usuarios = chatSnapshot.child("usuarios").children.map { it.value as String }

                    // Filtra los chats por el email proporcionado
                    if (usuarios.contains(email)) {
                        // Recorre todos los mensajes del chat
                        val mensajes = chatSnapshot.child("mensajes").children

                        for (mensajeSnapshot in mensajes) {
                            if (mensajeSnapshot.child("sender").value != email){
                                val leido = mensajeSnapshot.child("leido").getValue(Boolean::class.java) ?: true
                                if (!leido) {
                                    hayMensajesNoLeidos = true
                                    break
                                }
                            }

                        }
                    }

                    // Si se encuentra un mensaje no leído, no es necesario seguir buscando
                    if (hayMensajesNoLeidos) {
                        break
                    }
                }

                // Llama al callback con el resultado
                callback(hayMensajesNoLeidos)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al obtener mensajes pendientes: ${error.message}")
                callback(false)
            }
        })
    }

    fun sendNotificationBasic(context: Context){
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        var notification = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_no_leidos)
            .setContentTitle("Mensajes")
            .setContentText("Tienes mensajes sin leer")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager.notify(_name.value.hashCode(), notification)
    }

    fun enviarPeticion(emisor: String, receptor: String) {
        val peticion = hashMapOf(
            "emisor" to emisor,
            "receptor" to receptor

        )

        db.collection(Colecciones.Peticiones)
            .add(peticion)
            .addOnSuccessListener { documentReference ->
                val peticionId = documentReference.id // Obtén el ID generado por Firebase
                Log.d(TAG, "Petición enviada con ID: $peticionId")

                // Actualiza el campo "id" con el ID generado y lo guarda en Firebase
                val peticionActualizada = hashMapOf(
                    "id" to peticionId,
                    "emisor" to emisor,
                    "receptor" to receptor

                )

                // Ahora actualizamos el documento con el campo "id" actualizado
                documentReference.update(peticionActualizada as Map<String, Any>)
                    .addOnSuccessListener {
                        Log.d(TAG, "ID de la petición guardado correctamente")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error al guardar el ID de la petición", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al enviar la petición", e)
            }
    }

    fun obtenerPeticionesRecibidas() {
        Log.d(TAG, "Obteniendo peticiones recibidas...")
        db.collection(Colecciones.Peticiones)
            .whereEqualTo("receptor", auth.currentUser?.email)
            .get()
            .addOnSuccessListener { peticionesResult ->
                val peticiones = peticionesResult.documents.mapNotNull { doc ->

                    doc.toObject(Peticion::class.java)
                }
                _listadoPeticiones.clear()
                _listadoPeticiones.addAll(peticiones)
            }
            .addOnFailureListener {
                Log.e(TAG, "Error al obtener peticiones recibidas", it)
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

    fun aceptarPeticion(peticion: Peticion) {
        // Elimina la petición original de la colección Peticiones
        db.collection(Colecciones.Peticiones)
            .document(peticion.id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Petición aceptada correctamente")
                // Actualiza las listas de amigos después de aceptar la petición
                actualizarListasAmigos(peticion.emisor, peticion.receptor)

                // Busca la petición inversa (de receptor a emisor) y la elimina
                db.collection(Colecciones.Peticiones)
                    .whereEqualTo("emisor", peticion.receptor)
                    .whereEqualTo("receptor", peticion.emisor)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        // Si existe, elimina el documento de la colección Peticiones
                        val documentoPeticion = querySnapshot.documents.firstOrNull()
                        documentoPeticion?.reference?.delete()?.addOnSuccessListener {
                            Log.d(TAG, "Petición inversa eliminada correctamente")
                        }?.addOnFailureListener {
                            Log.e(TAG, "Error al eliminar la petición inversa", it)
                        }
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "Error al buscar la petición inversa", it)
                    }
            }
            .addOnFailureListener {
                Log.e(TAG, "Error al aceptar la petición", it)
            }
    }
    fun rechazarPeticion(peticion: Peticion) {
        db.collection(Colecciones.Peticiones)
            .document(peticion.id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Petición rechazada correctamente")

            }
            .addOnFailureListener {
                Log.e(TAG, "Error al aceptar la petición", it)
            }
    }
    fun actualizarListasAmigos(emisor: String, receptor: String) {
        Log.d(TAG, "Actualizando listas de amigos...")
        val emisorRef = db.collection(Colecciones.Usuarios).document(emisor)
        val receptorRef = db.collection(Colecciones.Usuarios).document(receptor)

        db.runTransaction { transaction ->
            val emisorSnapshot = transaction.get(emisorRef)
            val receptorSnapshot = transaction.get(receptorRef)


            if (emisorSnapshot.exists() && receptorSnapshot.exists()) {

                val listaAmigosEmisor = emisorSnapshot.get("perfil.amigos") as? MutableList<String> ?: mutableListOf()
                val listaAmigosReceptor = receptorSnapshot.get("perfil.amigos") as? MutableList<String> ?: mutableListOf()


                listaAmigosEmisor.add(receptor)
                listaAmigosReceptor.add(emisor)


                transaction.update(emisorRef, "perfil.amigos", listaAmigosEmisor)
                transaction.update(receptorRef, "perfil.amigos", listaAmigosReceptor)

                Log.d(TAG, "Listas de amigos actualizadas correctamente")
            } else {
                throw Exception("Uno de los usuarios no existe")
            }
        }
            .addOnSuccessListener {
                Log.d(TAG, "Transacción completada correctamente")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al actualizar las listas de amigos", e)
            }
    }


}



