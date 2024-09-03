package com.bartoszwesolowski.strategy

import com.bartoszwesolowski.model.PriceProvider
import javax.money.MonetaryAmount

class LifoInvestmentStrategy(verbose: Boolean) : ManualInvestmentStrategy(verbose) {
    private var isFirstBuy = true

    override fun buy(priceProvider: PriceProvider, value: MonetaryAmount) {
        if (!isFirstBuy) {
            newAsset()
        }
        super.buy(priceProvider, value)
        isFirstBuy = false
    }
}