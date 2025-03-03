package com.example.amistapp.Administrador.Eventos

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.amistapp.Modelos.AsistenteEvento
import com.example.amistapp.Modelos.Evento
import com.example.amistapp.Modelos.Usuario
import com.example.amistapp.Parametros.Colecciones
import com.example.amistapp.R
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.IOException
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
// Autora: Izaskun

class EventoViewModel: ViewModel() {
    private val coleccion = "eventos"
    val TAG = "Izaskun"
    private val database = FirebaseDatabase.getInstance().getReference(coleccion)

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> get() = _eventos

    private val _proximosEventos = MutableStateFlow<List<Evento>>(emptyList())
    val proximosEventos: StateFlow<List<Evento>> get() = _proximosEventos

    // Contendrá la lista de los inscritos al evento
    private val _inscritos = MutableStateFlow<List<String>>(emptyList())
    val inscritos: StateFlow<List<String>> get() = _inscritos

    // Contendrá la lista de asistentes al evento
    private val _asistentes = MutableStateFlow<List<AsistenteEvento>>(emptyList())
    val asistentes: StateFlow<List<AsistenteEvento>> get() = _asistentes

    private val _descripcion = mutableStateOf("")
    val descripcion: State<String> get() = _descripcion

    private val _latitud = MutableStateFlow(0.0)
    val latitud: StateFlow<Double> get() = _latitud

    private val _longitud = MutableStateFlow(0.0)
    val longitud: StateFlow<Double> get() = _longitud

    // Fecha: Solo la fecha sin la parte de la hora
    private val _fecha = MutableStateFlow(LocalDateTime.now().toLocalDate())
    val fecha: StateFlow<LocalDate> get() = _fecha

    // Hora: Solo la hora sin la parte de la fecha
    private val _hora = MutableStateFlow(LocalDateTime.now().toLocalTime())
    val hora: StateFlow<java.time.LocalTime> get() = _hora

    // Plazo de inscripción: Solo la fecha sin la parte de la hora
    private val _plazoInscripcion = MutableStateFlow(LocalDateTime.now().toLocalDate())
    val plazoInscripcion: StateFlow<LocalDate> get() = _plazoInscripcion

    private val _Error = MutableLiveData<String?>()
    val Error: LiveData<String?> = _Error

    private val _codigoError = MutableStateFlow(0)
    val codigoError: StateFlow<Int> get() = _codigoError

    private val _eventoId = mutableStateOf("")
    val eventoId: State<String> get() = _eventoId

    // Contendrá la lista de los inscritos al evento
    private val _fotos = MutableStateFlow<List<String>>(emptyList())
    val fotos: StateFlow<List<String>> get() = _fotos

    // para los archivos del storage, en este caso fotos
    val storage = Firebase.storage
    var storageRef = storage.reference /*
    Crea una referencia para subir, descargar o borrar un archivo, o para obtener o actualizar sus metadatos. Se puede decir que una referencia es un indicador
    que apunta a un archivo en la nube. Las referencias son livianas, por lo que puedes crear todas las que necesites. También se pueden reutilizar en varias operaciones.
    */

    private val _urlPfp = MutableLiveData<URL?>()
    val urlPfp: LiveData<URL?> = _urlPfp

    private val _imageUri = MutableLiveData<Uri>(Uri.EMPTY)
    val imageUri: LiveData<Uri> get() = _imageUri

    private val _imageFile = MutableLiveData<File?>()
    val imageFile: LiveData<File?> get() = _imageFile

    private val _isUploading = MutableLiveData<Boolean>(false)
    val isUploading: LiveData<Boolean> get() = _isUploading

    private val _uploadSuccess = MutableLiveData<Boolean>()
    val uploadSuccess: LiveData<Boolean> get() = _uploadSuccess

    private val _fileList = MutableLiveData<List<String>>(emptyList())
    val fileList: LiveData<List<String>> get() = _fileList

    fun updateImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    fun setImageFile(file: File) {
        _imageFile.value = file
    }

    fun setUrlPfp(url: URL) {
        this._urlPfp.value = url
    }



