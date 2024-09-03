import com.bartoszwesolowski.model.Transaction
import com.bartoszwesolowski.usd
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class TransactionTest {
    @Test
    fun testCurrentValue() {
        val transaction = Transaction(100.usd, 10.0)
        assertEquals(2000.usd, transaction.currentValue(200.usd))
    }

    @Test
    fun testCurrentValueAfterTax() {
        val transaction = Transaction(100.usd, 10.0)
        assertEquals(1810.usd, transaction.currentValueAfterTax(200.usd, 0.19))
    }

    @Test
    fun testSellValue() {
        val transaction = Transaction(100.usd, 10.0)
        val taxValue = transaction.sellValue(600.usd, 200.usd, 0.19)
        assertEquals(57.usd, taxValue)
        assertEquals(Transaction(100.usd, 7.0), transaction)
    }

    @Test
    fun testSellValueAfterTax() {
        val transaction = Transaction(100.usd, 10.0)
        val taxValue = transaction.sellValueAfterTax(543.usd, 200.usd, 0.19)
        assertEquals(57.usd, taxValue)
        assertEquals(Transaction(100.usd, 7.0), transaction)
    }
}