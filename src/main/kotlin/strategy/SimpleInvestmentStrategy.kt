package com.bartoszwesolowski.strategy

import com.bartoszwesolowski.model.PriceProvider
import javax.money.MonetaryAmount

class SimpleInvestmentStrategy(verbose: Boolean) : BaseInvestmentStrategy(verbose) {
    override fun buy(priceProvider: PriceProvider, value: MonetaryAmount) =
        account.buy(ASSET_NAME, priceProvider.getPrice(ASSET_NAME), value)

    override fun sellValue(
        priceProvider: PriceProvider,
        value: MonetaryAmount,
        tax: Double
    ): MonetaryAmount = account.sellValue(ASSET_NAME, value, priceProvider.getPrice(ASSET_NAME), tax)

    override fun sellAfterTaxValue(
        priceProvider: PriceProvider,
        afterTaxValue: MonetaryAmount,
        tax: Double
    ): MonetaryAmount =
        account.sellAfterTaxValue(ASSET_NAME, afterTaxValue, priceProvider.getPrice(ASSET_NAME), tax)

    private companion object {
        const val ASSET_NAME = "ETF-1"
    }
}