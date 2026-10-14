package io.github.ethanscharlie.virtualbank.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.input.KeyboardType
import javax.management.relation.Role


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositWithdrawlScreen(
    onBackClick: () -> Unit,
    bank: Bank,
    modifier: Modifier = Modifier,
) {
    var currentAccountMut = mutableStateOf("")
    var currentAccount by remember { currentAccountMut }

    var otherAccountMut = mutableStateOf("")
    var otherAccount by remember { otherAccountMut }

    var dropdownExpanded by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf("") }
    var radioSetting by remember { mutableStateOf("Withdrawl") }

    val accountOptions: List<String> = bank.accounts.map { (name, account) -> name }

    val icon = if (dropdownExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Add Record")
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
            Modifier.padding(32.dp).padding(innerPadding).verticalScroll(rememberScrollState())
        ) {

            DropdownMenu(currentAccountMut, accountOptions)

            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text("Value") },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .width(1000.dp)
            )

            val radioOptions = listOf("Withdrawl", "Deposit", "Transfer")
            Column(modifier.selectableGroup()) {
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (text == radioSetting),
                                onClick = { radioSetting = text }
                            )
                            .padding(horizontal = 0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == radioSetting),
                            onClick = null // null recommended for accessibility with screen readers
                        )
                        Text(
                            text = text,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            if (radioSetting == "Transfer") {
                DropdownMenu(otherAccountMut, accountOptions)
            }

            Button(
                onClick = {
                    var v = 0.0f

                    try {
                        v = value.toFloat()
                    } catch (e: NumberFormatException) {
                        return@Button
                    }

                    if (currentAccount == "") return@Button
                    if (radioSetting == "Transfer" && otherAccount == "") return@Button

                    when (radioSetting) {
                        "Withdrawl" -> bank.withdrawl(currentAccount, v)
                        "Deposit" -> bank.deposit(currentAccount, v)
                        "Transfer" -> bank.transfer(currentAccount, otherAccount, v)
                    }

                    onBackClick()
                },
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .width(1000.dp)
            ) {
                Text(text = "Enter") // TODO Crash on enter and can't add after added one
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(selection: MutableState<String>, options: List<String>) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = dropdownExpanded,
        onExpandedChange = { dropdownExpanded = it },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .padding(vertical = 10.dp)
                .width(1000.dp),
            value = selection.value,
            onValueChange = { selection.value = it },
            label = { Text("Select") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            readOnly = true
        )
        DropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        selection.value = selectionOption
                        dropdownExpanded = false
                    }
                )
            }
        }
    }
}
