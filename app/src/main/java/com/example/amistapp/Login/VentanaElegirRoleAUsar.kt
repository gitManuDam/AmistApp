package com.example.amistapp.Login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.amistapp.R

@Composable
fun VentanaElegirRoleAUsar():String{
    var selectedText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var opciones = listOf<String>("estandar", "administrador")

    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tipo de acceso: ",
            color = colorResource(R.color.texto),
            fontSize = 15.sp,
            modifier = Modifier.padding(10.dp)
        )

        Box(modifier = Modifier.width(140.dp)) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = { selectedText = it },
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .clickable {
                        expanded = true
                    }
                    .wrapContentWidth()
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false })
            {
                opciones.forEach { opcion ->
                    DropdownMenuItem(text = { Text(text = opcion) }, onClick = {
                        expanded = false
                        selectedText = opcion


                    })
                }
            }
        }
    }
    return selectedText
}