package com.bartoszwesolowski.scenario

import javax.money.MonetaryAmount

data class InvestmentState(
    val investedValue: MonetaryAmount,
    val value: MonetaryAmount,
    val valueAfterTax: MonetaryAmount,
    val withdrawnValue: MonetaryAmount,
    val withdrawnValueAfterTax: MonetaryAmount,
    val taxValue: MonetaryAmount,
)