package com.casecode.pos.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.ui.BranchesHeader

@Composable
fun BranchesTab(viewModel: ProfileViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddBranchDialog by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        BranchesHeader(onAddClick = { showAddBranchDialog = true })
        BranchesList(branches = uiState.business.branches)
    }
    if (showAddBranchDialog) {
        AddBranchDialog(
            onDismissRequest = { showAddBranchDialog = false },
            viewModel = viewModel,
        )
    }
}

@Composable
private fun BranchesList(branches: List<Branch>) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
    ) {
        items(branches) { branch ->
            BranchItem(branch = branch)
        }
    }
}

@Composable
private fun BranchItem(branch: Branch) {
    ElevatedCard(Modifier.padding(bottom = 8.dp)) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(com.casecode.pos.core.ui.R.string.core_ui_branch_name_hint) + branch.branchName,
                )
            },
            supportingContent = {
                Text(
                    text = branch.phoneNumber,
                )
            },
            leadingContent = {
                Text(
                    text = "0${branch.branchCode}",
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            colors =
                ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    headlineColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    overlineColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    supportingColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BranchItemPreview() {
    POSTheme {
        BranchesList(
            arrayListOf(
                Branch(branchCode = 1, branchName = "branch1", phoneNumber = "0000000000"),
                Branch(branchCode = 2, branchName = "branch2", phoneNumber = "0000000000"),
            ),
        )
    }
}