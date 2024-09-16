import com.bartoszwesolowski.model.Asset
import com.bartoszwesolowski.model.SellResult
import com.bartoszwesolowski.model.Transaction
import com.bartoszwesolowski.usd
import java.util.LinkedList
import kotlin.test.Test
import kotlin.test.assertEquals

class AssetTest {
    @Test
    fun testCurrentValue() {
        val asset = Asset(
            "ETF1",
            LinkedList<Transaction>().apply {
                add(Transaction(100.usd, 10.0))
                add(Transaction(150.usd, 5.0))
            }
        )

        assertEquals(3000.usd, asset.currentValue(200.usd))
    }

    @Test
    fun testCurrentValueAfterTax() {
        val asset = Asset(
            "ETF1",
            LinkedList<Transaction>().apply {
                add(Transaction(100.usd, 10.0))
                add(Transaction(150.usd, 5.0))
            }
        )

        assertEquals(2762.5.usd, asset.currentValueAfterTax(200.usd, 0.19))
    }

    @Test
    fun testSell() {
        val asset = Asset(
            "ETF1",
            LinkedList<Transaction>().apply {
                add(Transaction(100.usd, 10.0))
                add(Transaction(150.usd, 5.0))
            }
        )

        val result = asset.sell(2600.usd, 200.usd, 0.19)
        assertEquals(
            SellResult(
                value = 2600.usd,
                valueAfterTax = 2381.5.usd,
                taxValue = 218.50.usd
            ), result
        )
        assertEquals(
            Asset(
                "ETF1",
                LinkedList<Transaction>().apply {
                    add(Transaction(150.usd, 2.0))
                }
            ),
            asset
        )
    }

    @Test
    fun testSellAfterTax() {
        val asset = Asset(
            "ETF1",
            LinkedList<Transaction>().apply {
                add(Transaction(100.usd, 10.0))
                add(Transaction(150.usd, 5.0))
            }
        )

        val result = asset.sellAfterTax(2381.5.usd, 200.usd, 0.19)
        assertEquals(
            SellResult(
                value = 2600.usd,
                valueAfterTax = 2381.5.usd,
                taxValue = 218.5.usd
            ), result
        )
        assertEquals(
            Asset(
                "ETF1",
                LinkedList<Transaction>().apply {
                    add(Transaction(150.usd, 2.0))
                }
            ),
            asset
        )
    }
}