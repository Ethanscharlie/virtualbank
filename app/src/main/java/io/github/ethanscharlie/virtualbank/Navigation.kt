package io.github.ethanscharlie.virtualbank

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.github.ethanscharlie.virtualbank.ui.main.*

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(Main)
    val bank = Bank(LocalContext.current)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry<Main> {
                    MainScreen(
                        onItemClick = { navKey -> backStack.add(navKey) },
                        bank = bank,
                        modifier = Modifier.safeDrawingPadding().padding(16.dp)
                    )
                }

                entry<DepositWithdrawl> {
                    DepositWithdrawlScreen(
                        onBackClick = { backStack.removeLastOrNull() },
                        bank = bank,
                        modifier = Modifier.safeDrawingPadding().padding(16.dp)
                    )
                }

                entry<Rules> {
                    RulesScreen(
                        onBackClick = { backStack.removeLastOrNull() },
                        bank = bank,
                        modifier = Modifier.safeDrawingPadding().padding(16.dp)
                    )
                }

                entry<Accounts> {
                    AccountsScreen(
                        onBackClick = { backStack.removeLastOrNull() },
                        bank = bank,
                        modifier = Modifier.safeDrawingPadding().padding(16.dp)
                    )
                }
            },
    )
}
