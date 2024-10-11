package com.bartoszwesolowski.model

import javax.money.MonetaryAmount

fun interface PriceProvider {
    fun getPrice(): MonetaryAmount
}