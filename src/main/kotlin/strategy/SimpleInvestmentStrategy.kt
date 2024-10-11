package com.bartoszwesolowski.strategy

import com.bartoszwesolowski.model.SellResult
import com.bartoszwesolowski.model.PriceProvider
import javax.money.MonetaryAmount

class SimpleInvestmentStrategy(verbose: Boolean) : BaseInvestmentStrategy(verbose) {
    override fun buy(priceProvider: PriceProvider, value: MonetaryAmount) =
        account.buy(ASSET_NAME, priceProvider.getPrice(), value)

    override fun sell(
        priceProvider: PriceProvider,
        value: MonetaryAmount,
        tax: Double
    ): SellResult = account.sell(ASSET_NAME, value, priceProvider.getPrice(), tax)

    override fun sellAfterTax(
        priceProvider: PriceProvider,
        afterTaxValue: MonetaryAmount,
        tax: Double
    ): SellResult = account.sellAfterTax(ASSET_NAME, afterTaxValue, priceProvider.getPrice(), tax)

    private companion object {
        const val ASSET_NAME = "ETF-1"
    }
}