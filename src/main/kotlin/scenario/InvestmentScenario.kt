package com.bartoszwesolowski.scenario

import com.bartoszwesolowski.strategy.BaseInvestmentStrategy

interface InvestmentScenario {
    fun simulate(strategy: BaseInvestmentStrategy): List<InvestmentState>
}