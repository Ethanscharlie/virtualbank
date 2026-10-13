package io.github.ethanscharlie.virtualbank

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Main : NavKey

@Serializable
data object DepositWithdrawl : NavKey
