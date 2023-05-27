package com.example.mor.nytnews

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun flowFlatMapLatestExample() = runBlocking {
        val flow1 = flow {
            for (i in 1..5) {
                delay(1000)
                emit(i)
            }
        }

        val flow2 = flow1.flatMapLatest { value ->
            println("flow 1 value: $value")
            flow {
                    delay(1500)
                    emit(value * 10)

            }
        }

        flow2.collect { value ->
            println("Flow 2: $value")
        }
    }

    @Test
    fun flowFlatMapMergeExample() = runBlocking {
        val flow1 = flow {
            for (i in 1..3) {
                delay(1000)
                emit(i)
            }
        }

        val flow2 = flow1.flatMapMerge { value ->
            println("flow 1 value: $value")

            flow {
                for (j in 1..value) {
                    delay(500)
                    emit("Flow2: $j")
                }
            }
        }

        flow2.collect { value ->
            println(value)
        }
    }
}