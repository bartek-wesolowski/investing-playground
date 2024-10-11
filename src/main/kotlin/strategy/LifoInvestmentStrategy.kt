package com.bartoszwesolowski.strategy

import javax.money.MonetaryAmount

class LifoInvestmentStrategy(verbose: Boolean) : ManualInvestmentStrategy(verbose) {
    private var isFirstBuy = true

    override fun buy(currentPrice: MonetaryAmount, value: MonetaryAmount) {
        if (!isFirstBuy) {
            newAsset()
        }
        super.buy(currentPrice, value)
        isFirstBuy = false
    }
}