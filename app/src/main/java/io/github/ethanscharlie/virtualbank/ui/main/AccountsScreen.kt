package io.github.ethanscharlie.virtualbank.ui.main

import android.view.RoundedCorner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import io.github.ethanscharlie.virtualbank.Bank
import io.github.ethanscharlie.virtualbank.data.DefaultDataRepository
import io.github.ethanscharlie.virtualbank.theme.VirtualBankTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import javax.management.relation.Role


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    onBackClick: () -> Unit,
    bank: Bank,
    modifier: Modifier = Modifier,
) {
    var openAlertDialog by remember { mutableStateOf(false) }
    var newAccountName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Accounts")
                },

                navigationIcon = {
                    TextButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            Modifier.padding(32.dp).padding(innerPadding)
        ) {

            OutlinedTextField(
                value = newAccountName,
                onValueChange = { newAccountName = it },
                label = { Text("New Account") },
                maxLines = 1,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .width(1000.dp)
            )

            Button(
                onClick = {
                    if (newAccountName != "") {
                        bank.createAccount(newAccountName)
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(1000.dp)
            ) {
                Text("Create Account")
            }

            HorizontalDivider()

            bank.allAccounts().forEach { (name, account) ->
                Row() {
                    Text(
                        String.format("%s ($ %.2f)", name, account),
                        modifier = Modifier.padding(8.dp).align(Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            if (account != 0.0f) {
                                openAlertDialog = true
                            } else {
                                bank.removeAccount(name)
                                onBackClick()
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Delete, "Remove Icon Button")
                    }

                    when {
                        openAlertDialog -> CannotRemoveDialog({ openAlertDialog = false })
                    }
                }

                HorizontalDivider()
            }
        }
    }
}

@Composable
fun CannotRemoveDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        icon = {
            //Icon(icon, contentDescription = "Example Icon")
        },
        text = {
            Text(text = "Cannot remove account unless empty")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Confirm")
            }
        },
    )
}
