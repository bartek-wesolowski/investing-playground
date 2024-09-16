package com.bartoszwesolowski.model

import nl.hiddewieringa.money.times
import javax.money.Monetary
import javax.money.MonetaryAmount

class SP500YearlyPriceProvider(
    private val initialPrice: MonetaryAmount,
    private val offset: Int = 0
) : YearlyPriceProvider {
    private val sp500Returns = listOf(
        0.3806, 0.4336, 0.1206, 0.0046, 0.2689, -0.0873, 0.2280, 0.1648, 0.1245, -0.1006,
        0.2398, 0.1106, -0.0850, 0.0010, 0.1431, 0.1915, -0.1466, -0.2697, 0.3720, 0.2384,
        -0.0718, 0.0656, 0.1844, 0.3242, -0.0491, 0.2155, 0.2234, 0.0627, 0.3173, 0.1867,
        0.0525, 0.1681, 0.3169, -0.0310, 0.3055, 0.0767, 0.1008, -0.0154, 0.3743, 0.2296,
        0.3336, 0.2858, 0.2104, -0.0910, -0.1189, -0.2210, 0.2868, 0.1088, 0.0491, 0.1579,
        0.0549, -0.3655, 0.2345, 0.1278, 0.0000, 0.1341, 0.2960, 0.1139, -0.0073, 0.0954,
        0.1942, -0.0624, 0.2888, 0.1626, 0.2689, -0.1811, 0.1227
    )

    override fun getPriceInYear(year: Int, assetName: String): MonetaryAmount {
        var price = initialPrice
        for (i in 2..year) {
            price *= 1 + sp500Returns[(i - 2 + offset) % sp500Returns.size]
        }
        return price.with(Monetary.getRounding(initialPrice.currency))
    }
}