    fun setLatitud(nuevaLatitud: Double) {
       _latitud.value = nuevaLatitud
        _Error.value = null
    }

    fun setLongitud(nuevaLongitud: Double) {
        _longitud.value = nuevaLongitud
        _Error.value = null
    }

    fun setFecha(nuevaFecha: LocalDate) {
        _fecha.value = nuevaFecha
    }

    fun setHora(nuevaHora: LocalTime) {
        _hora.value = nuevaHora
    }

    fun setPlazoInscripcion(nuevoPlazoIns: LocalDate) {
        _plazoInscripcion.value = nuevoPlazoIns
        _Error.value = null
    }

    fun setEventoId(nuevoId:String){
        _eventoId.value = nuevoId
    }

    fun setDescripcion(nuevaDescripcion: String) {
        if (nuevaDescripcion.isEmpty()) {
            _Error.value = "El campo no puede estar vacio"
        } else {
            _descripcion.value = nuevaDescripcion
            _Error.value = null
        }
    }

    init {
        observeEventos()
    }

    //Subir la imagen a Storage.
    //Para subir un archivo a Cloud Storage, primero debes crear una referencia a la ruta de acceso completa del archivo, incluido el nombre.
    //https://firebase.google.com/docs/storage/android/upload-files?hl=es-419
    fun uploadImage(context: Context) {
        val file = _imageFile.value ?: return
        val fileUri = Uri.fromFile(file)
        val ref = storageRef.child("images/${fileUri.lastPathSegment}")

        _isUploading.value = true
        ref.putFile(fileUri)
            .addOnSuccessListener {
                _isUploading.value = false
                _uploadSuccess.value = true
                Toast.makeText(context, context.getString(R.string.pfp_updated), Toast.LENGTH_SHORT).show()
                loadFileList()
                Log.d(TAG, "Imagen subida correctamente. URL de la misma: ${ref.downloadUrl}")
            }
            .addOnFailureListener { exception ->
                _isUploading.value = false
                _uploadSuccess.value = false
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    //  Se encarga de realizar dos tareas principales:
    //Subir una imagen a Firebase Storage.
    //Guardar la URL de la imagen subida en la base de datos de Firebase Realtime Database dentro de un
    // evento dado, en su lista de fotos.
    fun uploadImageAndSaveToEvent(context: Context, eventoId: String) {
        val file = _imageFile.value ?: return
        val fileUri = Uri.fromFile(file)
        val ref = storageRef.child("images/${fileUri.lastPathSegment}")

        _isUploading.value = true

        Log.e("Evento", "El id del evento es $eventoId.")

        ref.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                //  URL de la imagen subida
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()  //  URL a guardar

                    // se guarda  la URL en el evento correspondiente
                    val eventoRef = database.child(eventoId)

                    eventoRef.get().addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            // Obtiene las fotos existentes
                            val fotosExistentes = snapshot.child("fotos").children
                                .mapNotNull { it.getValue(String::class.java) }
                                .toMutableList()

                            // Agrega la nueva URL a la lista de fotos
                            fotosExistentes.add(imageUrl)

                            // Guarda la lista de fotos actualizada
                            eventoRef.child("fotos").setValue(fotosExistentes)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        _isUploading.value = false
                                        _uploadSuccess.value = true
                                        Toast.makeText(context, "Imagen subida y guardada en el evento", Toast.LENGTH_SHORT).show()
                                        Log.d(TAG, "Imagen subida y URL guardada correctamente en el evento")
                                    } else {
                                        _isUploading.value = false
                                        _uploadSuccess.value = false
                                        Toast.makeText(context, "Error al guardar la foto en el evento", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Log.e("Evento", "El evento no existe en la base de datos.")
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                _isUploading.value = false
                _uploadSuccess.value = false
                Toast.makeText(context, "Error al subir la imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    //sube una imagen a Firebase Storage, obtiene la URL de la imagen subida y
    // luego guarda esa URL en el perfil de un usuario en Firestore.
    fun uploadImageAndSaveToPerfil(context: Context, usuario: Usuario?) {
        val file = _imageFile.value ?: return
        val fileUri = Uri.fromFile(file)
        val ref = storageRef.child("profile_images/${fileUri.lastPathSegment}")

        _isUploading.value = true

        usuario?.let { user ->
            ref.putFile(fileUri)
                .addOnSuccessListener { taskSnapshot ->
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()  // URL a guardar

                        // Referencia al documento del usuario en Firestore
                        val userRef = FirebaseFirestore.getInstance().collection(Colecciones.Usuarios).document(user.email)

                        userRef.get().addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                val perfil = snapshot.get("perfil") as? Map<String, Any> ?: emptyMap()
                                val fotosExistentes = (perfil["fotos"] as? List<String>)?.toMutableList() ?: mutableListOf()

                                // Agregar la nueva foto a la lista
                                fotosExistentes.add(imageUrl)

                                // Actualizar el campo "perfil.fotos"
                                userRef.update("perfil.fotos", fotosExistentes)
                                    .addOnCompleteListener { task ->
                                        _isUploading.value = false
                                        if (task.isSuccessful) {
                                            _uploadSuccess.value = true
                                            Toast.makeText(context, "Imagen subida y guardada en el perfil", Toast.LENGTH_SHORT).show()
                                            Log.d(TAG, "Imagen subida y URL guardada correctamente en el perfil del usuario")
                                        } else {
                                            _uploadSuccess.value = false
                                            Toast.makeText(context, "Error al guardar la foto en el perfil", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    _isUploading.value = false
                    _uploadSuccess.value = false
                    Toast.makeText(context, "Error al subir la imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    //Carga la lista de archivos desde Firebase Storage.
    fun loadFileList() {
        storageRef.child("images").listAll()
            .addOnSuccessListener { result ->
                val urls = mutableListOf<String>()
                val tasks = result.items.map { ref ->
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        urls.add(uri.toString())
                        if (urls.size == result.items.size) {
                            _fileList.value = urls
                        }
                    }
                }
                tasks.forEach { it }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error al cargar la lista: ${exception.message}")
            }
    }

    // Borra fichero (en este caso será foto) del storage
    fun deleteFile(fileUrl: String, context: Context) {
        val ref = storage.getReferenceFromUrl(fileUrl)
        ref.delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Archivo eliminado correctamente", Toast.LENGTH_SHORT).show()
                loadFileList() //Recargamos la lista de archivos tras eliminar uno.
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error al eliminar archivo: ${exception.message}")
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Borra la foto de la lista de fotos del evento
    fun deleteImageFromEvent(eventoId: String, fileUrl: String, context: Context) {
        val eventoRef = database.child(eventoId)

        // Obtiene las fotos existentes
        eventoRef.child("fotos").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {

                val fotosExistentes = snapshot.value as? List<String> ?: emptyList()
                val fotosActualizadas = fotosExistentes.toMutableList()

                // Elimina la URL de la foto de la lista
                if (fotosActualizadas.remove(fileUrl)) {
                    // Actualizar la lista de fotos en la base de datos
                    eventoRef.child("fotos").setValue(fotosActualizadas)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Recarga la lista de fotos del evento
                                obtenerFotosDelEvento(eventoId)
                                Toast.makeText(context, "Imagen eliminada del evento", Toast.LENGTH_SHORT).show()
                                Log.d("EliminarFoto", "Imagen eliminada correctamente de Realtime Database")
                            } else {
                                Toast.makeText(context, "Error al eliminar la foto del evento", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "La imagen no existe en el evento", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No se encontraron fotos en el evento", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error al obtener datos del evento", Toast.LENGTH_SHORT).show()
        }
    }


    //Actualiza la URI de la imagen seleccionada
    fun selectImage(uri: String) {
        _imageUri.value = Uri.parse(uri)
    }


    // observa cambios en los datos de Firebase Realtime Database y filtra los eventos
    // según su fecha de inscripción, guardándolos en dos listas: una para todos los eventos y
    // otra solo para los próximos eventos cuya fecha de inscripción sea igual o mayor a la fecha actual.
    private fun observeEventos() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fechaActual = LocalDate.now()
                val nuevosEvent = snapshot.children.mapNotNull { it.getValue(Evento::class.java) }
                    .sortedByDescending { it.timestamp }
                // se guardan todos los eventos
                _eventos.value = nuevosEvent.toList()


                // Se guardan solo los que la fecha de inscripción es igual o mayor a la actual
                _proximosEventos.value = nuevosEvent.filter { evento ->
                    val fechaInscripcionString = evento.plazoInscripcion
                    // Comprobar si la fecha no está vacía ni nula
                    if (!fechaInscripcionString.isNullOrEmpty()) {
                        val fechaInscripcion = LocalDate.parse(fechaInscripcionString)
                        fechaInscripcion >= fechaActual
                    } else {
                        // Si la fecha está vacía o nula, puedes decidir qué hacer (por ejemplo, no incluir el evento)
                        false
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error al escuchar los cambios en la base de datos", error.toException())
            }
        })

    }

    //Recupera las URLs de las fotos asociadas a un evento desde la base de datos de Firebase
    // Realtime Database y luego actualiza un StateFlow con la lista de fotos obtenidas.
    fun obtenerFotosDelEvento(eventoId: String) {
        val eventoRef = database.child(eventoId)

        eventoRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Extraemos las URLs de las fotos del evento
                val fotos = snapshot.child("fotos").children.mapNotNull { it.getValue(String::class.java) }
                // Actualizamos el StateFlow con las fotos obtenidas
                _fotos.value = fotos
            } else {
                Log.e(TAG, "El evento con ID $eventoId no existe.")
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error al obtener las fotos: ${exception.message}")
        }
    }

    fun addInscrito(nuevoInscrito: String) {
        _inscritos.value = _inscritos.value + nuevoInscrito
    }

    // Añade un nuevo asistente a la lista de asistentes si no está añadido ya
    // recibe un objeto de tipo AsistenteEvento(email  y hora (para saber luego el orden de llegada))
    fun addAsistente(nuevoAsistente: AsistenteEvento) {
        _asistentes.value = _asistentes.value + nuevoAsistente
    }

    // se le pasa solo el email  y lo añade con la hora actual
    fun addAsistentePorEmail(email: String) {
        if (email.isEmpty()) {
            _Error.value = "El email no puede estar vacío"
            return
        }
        if (_asistentes.value.any { it.email == email }) {
            _Error.value = "El asistente ya está registrado"
            return
        }
        val nuevoAsistente = AsistenteEvento(email, LocalTime.now().toString())
    }

    // Devuelve una dirección para mostrar con la latitud y la longitud elegidas, es decir,
    // convierte en una dirección legible la latitud y la longitud
    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun getDireccion(latitud: Double, longitud: Double): String {
        val context = LocalContext.current
        val geocoder = Geocoder(context, Locale.getDefault())

        return try {
            val direcciones = geocoder.getFromLocation(latitud, longitud, 1)
            if (direcciones != null && direcciones.isNotEmpty()) {
                direcciones[0].getAddressLine(0) // Dirección completa
            } else {
                "Dirección no encontrada"
            }
        } catch (e: IOException) {
            "Error al obtener la dirección"
        }
    }

    //Agrega un nuevo evento a la base de datos de Firebase Realtime Database.
    fun addEvento() {
        val nuevoEvento = Evento(
            id = null,
            descripcion = descripcion.value,
            latitud = _latitud.value,
            longitud = _longitud.value,
            fecha = _fecha.value.toString(),
            hora = _hora.value.toString(),
            plazoInscripcion = _plazoInscripcion.value.toString(),
            inscritos = _inscritos.value,
            asistentes = _asistentes.value,
            fotos = _fotos.value
//            inscritos = if (_inscritos.value.isEmpty()) listOf("") else _inscritos.value,
//            asistentes = if (_asistentes.value.isEmpty()) listOf(AsistenteEvento("", "")) else _asistentes.value
        )

        val newEventoId = database.push().key
        if (newEventoId != null) {
            nuevoEvento.id = newEventoId
            database.child(newEventoId).setValue(nuevoEvento)
                .addOnCompleteListener { ne ->
                    if (!ne.isSuccessful) {
                        Log.e(TAG, "Error al enviar el evento", ne.exception)
                    } else {
                        Log.e(TAG, "Evento enviado con éxito")
                        limpiarDatos()
                    }
                }
        }
    }

    fun limpiarDatos() {
        _latitud.value = 0.0
        _longitud.value = 0.0
        _fecha.value = LocalDate.now()
        _hora.value = LocalTime.now()
        _plazoInscripcion.value = LocalDate.now()
        _inscritos.value = mutableListOf()
        _asistentes.value = mutableListOf()
    }

    // Elimina de la base de datos un evento pasado por parámetro
    fun eliminarEvento(evento: Evento) {

        val eventoRef = database.child(evento.id!!)

        eventoRef.removeValue()
            .addOnCompleteListener { ee ->
                if (ee.isSuccessful) {
                    Log.e(TAG, "Evento eliminado con éxito")
                } else {
                    Log.e(TAG, "Error al eliminar el evento", ee.exception)
                }
            }
    }

    // Permite a un usuario inscribirse en un evento concreto
    fun incribirseEnEvento(eventoId: String, emailUsuario: String, context: Context) {
        val eventoRef = database.child(eventoId)

        eventoRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val evento = snapshot.getValue(Evento::class.java)
                evento?.let {
                    val listaActualizada = it.inscritos?.toMutableList() ?: mutableListOf()

                    if (!listaActualizada.contains(emailUsuario)) { // Evita duplicados
                        listaActualizada.add(emailUsuario)

                        // Sube la lista actualizada a Firebase
                        eventoRef.child("inscritos").setValue(listaActualizada)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("Evento", "Usuario inscrito con éxito")
                                    Toast.makeText(context, "Te has inscrito en el evento", Toast.LENGTH_SHORT).show()

                                    _inscritos.value = listaActualizada
                                } else {
                                    Log.e("Evento", "Error al inscribirse", task.exception)
                                }
                            }
                    } else {
                        Log.d("Evento", "El usuario ya está inscrito")
                        Toast.makeText(context, "Ya estás inscrito en este evento", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.e("Evento", "El evento no existe")
            }
        }.addOnFailureListener { exception ->
            Log.e("Evento", "Error al obtener el evento", exception)
        }
    }

    // Obtiene la lista de las personas inscritas a un evento determinado
    fun obtenerInscritos(eventoId: String){
        val eventoRef = database.child(eventoId).child("inscritos")

        eventoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(oi: DataSnapshot) {
                val lista = oi.children.mapNotNull { it.getValue(String::class.java) }
                _inscritos.value = lista // Actualiza el MutableStateFlow
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("EventoViewModel", "Error al cargar inscritos", error.toException())
            }
        })
    }

    // Obtiene la lista de asistente a un evento determinado
    fun obtenerAsistentes(eventoId: String) {
        val eventoRef = database.child(eventoId).child("asistentes")

        eventoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = snapshot.children.mapNotNull { it.getValue(AsistenteEvento::class.java) }
                Log.e(TAG, "id del evento en obtener asistentes es: $eventoId")
                Log.e(TAG, "Asistentes obtenidos: $lista")
                _asistentes.value = lista // Actualiza el MutableStateFlow con la nueva lista
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("EventoViewModel", "Error al cargar asistentes", error.toException())
            }
        })
    }


    // Permite a un usuario unirse a un evento como asistente.
    // Esto implica agregar al usuario a la lista de asistentes del evento y
    // almacenar esta información en Firebase.
    fun asistirAlEvento(eventoId: String, emailLogeado:String,  context:Context){
        val eventoRef = database.child(eventoId).child("asistentes")

        eventoRef.get().addOnSuccessListener { ae ->
            val asistentesActuales = mutableListOf<AsistenteEvento>()

            // se comprueba si la lista de asistentes existe y no está vacía
            if (ae.exists() && ae.childrenCount > 0) {
                for (child in ae.children) {
                    val asistente = child.getValue(AsistenteEvento::class.java)
                    if (asistente != null) {
                        asistentesActuales.add(asistente)
                    }
                }
            }

            // el nuevo asistente con la hora actual
            val horaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            val nuevoAsistente = AsistenteEvento(emailLogeado, horaActual)

            // se añade el nuevo asistente a la lista
            asistentesActuales.add(nuevoAsistente)

            // Subir la lista actualizada a Firebase
            eventoRef.setValue(asistentesActuales)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Te has unido al evento", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al unirse al evento", Toast.LENGTH_SHORT).show()
                    }
                }
        }.addOnFailureListener {
            Toast.makeText(context, "Error al obtener los asistentes", Toast.LENGTH_SHORT).show()
        }
    }

    // Comprueba si el plazo de inscripción está abierot
    fun plazoInscripcionAbierto():Boolean{
        val eventoId = eventoId.value
        val evento = database.child(eventoId).get().result?.getValue(Evento::class.java)

        return if (evento != null) {
            val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val fechaPlazo = LocalDate.parse(evento.plazoInscripcion, formato)
            val fechaHoy = LocalDate.now()

            fechaHoy.isBefore(fechaPlazo) || fechaHoy.isEqual(fechaPlazo)
        } else {
            false
        }
    }

    // Comprueba si un usuario determinado está inscrito a un evento determinado
    fun estaInscrito(eventoId: String, emailUsuario: String, onResultado: (Boolean) -> Unit) {

        val eventoRef = database.child(eventoId).child("inscritos")

        eventoRef.get().addOnSuccessListener { ea ->
            if (ea.exists()) {
                val listaInscritos = ea.children.mapNotNull { it.getValue(String::class.java) }
                onResultado(listaInscritos.contains(emailUsuario))
            } else {
                onResultado(false)
            }
        }.addOnFailureListener {
            Log.e("Evento", "Error al verificar inscripción", it)
            onResultado(false)
        }

    }

    // Comprueba si un usuario determinado asistió o no a un evento determinado
    fun asistio(eventoId: String, emailUsuario: String, onResultado: (Boolean) -> Unit) {

        val eventoRef = database.child(eventoId).child("asistentes")

        eventoRef.get().addOnSuccessListener { ea ->
            if (ea.exists()) {
                val listaAsistentes = ea.children.mapNotNull { it.getValue(AsistenteEvento::class.java) }
                onResultado(listaAsistentes.any { it.email == emailUsuario }) // Verifica si el email está en la lista
            } else {
                onResultado(false)
            }
        }.addOnFailureListener {
            Log.e("Evento", "Error al verificar inscripción", it)
            onResultado(false)
        }

    }

    // Elimina de la lista de inscritos a un usuario determinado
    fun eliminarInscrito(emailInscrito: String) {

        Log.e(TAG, "Intentando eliminar al asistente con email: $emailInscrito")

        val eventoId = _eventoId.value
//        val eventoRef = database.child("eventos").child(eventoId)
        val eventoRef = database.child(eventoId)
        Log.e(TAG, "Intentando eliminar usuario con email: $emailInscrito del evento: ${eventoId}")
        Log.e(TAG, "el evento es : $eventoId")

        eventoRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val eventoActualizado = snapshot.getValue(Evento::class.java)

                eventoActualizado?.let {
                    val listaInscritos = it.inscritos?.toMutableList() ?: mutableListOf()
                    Log.e(TAG, "Lista de inscritos antes de eliminar: $listaInscritos")

                    if (listaInscritos.contains(emailInscrito)) {
                        listaInscritos.remove(emailInscrito)

                        eventoRef.child("inscritos").setValue(listaInscritos)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "Usuario eliminado con éxito")
                                    _inscritos.value = listaInscritos
                                } else {
                                    Log.e(TAG, "Error al actualizar Firebase", task.exception)
                                }
                            }
                    } else {
                        Log.d(TAG, "El usuario no está inscrito en este evento")
                    }
                }
            } else {
                Log.e(TAG, "El evento no existe en Firebase")
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error al obtener el evento", exception)
        }
    }

    fun obtenerFotosDelUsuario(usuario: Usuario?) {
        _fotos.value = usuario?.perfil?.fotos ?: emptyList()
    }
}