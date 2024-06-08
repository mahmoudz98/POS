package com.casecode.pos.ui.item

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casecode.pos.R
import com.casecode.pos.design.component.PosTopAppBar
import com.casecode.pos.design.icon.PosIcons


enum class SearchWidgetState {
    OPENED, CLOSED
}

@Composable
fun ItemTopAppBar(
    searchWidgetState: SearchWidgetState,
    modifier: Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onMenuClick: () -> Unit,
    onSearchClicked: () -> Unit,
    onCloseClicked: () -> Unit,
) {

    when (searchWidgetState) {
        SearchWidgetState.CLOSED -> {
            DefaultAppBar(
                modifier = modifier,
                onMenuClick = onMenuClick,
                onSearchClicked = onSearchClicked,
            )
        }

        SearchWidgetState.OPENED -> {
            SearchToolbar(
                modifier = modifier,
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                onCloseClicked = { onCloseClicked() },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppBar(
    onMenuClick: () -> Unit, onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PosTopAppBar(
        modifier = modifier,
        titleRes = R.string.items_title,
        navigationIcon = PosIcons.Menu,
        navigationIconContentDescription = stringResource(
            id = R.string.menu_pos,
        ),
        onActionClick = { onSearchClicked() },
        actionIconContentDescription = stringResource(R.string.search),
        actionIcon = PosIcons.Search,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
        ),
        onNavigationClick = { onMenuClick() },
    )

}

@Composable
internal fun SearchToolbar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onCloseClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        SearchTextField(
            modifier = modifier,
            searchQuery = searchQuery,
            onCloseClicked = onCloseClicked,
            onSearchQueryChanged = onSearchQueryChanged,
        )
    }
}

@Composable
private fun SearchTextField(
    modifier: Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onCloseClicked: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    val keyboardController = LocalSoftwareKeyboardController.current

    val onSearchExplicitlyTriggered = {
        onCloseClicked()
        keyboardController?.hide()
    }

    TextField(
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        leadingIcon = {
            Icon(
                imageVector = PosIcons.Search,
                contentDescription = stringResource(
                    id = R.string.search,
                ),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    if (searchQuery.isNotEmpty()) {
                        onSearchQueryChanged("")
                    } else {
                        onCloseClicked()
                    }
                },
            ) {
                Icon(
                    imageVector = PosIcons.Close,
                    contentDescription = stringResource(
                        id = R.string.navigation_drawer_close,
                    ),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

        },
        onValueChange = {
            if ("\n" !in it) onSearchQueryChanged(it)
        },
        modifier = modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .focusRequester(focusRequester)
            .onKeyEvent {
                if (it.key == Key.Enter) {
                    onSearchExplicitlyTriggered()
                    true
                } else {
                    false
                }
            }
            .testTag("searchTextField"),
        shape = RoundedCornerShape(32.dp),
        value = searchQuery,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchExplicitlyTriggered()
            },
        ),
        maxLines = 1,
        singleLine = true,
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultAppBarPreview() {
    DefaultAppBar(
        onMenuClick = {},
        onSearchClicked = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchToolbarPreview() {
    SearchToolbar(
        searchQuery = "",
        onSearchQueryChanged = {},
        onCloseClicked = {},
    )

}