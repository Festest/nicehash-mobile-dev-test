package com.example.myapplication

import com.example.myapplication.testone.Accumulator
import org.junit.Test

import org.junit.Assert.*
import java.io.InputStream

class AccumulatorUnitTest {
    @Test
    fun execute_isCorrect() {
        val inputString = "" +
                "nop +0\n\n" +
                "acc +1\n\n" +
                "jmp +4\n\n" +
                "acc +3\n\n" +
                "jmp -3\n\n" +
                "acc -99\n\n" +
                "acc +1\n\n" +
                "jmp -4\n\n" +
                "acc +6"

        val inputStream: InputStream = inputString.byteInputStream(Charsets.UTF_8)

        val accumulator = Accumulator(inputStream)
        assertEquals(5, accumulator.execute())
    }
}