package model

import com.bartoszwesolowski.model.SP500PriceProvider
import com.bartoszwesolowski.usd
import kotlin.test.Test
import kotlin.test.assertEquals

class SP500PriceProviderTest {
    @Test
    fun `offset 0`() {
        val sp500YearlyPriceProvider = SP500PriceProvider(100.usd)
        assertEquals(100.usd, sp500YearlyPriceProvider.getPriceInYear(1))
        assertEquals(138.06.usd, sp500YearlyPriceProvider.getPriceInYear(2))
        assertEquals(197.92.usd, sp500YearlyPriceProvider.getPriceInYear(3))
        assertEquals(221.79.usd, sp500YearlyPriceProvider.getPriceInYear(4))
    }

    @Test
    fun `offset 1`() {
        val sp500YearlyPriceProvider = SP500PriceProvider(100.usd, offset = 1)
        assertEquals(100.usd, sp500YearlyPriceProvider.getPriceInYear(1))
        assertEquals(143.36.usd, sp500YearlyPriceProvider.getPriceInYear(2))
        assertEquals(160.65.usd, sp500YearlyPriceProvider.getPriceInYear(3))
        assertEquals(161.39.usd, sp500YearlyPriceProvider.getPriceInYear(4))
    }
}