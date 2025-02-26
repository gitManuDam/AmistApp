package com.example.amistapp.Administrador.Eventos

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
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

//    private val _asistenteEvento = mutableStateOf(AsistenteEvento())
//    val asistenteEvento: State<AsistenteEvento>  get() = _asistenteEvento

//    // Estas dos variables conformarán AsistenteEvento
//    private val _EmailAsistenteEvento =  mutableStateOf("")
//    val emailAsitenteEvento: State<String> get() = _EmailAsistenteEvento
//
//    private val _horaAsistenteEvento= MutableStateFlow(Timestamp.now())
//    val horaAsistenteEvento: StateFlow<Timestamp> get() = _horaAsistenteEvento

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

    private fun observeEventos() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fechaActual = LocalDate.now()
                val nuevosEvent = snapshot.children.mapNotNull { it.getValue(Evento::class.java) }
                    .sortedByDescending { it.timestamp }
                // se guardan todos los eventos
                _eventos.value = nuevosEvent.toList()

                // se guardan solo los que la fecha de inscripcion es igual o mayor a la actual
                _proximosEventos.value = nuevosEvent.filter { evento ->
                    val fechaInscripcion =
                        LocalDate.parse(evento.plazoInscripcion) // Asegúrate de que la fecha está en formato YYYY-MM-DD
                    fechaInscripcion >= fechaActual
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error al escuchar los cambios en la base de datos", error.toException())
            }
        })

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

    // Devuelve una direccion para mostrar con la latitud y la longitud
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
            asistentes = _asistentes.value
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

    fun obtenerAsistentes(eventoId: String) {
        val eventoRef = database.child(eventoId).child("asistentes")

        eventoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = snapshot.children.mapNotNull { it.getValue(AsistenteEvento::class.java) }
                _asistentes.value = lista // Actualiza el MutableStateFlow con la nueva lista
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("EventoViewModel", "Error al cargar asistentes", error.toException())
            }
        })
    }

    fun asistirAlEvento(eventoId: String, emailLogeado:String,  context:Context){
        val eventoRef = database.child(eventoId).child("asistentes")
        val emailLogeado =
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
}