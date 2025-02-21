package com.example.amistapp.Administrador.Eventos

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavHostController
import com.example.amistapp.DatosPerfil.DatosPerfilViewModel

import com.example.amistapp.R
import com.example.amistapp.Rutas
import androidx.compose.runtime.collectAsState
import com.google.firebase.perf.util.Timer


import java.time.LocalDate
import java.time.LocalTime
// Autora: Izaskun
@Composable
fun CrearEvento(navController: NavHostController, eventoVM: EventoViewModel){
    Column(modifier = Modifier
        .padding(vertical = 20.dp)
        .systemBarsPadding()) {

        Spacer(modifier = Modifier.height(50.dp))
        descripcion(eventoVM)
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Espaciado alrededor de la fila
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Espaciado entre los elementos en la fila
            verticalAlignment = Alignment.CenterVertically // Alineación vertical de los elementos
        ) {
            botonAddUbicacion(navController)
            mostrarUbicacion(eventoVM)
        }
        Spacer(modifier = Modifier.height(20.dp))
        seleccionarFechaEvento(eventoVM)
        Spacer(modifier = Modifier.height(20.dp))
        seleccionarHoraEvento(eventoVM)
        Spacer(modifier = Modifier.height(20.dp))
        seleccionarPlazoInscripcion(eventoVM)
        Spacer(modifier = Modifier.height(20.dp))
        Row( modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center,verticalAlignment = Alignment.CenterVertically){
            botonAceptar(navController, eventoVM )
            Spacer(modifier = Modifier.width(8.dp))
            botonCancelar(navController, eventoVM)
        }
    }
}

@Composable
fun descripcion(eventoVM: EventoViewModel){
    val descripcion = remember { mutableStateOf(eventoVM.descripcion.value) }


    Text(
        text = "Descripción:",
        color = colorResource(R.color.texto),
        fontSize = 15.sp,
        modifier = Modifier.padding(10.dp)
    )
    TextField(
        value = descripcion.value,
        onValueChange = {nuevaDescripcion->
            descripcion.value = nuevaDescripcion
            eventoVM.setDescripcion(nuevaDescripcion)
        },
        modifier = Modifier
            .width(400.dp)
            .padding(8.dp),
        singleLine = true,
        textStyle = TextStyle(fontSize = 15.sp)
    )
}

@Composable
fun botonAddUbicacion(navController: NavHostController) {
    Button(onClick = {navController.navigate(Rutas.mapa)},
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        )
    ) {
        Text(
            text = "Añadir ubicación",

            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            modifier = Modifier.padding(10.dp)
        )
    }

}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun mostrarUbicacion(eventoVM: EventoViewModel){

    val direccion = eventoVM.getDireccion(eventoVM.latitud.value, eventoVM.longitud.value)
    val dir = remember { mutableStateOf(TextFieldValue(direccion)) }

    Text(
        text = dir.value.text,
        style = TextStyle(color = colorResource(R.color.texto), fontSize = 15.sp,), // Cambia el color y el tamaño del texto
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun seleccionarFechaEvento(eventoVM: EventoViewModel){
    var fechaEvento by remember { mutableStateOf<LocalDate?>(null) }

    val contexto = LocalContext.current
    val fechaActual = LocalDate.now()

    val ventanaFecha = android.app.DatePickerDialog(
        contexto,
        { _, año, mes, dia ->
            // Seleccionar la fecha y actualizar el estado
            val fechaSeleccionada = LocalDate.of(año, mes + 1, dia)  // Ajustar el mes (0-11)
            fechaEvento = fechaSeleccionada

            // Actualizar el ViewModel con la fecha seleccionada
            eventoVM.setFecha(fechaSeleccionada)
        },
        fechaActual.year,
        fechaActual.monthValue - 1,  // Ajustar el mes (0-11)
        fechaActual.dayOfMonth
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Fecha del Evento: ",
            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            modifier = Modifier.padding(10.dp)
        )

        // Mostrar la fecha seleccionada
        Text(
            text = fechaEvento?.toString() ?: "No seleccionada",
            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            modifier = Modifier.padding(10.dp)
        )

        // Botón para abrir el DatePicker
        IconButton(onClick = { ventanaFecha.show() }) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Seleccionar Fecha",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun seleccionarHoraEvento(eventoVM: EventoViewModel) {
    var horaEvento by remember { mutableStateOf<LocalTime?>(null) }

    val contexto = LocalContext.current
    val horaActual = LocalTime.now()

    // Crear un TimePickerDialog
    val ventanaHora = android.app.TimePickerDialog(
        contexto,
        { _, hora, minuto ->
            // Seleccionar la hora y actualizar el estado
            val horaSeleccionada = LocalTime.of(hora, minuto)
            horaEvento = horaSeleccionada

            // Actualizar el ViewModel con la hora seleccionada
            eventoVM.setHora(horaSeleccionada)
        },
        horaActual.hour,
        horaActual.minute,
        true // Especifica si quieres usar el formato de 24 horas (true) o de 12 horas (false)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Hora del Evento: ",
            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            modifier = Modifier.padding(10.dp)
        )

        // Mostrar la hora seleccionada
        Text(
            text = horaEvento?.toString() ?: "No seleccionada",
            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            modifier = Modifier.padding(10.dp)
        )

        // Botón para abrir el TimePicker
        IconButton(onClick = { ventanaHora.show() }) {
            Icon(
                imageVector = Icons.Filled.AccessTime,
                contentDescription = "Seleccionar Hora",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun seleccionarPlazoInscripcion(eventoVM: EventoViewModel){
    var fechaEvento by remember { mutableStateOf<LocalDate?>(null) }

    val contexto = LocalContext.current
    val fechaActual = LocalDate.now()

    val ventanaFecha = android.app.DatePickerDialog(
        contexto,
        { _, año, mes, dia ->
            // Seleccionar la fecha y actualizar el estado
            val fechaSeleccionada = LocalDate.of(año, mes + 1, dia)  // Ajustar el mes (0-11)
            fechaEvento = fechaSeleccionada

            if (fechaSeleccionada > eventoVM.fecha.value){
                Toast.makeText(contexto, "El plazo de inscripcion tiene que ser antes que el evento", Toast.LENGTH_SHORT).show()

            } else{
                // Actualizar el ViewModel con la fecha seleccionada
                eventoVM.setPlazoInscripcion(fechaSeleccionada)
            }
        },
        fechaActual.year,
        fechaActual.monthValue - 1,  // Ajustar el mes (0-11)
        fechaActual.dayOfMonth
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Plazo de inscripción ",
            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            modifier = Modifier.padding(10.dp)
        )

        // Mostrar la fecha seleccionada
        Text(
            text = fechaEvento?.toString() ?: "No seleccionada",
            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            modifier = Modifier.padding(10.dp)
        )

        // Botón para abrir el DatePicker
        IconButton(onClick = { ventanaFecha.show() }) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Seleccionar Fecha",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun botonAceptar(
    navController: NavHostController, eventoVM: EventoViewModel
) {
    Button(onClick = {
        eventoVM.addEvento()
        navController.navigate(Rutas.administrador)},
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        )
    )
    {
        Text(text = "Aceptar")
    }

}

@Composable
fun botonCancelar(
    navController: NavHostController, eventoVM: EventoViewModel
) {
    Button(onClick = {
        eventoVM.limpiarDatos()
        navController.navigate(Rutas.administrador)},
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.botones), // Color de fondo del botón
            contentColor = colorResource(R.color.textoBotones) // Color del texto
        )
    )
    {
        Text(text = "Cancelar")
    }

}