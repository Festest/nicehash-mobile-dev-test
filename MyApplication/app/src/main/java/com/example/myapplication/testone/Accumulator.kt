package com.example.myapplication.testone

import java.io.IOException
import java.io.InputStream

/**
 * Create a "Triple" that will store the command type, the value and a boolean to check if we have executed it already
 */
data class LineData(var cmd: String, var value: Int, var isExecuted: Boolean)

/**
 * The Accumulator class can compute test 1. It takes an InputStream from a previously selected file.
 */
class Accumulator(private val inputStream: InputStream?) {
    /**
     * This function reads the contents of the file and creates a list of LineData.
     * We will modify the isExecuted element when we execute the command for the first time.
     *
     * @return A list of LineData
     */
    private fun readFileContents(): List<LineData> {
        val contentList: MutableList<LineData> = mutableListOf()

        try {
            inputStream?.bufferedReader()?.forEachLine { line ->
                val parts = line.split(" ")
                if (parts.size == 2) {
                    val cmd = parts[0]
                    val number = parts[1].toIntOrNull()
                    if (number != null) {
                        contentList.add(LineData(cmd, number, false))
                    }
                }
            }
        } catch (e: IOException) {
            println("An error occurred while reading the file: ${e.message}")
        }
        return contentList
    }

    /**
     * This function computes Test 1 by retrieving the contents of a file.
     *
     * @return The value of the accumulator after reaching the end of
     * the list of commands or before executing the same command twice.
     */
    fun execute(): Int {

        // Get a list of LineData with the commands
        val contentList = readFileContents()

        // Initialize variables
        var index = 0
        var acc = 0

        /* Loop until we reach the end of the list of commands
           OR Break if we are executing the same command twice. */
        while (index < contentList.size) {
            if (contentList[index].isExecuted) break

            when (contentList[index].cmd) {
                "acc" -> {
                    acc += contentList[index].value
                    contentList[index].isExecuted = true
                    index += 1
                }
                "jmp" -> {
                    contentList[index].isExecuted = true
                    index += contentList[index].value
                    continue
                }
                "nop" -> {
                    contentList[index].isExecuted = true
                    index += 1
                }
            }
        }
        return acc
    }
}
