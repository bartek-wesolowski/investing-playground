package com.bartoszwesolowski.scenario

import javax.money.MonetaryAmount

data class InvestmentScenarioResult(
    val investmentValue: MonetaryAmount,
    val accountValue: MonetaryAmount,
    val accountValueAfterTax: MonetaryAmount,
    val withdrawnValueAfterTax: MonetaryAmount,
    val taxValue: MonetaryAmount,
)