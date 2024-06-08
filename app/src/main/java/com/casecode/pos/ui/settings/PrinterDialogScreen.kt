package com.casecode.pos.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.casecode.pos.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPrinterDialog(
    onDismissRequest: () -> Unit,
    onSubmit: (String, String, Int, String) -> Unit
) {
    var printerName by remember { mutableStateOf("") }
    var ipAddress by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    var macAddress by remember { mutableStateOf("") }


            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = printerName,
                    onValueChange = { printerName = it },
                    label = { Text("Printer Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = { Text("IP Address") },
                    visualTransformation = IpAddressTransformation,

                    keyboardOptions  = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = port,
                    onValueChange = { port = it },
                    label = { Text("Port") },
                    keyboardOptions  = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = macAddress,
                    onValueChange = { macAddress = it },
                    label = { Text("MAC Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

}

 private object IpAddressTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // 1. Split the input into parts
        val parts = text.text.split('.')
        // 2. Add "." between parts, up to 3 parts
        val outputText = parts.take(3).joinToString(".")

        // 3. Calculate the OffsetMapping (crucial for cursor positioning)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= text.length) {
                    return outputText.indexOf(text.text.substring(0, offset))
                }
                return outputText.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= outputText.length) {
                    return text.text.indexOf(outputText.substring(0, offset))
                }
                return text.text.length
            }
        }

        // 4. Create the TransformedText object
        return TransformedText(
            AnnotatedString(outputText),
            offsetMapping
        )
    }
}