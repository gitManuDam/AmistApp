package com.example.amistapp.Administrador.Eventos

import android.annotation.SuppressLint
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.amistapp.AsistenteEvento
import com.example.amistapp.Evento
import com.google.firebase.Timestamp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Locale

class EventoViewModel: ViewModel() {
    private val coleccion = "eventos"
    val TAG = "Izaskun"
    private val database = FirebaseDatabase.getInstance().getReference(coleccion)

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> get() = _eventos

    // Contendrá la lista de los inscritos al evento
    private val _inscritos = MutableStateFlow(mutableListOf<String>())
    val inscritos: StateFlow<List<String>> get() = _inscritos

    // Contendrá la lista de asistentes al evento
    private val _asistentes = MutableStateFlow(mutableListOf<AsistenteEvento>())
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

    fun setDescripcion(nuevaDescripcion: String) {
        if (nuevaDescripcion.isEmpty()) {
            _Error.value = "El campo no puede estar vacio"
        } else {
            _descripcion.value = nuevaDescripcion
            _Error.value = null
        }
    }

    // Añade un nuevo asistente a la lista de asistentes si no está añadido ya
    // recibe un objeto de tipo AsistenteEvento(email  y hora (para saber luego el orden de llegada))
    fun addAsistente(nuevoAsistente: AsistenteEvento) {
        if (_asistentes.value.any { it.email == nuevoAsistente.email }) {
            _Error.value = "El asistente ya está registrado"
        } else {
            _asistentes.value = (_asistentes.value + nuevoAsistente).toMutableList()
            _Error.value = null
        }
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
        _asistentes.value = (_asistentes.value + nuevoAsistente).toMutableList()
        _Error.value = null
    }

    // Devuelve una direccion para mostrar con la latitud y la longitud
    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun getDireccion(): String {
        val context = LocalContext.current
        val geocoder = Geocoder(context, Locale.getDefault())

        return try {
            val direcciones = geocoder.getFromLocation(latitud.value, longitud.value, 1)
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
            inscritos = if (_inscritos.value.isEmpty()) listOf("") else _inscritos.value,
            asistentes = if (_asistentes.value.isEmpty()) listOf(AsistenteEvento("", "")) else _asistentes.value
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

    fun limpiarDatos(){
        _latitud.value = 0.0
        _longitud.value = 0.0
        _fecha.value = LocalDate.now()
        _hora.value = LocalTime.now()
        _plazoInscripcion.value = LocalDate.now()
        _inscritos.value = mutableListOf()
        _asistentes.value = mutableListOf()
    }
}