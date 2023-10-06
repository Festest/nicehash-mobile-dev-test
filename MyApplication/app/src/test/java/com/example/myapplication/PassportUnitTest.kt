package com.example.myapplication

import com.example.myapplication.testtwo.PassportChecker
import org.junit.Test

import org.junit.Assert.*
import java.io.InputStream

class PassportUnitTest {
    @Test
    fun execute_isCorrect() {
        val inputString = "" +
                "ecl:gry pid:860033327 eyr:2020 hcl:#fffffd\n\n" +
                "byr:1937 iyr:2017 cid:147 hgt:183cm\n\n" +
                "\n\n\n" +
                "iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884\n\n" +
                "hcl:#cfa07d byr:1929\n\n" +
                "\n\n\n" +
                "hcl:#ae17e1 iyr:2013\n\n" +
                "eyr:2024\n\n" +
                "ecl:brn pid:760753108 byr:1931\n\n" +
                "hgt:179cm\n\n" +
                "\n\n\n" +
                "hcl:#cfa07d eyr:2025 pid:166559648\n\n" +
                "iyr:2011 ecl:brn hgt:59in"

        val inputStream: InputStream = inputString.byteInputStream(Charsets.UTF_8)

        val passChecker = PassportChecker(inputStream)
        val computedResult = passChecker.execute()

        assertEquals(2, computedResult.resultMultiThreaded)
        assertEquals(2, computedResult.resultSingleThreaded)
        assertEquals(computedResult.resultMultiThreaded, computedResult.resultSingleThreaded)
    }
}