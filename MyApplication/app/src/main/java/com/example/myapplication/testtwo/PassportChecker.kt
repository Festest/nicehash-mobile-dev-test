package com.example.myapplication.testtwo

import java.io.IOException
import java.io.InputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

/**
 * Create an ExecutionResult data class that we will return to be presented to the user.
 */
data class ExecutionResult(
    val resultSingleThreaded: Int,
    val durationSingleThreaded: Long,
    val resultMultiThreaded: Int,
    val durationMultiThreaded: Long
)

/**
 * PassportChecker will take an InputStream, retrieve the contents of the file
 * and do passport validation in both single and multi threaded modes.
 */
class PassportChecker(private val inputStream: InputStream?) {
    /**
     * We parse the given file and generate a list of strings
     * where each element of the list (string) is one passport
     *
     * @return List of passports (strings)
     */
    private fun readFileContents(): List<String> {
        val passports = mutableListOf<String>()
        val currentPassport = StringBuilder()

        var consecutiveEmptyLines = 0

        try {
            inputStream?.bufferedReader()?.forEachLine { line ->
                if (line.isBlank()) {
                    consecutiveEmptyLines++ // Ignore empty lines
                } else {
                    consecutiveEmptyLines = 0

                    // Add a space if the remaining passport info is in another line
                    if (currentPassport.isNotEmpty()) currentPassport.append(' ')

                    currentPassport.append(line.trim())
                }

                // A new passport begins after 3 consecutive empty lines
                if (consecutiveEmptyLines == 3) {
                    passports.add(currentPassport.toString())
                    currentPassport.clear()
                }
            }

            // Add the last passport if it exists
            if (currentPassport.isNotEmpty()) {
                passports.add(currentPassport.toString())
            }
        } catch (e: IOException) {
            println("An error occurred while reading the file: ${e.message}")
        }
        return passports
    }

    /**
     * This function splits a list in X other lists with a similar number of elements
     * We will use this for splitting the large list of passports into smaller ones
     * to take advantage of multi threading.
     *
     * @param x: Number of lists to create. Should be equal to the number of threads.
     * @return A list of X lists of approximately equal size
     */
    private fun <T> splitList(original: List<T>, x: Int): List<List<T>> {
        val result = mutableListOf<List<T>>()
        val sublistSize = original.size / x
        var startIndex = 0
        var endIndex = sublistSize

        for (i in 1..x) {
            // For the last sublist, make sure to take all remaining elements
            if (i == x) endIndex = original.size
            result.add(original.subList(startIndex, endIndex))
            startIndex = endIndex
            endIndex += sublistSize
        }

        return result
    }

    /**
     * This function validates a passport by checking if it contains the 7 mandatory fields.
     * @param passport: A string containing passport fields.
     * @return Boolean for whether the passport is valid or not.
     */
    private fun isValidPassport(passport: String): Boolean {
        val requiredFields = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")

        // Check if each required field is present in the passport string
        for (field in requiredFields) {
            if (!passport.contains("$field:")) {
                return false
            }
        }

        return true
    }

    /**
     * This function executes passport validation using multiple threads.
     * @param passportList: A list of passport strings to be validated
     * @param numberOfThreads: Integer stating the number of threads to be used
     * @return Number of valid passports
     */
    private fun checkMultiThreaded(passportList: List<String>, numberOfThreads: Int): Int {
        val executor = Executors.newFixedThreadPool(numberOfThreads)
        val passports = splitList(passportList, numberOfThreads)
        val validPass = AtomicInteger(0)

        for (sublist in passports) {
            executor.submit {
                sublist.forEach { passport ->
                    if (isValidPassport(passport)) {
                        validPass.incrementAndGet()
                    }
                }
            }
        }

        executor.shutdown()
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
        }

        return validPass.get()
    }

    /**
     * Performs passport validation with a single thread.
     * @return Number of valid passports
     */
    private fun checkSingleThreaded(passports: List<String>): Int {
        var validPass = 0

        passports.forEach { passport -> if (isValidPassport(passport)) validPass += 1 }

        return validPass
    }

    /**
     * Measures the time of execution for a given method
     * @return the result and duration
     */
    private fun <T> measureExecutionTime(block: () -> T): Pair<T, Long> {
        var result: T
        val duration = measureTimeMillis {
            result = block()
        }
        return result to duration
    }

    /**
     * Executes the passport validation both in Single and Multi thread modes.
     * @return the results and durations for each computation.
     */
    fun execute(): ExecutionResult {
        val passports = readFileContents()

        val (result2, duration2) = measureExecutionTime {
            checkMultiThreaded(passports, 4)
        }

        val (result1, duration1) = measureExecutionTime {
            checkSingleThreaded(passports)
        }

        return ExecutionResult(result1, duration1, result2, duration2)
    }
}