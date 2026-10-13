package io.github.ethanscharlie.virtualbank.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import io.github.ethanscharlie.virtualbank.Bank
import io.github.ethanscharlie.virtualbank.DepositWithdrawl
import io.github.ethanscharlie.virtualbank.Rules
import io.github.ethanscharlie.virtualbank.data.DefaultDataRepository
import io.github.ethanscharlie.virtualbank.theme.VirtualBankTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onItemClick: (NavKey) -> Unit, bank: Bank, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Virtual Bank")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onItemClick(DepositWithdrawl) })
            {
                Icon(Icons.Filled.Add, "Deposit/Withdrawl action button.")
            }
        }
    )
    { innerPadding ->
        Column(
            Modifier.padding(32.dp).padding(innerPadding)
        ) {
            bank.accounts.forEach { (name, account) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = name)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = String.format("$ %.2f", account), textAlign = TextAlign.End)
                }

                HorizontalDivider()
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onItemClick(Rules) }
            ) {
                Text("Rules")
            }
        }
    }
}

