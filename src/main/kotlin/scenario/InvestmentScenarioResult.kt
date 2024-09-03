package com.bartoszwesolowski.scenario

import javax.money.MonetaryAmount

data class InvestmentScenarioResult(
    val investmentValue: MonetaryAmount,
    val withdrawnValue: MonetaryAmount,
    val withdrawalYears: Int,
    val taxValue: MonetaryAmount,
)