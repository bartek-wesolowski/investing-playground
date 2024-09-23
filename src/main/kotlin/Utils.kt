package com.bartoszwesolowski

import org.javamoney.moneta.Money
import javax.money.MonetaryAmount

val Int.usd: MonetaryAmount
    get() = Money.of(this, "USD")

val Double.usd: MonetaryAmount
    get() = Money.of(this, "USD")

fun min(a: MonetaryAmount, b: MonetaryAmount): MonetaryAmount = if (a.isLessThan(b)) a else b