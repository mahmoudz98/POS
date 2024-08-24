package com.casecode.pos.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.model.data.users.Business

@Composable
fun BusinessInfoTab(business: Business) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        // TODO: Handle storeType with english and arabic
        OutlinedTextField(
            value = business.storeType?.englishName ?: "",
            onValueChange = { /* No changes allowed */ },
            label = { Text(stringResource(id = com.casecode.pos.core.ui.R.string.core_ui_store_type_hint)) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = business.email ?: "",
            onValueChange = { /* No changes allowed */ },
            label = { Text(stringResource(id = com.casecode.pos.core.ui.R.string.core_ui_work_email_hint)) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = business.phone ?: "",
            onValueChange = { /* No changes allowed */ },
            label = { Text(stringResource(id = com.casecode.pos.core.ui.R.string.core_ui_work_phone_number_hint)) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
        )
    }
}