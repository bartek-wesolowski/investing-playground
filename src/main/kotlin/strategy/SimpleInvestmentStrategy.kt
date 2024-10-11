package com.bartoszwesolowski.strategy

import com.bartoszwesolowski.model.SellResult
import javax.money.MonetaryAmount

class SimpleInvestmentStrategy(verbose: Boolean) : BaseInvestmentStrategy(verbose) {
    override fun buy(currentPrice: MonetaryAmount, value: MonetaryAmount) =
        account.buy(ASSET_NAME, currentPrice, value)

    override fun sell(
        currentPrice: MonetaryAmount,
        value: MonetaryAmount,
        tax: Double
    ): SellResult = account.sell(ASSET_NAME, value, currentPrice, tax)

    override fun sellAfterTax(
        currentPrice: MonetaryAmount,
        afterTaxValue: MonetaryAmount,
        tax: Double
    ): SellResult = account.sellAfterTax(ASSET_NAME, afterTaxValue, currentPrice, tax)

    private companion object {
        const val ASSET_NAME = "ETF-1"
    }
}