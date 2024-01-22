package com.dnovaes.stockcontrol

import com.dnovaes.stockcontrol.common.extensions.formatCurrency
import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun `test debug`() {
        val input = "2,0"
        val expected = "2,00"
        assertEquals(expected, input.formatCurrency())
    }

    @Test
    fun `assert inputs to valid brazilian currency having max length 3`() {
        val inputs = listOf(
            "2", //0
            "02", //1
            "002", //2
            "0,02", //3
            "20", //4
            "200", //5
            "2,00", //6
            "2,0", //7
            "0,0", //8
        )
        val expected = listOf(
            "0,02", //0
            "0,02", //1
            "0,02", //2
            "0,02", //3
            "0,20", //4
            "2,00", //5
            "2,00", //6
            "2,00", //7
            "0,00", //8
        )
        inputs.forEachIndexed { i, input ->
            println("tested $i with value: $input, expected: ${expected[i]}, found ${input.formatCurrency()}")
            assertEquals(expected[i], input.formatCurrency())
        }
    }


    @Test
    fun `assert inputs to valid brazilian currency bigger than length 3`() {
        val inputs = listOf(
            "0,002", //1
            "0,022", //2
            "0,222", //3
            "2,223", //4
            "22,234", //5
            "222,345", //6
            "1234,5", //7
        )

        val expected = listOf(
            "0,02", //1
            "0,22", //2
            "2,22", //3
            "22,23", //4
            "222,34", //5
            "2223,45", //6
            "12345,00", //7
        )

        inputs.forEachIndexed { i, input ->
            println("tested $i with value: $input, expected: ${expected[i]}, found ${input.formatCurrency()}")
            assertEquals(expected[i], input.formatCurrency())
        }
    }
}