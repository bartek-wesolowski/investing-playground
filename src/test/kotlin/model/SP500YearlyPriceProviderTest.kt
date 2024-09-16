package model

import com.bartoszwesolowski.model.SP500YearlyPriceProvider
import com.bartoszwesolowski.usd
import kotlin.test.Test
import kotlin.test.assertEquals

class SP500YearlyPriceProviderTest {
    @Test
    fun `offset 0`() {
        val sp500YearlyPriceProvider = SP500YearlyPriceProvider(100.usd)
        assertEquals(100.usd, sp500YearlyPriceProvider.getPriceInYear(1, "SP500"))
        assertEquals(138.06.usd, sp500YearlyPriceProvider.getPriceInYear(2, "SP500"))
        assertEquals(197.92.usd, sp500YearlyPriceProvider.getPriceInYear(3, "SP500"))
        assertEquals(221.79.usd, sp500YearlyPriceProvider.getPriceInYear(4, "SP500"))
    }

    @Test
    fun `offset 1`() {
        val sp500YearlyPriceProvider = SP500YearlyPriceProvider(100.usd, offset = 1)
        assertEquals(100.usd, sp500YearlyPriceProvider.getPriceInYear(1, "SP500"))
        assertEquals(143.36.usd, sp500YearlyPriceProvider.getPriceInYear(2, "SP500"))
        assertEquals(160.65.usd, sp500YearlyPriceProvider.getPriceInYear(3, "SP500"))
        assertEquals(161.39.usd, sp500YearlyPriceProvider.getPriceInYear(4, "SP500"))
    }
}