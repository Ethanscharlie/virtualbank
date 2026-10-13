package io.github.ethanscharlie.virtualbank

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.github.ethanscharlie.virtualbank.ui.main.DepositWithdrawlScreen
import io.github.ethanscharlie.virtualbank.ui.main.MainScreen
import io.github.ethanscharlie.virtualbank.ui.main.RulesScreen

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(Main)
    val bank = Bank()
    bank.test()

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
                        onBackClick = { backStack.removeLast() },
                        bank = bank,
                        modifier = Modifier.safeDrawingPadding().padding(16.dp)
                    )
                }

                entry<Rules> {
                    RulesScreen(
                        onBackClick = { backStack.removeLast() },
                        bank = bank,
                        modifier = Modifier.safeDrawingPadding().padding(16.dp)
                    )
                }
            },
    )
}
