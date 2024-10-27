/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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