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
package com.casecode.pos.feature.supplier

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.component.SearchToolbar
import com.casecode.pos.core.designsystem.component.SearchTopAppBar
import com.casecode.pos.core.designsystem.component.SearchWidgetState
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Supplier
import com.casecode.pos.core.ui.parameterprovider.SupplierPreviewParameterProvider
import com.casecode.pos.core.ui.utils.formatPhoneNumber

@Composable
fun SupplierTopAppBar(
    searchWidgetState: SearchWidgetState,
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onSearchClicked: () -> Unit,
    onCloseClicked: () -> Unit,
) {
    SearchTopAppBar(
        searchWidgetState = searchWidgetState,
        defaultTopAppBar = {
            SupplierDefaultTopAppBar(
                modifier = modifier,
                onBackClick = onBackClick,
                onSearchClicked = onSearchClicked,
            )
        },
        searchTopAppBar = {
            SearchToolbar(
                modifier = modifier,
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                onCloseClicked = onCloseClicked,
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierDefaultTopAppBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSearchClicked: () -> Unit,
) {
    PosTopAppBar(
        modifier = modifier,
        navigationIcon = PosIcons.ArrowBack,
        titleRes = R.string.feature_supplier_title,
        actionIcon = PosIcons.Search,
        onNavigationClick = { onBackClick() },
        onActionClick = { onSearchClicked() },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),

    )
}

@Composable
fun SupplierCardList(
    suppliers: List<Supplier>,
    countryIsoCode: String = "EG",
    onSupplierClick: (Supplier) -> Unit,
    onSupplierDelete: (Supplier) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp).clipToBounds(),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(suppliers, key = { it.id }) { supplier ->
            SupplierCard(
                contactName = supplier.contactName,
                companyName = supplier.companyName,
                contactPhone = supplier.contactPhone,
                countryIsoCode = countryIsoCode,
                modifier = Modifier.padding(8.dp).animateItem(),
                onSupplierClick = { onSupplierClick(supplier) },
                onSupplierDelete = { onSupplierDelete(supplier) },
            )
        }
        item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing)) }
    }
}

@Composable
fun SupplierCard(
    modifier: Modifier = Modifier,
    companyName: String,
    contactName: String,
    contactPhone: String,
    countryIsoCode: String,
    onSupplierClick: () -> Unit,
    onSupplierDelete: () -> Unit,
) {
    ElevatedCard(
        modifier.combinedClickable(onClick = onSupplierClick, onLongClick = onSupplierDelete),
    ) {
        ListItem(
            overlineContent = { Text(text = contactName) },
            headlineContent = { Text(companyName) },
            supportingContent = {
                val phoneFormated = formatPhoneNumber(contactPhone, countryIsoCode) ?: contactPhone
                Text(phoneFormated)
            },
            colors =
            ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                headlineColor = MaterialTheme.colorScheme.tertiary,
                overlineColor = MaterialTheme.colorScheme.onSurfaceVariant,
                supportingColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun SupplierCardListPreview(
    @PreviewParameter(SupplierPreviewParameterProvider::class) suppliers: List<Supplier>,
) {
    POSTheme { SupplierCardList(suppliers, "EG", {}, {}) }
}

@Preview
@Composable
private fun SupplierCardPreview(
    @PreviewParameter(SupplierPreviewParameterProvider::class) suppliers: List<Supplier>,
) {
    POSTheme {
        SupplierCard(
            contactName = suppliers[0].contactName,
            companyName = suppliers[0].companyName,
            contactPhone = suppliers[0].contactPhone,
            countryIsoCode = "EG",
            onSupplierClick = {},
            onSupplierDelete = {},
        )
    }
}