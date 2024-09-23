package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.strategy.BaseInvestmentStrategy

interface PartialInvestmentScenario {
    fun setStartYear(year: Int)
    fun transact(year: Int, strategy: BaseInvestmentStrategy): InvestmentState
    fun isFinished(year: Int, strategy: BaseInvestmentStrategy): Boolean
}