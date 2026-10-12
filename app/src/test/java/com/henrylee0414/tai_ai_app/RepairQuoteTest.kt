package com.henrylee0414.tai_ai_app

import com.henrylee0414.tai_ai_app.data.model.RepairQuote
import org.junit.Assert.assertEquals
import org.junit.Test

class RepairQuoteTest {

    @Test
    fun total_addsPartsAndLabor() {
        val quote = RepairQuote(
            partsAmountTWD = 2400,
            laborAmountTWD = 600
        )

        assertEquals(3000L, quote.totalAmountTWD)
        assertEquals("TWD", quote.currency)
    }

    @Test
    fun zeroAmount_isAllowed() {
        val quote = RepairQuote(
            partsAmountTWD = 0,
            laborAmountTWD = 0
        )

        assertEquals(0L, quote.totalAmountTWD)
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativePartsAmount_isRejected() {
        RepairQuote(
            partsAmountTWD = -1,
            laborAmountTWD = 600
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeLaborAmount_isRejected() {
        RepairQuote(
            partsAmountTWD = 2400,
            laborAmountTWD = -1
        )
    }

    @Test
    fun largeAmounts_doNotOverflowInt() {
        val quote = RepairQuote(
            partsAmountTWD = Int.MAX_VALUE,
            laborAmountTWD = Int.MAX_VALUE
        )

        assertEquals(4294967294L, quote.totalAmountTWD)
    }
}