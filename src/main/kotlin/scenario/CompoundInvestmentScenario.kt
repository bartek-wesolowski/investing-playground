package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.strategy.BaseInvestmentStrategy
import javax.money.CurrencyUnit
import javax.money.Monetary

class CompoundInvestmentScenario(
    private val scenarios: List<PartialInvestmentScenario>,
    private val currency: CurrencyUnit = Monetary.getCurrency("USD")
) : InvestmentScenario {
    override fun simulate(strategy: BaseInvestmentStrategy): List<InvestmentState> {
        val result = mutableListOf(InvestmentState.initial(currency))
        var year = 1
        for (scenario in scenarios) {
            scenario.setStartYear(year)
            while (!scenario.isFinished(year, strategy)) {
                result += scenario.transact(year, strategy)
                year++
            }
        }
        return result
    }
}