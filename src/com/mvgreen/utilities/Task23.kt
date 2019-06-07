package com.mvgreen.utilities

import java.util.Locale.ENGLISH as EN
import kotlin.math.pow

object Task23 {
    fun solve() {
        println("Задание 2.3")
        val seq = ProjectData.srcSequence
        for (N in 0..3) {
            val result = NonlinearSolver.solve(N = N, correlation = seq!!.correlation.asArray(4))
            printAndCompareResults(N, result, seq)
        }
        println()
        printSeparator()
    }

    private fun printAndCompareResults(N: Int, result: DoubleArray?, seq: SequenceData) {
        println("СС($N):")
        if (result == null) {
            println("Решение системы не найдено")
            return
        }
        for (i in 0 until result.size)
            println("alpha$i = ${"%.4f".format(EN, result[i])}")

        print("Значения теоретической НКФ: ")
        val tNcf = DoubleArray(11)
        var eps = 0.0
        for (n in 0..10) {
            val tCor = MATheorCorr(N, n, result, seq.correlation.asArray(4)) / seq.variance
            tNcf[n] = tCor
            eps += (tCor - seq.ncf[n]).pow(2)
            print("${"%.4f".format(EN, tCor)} ")
        }
        println()
        println("Погрешность: ${"%.8f".format(EN, eps)}")
        println()
        if (eps < ProjectData.bestMA.eps)
            with(ProjectData.bestMA) {
                this.eps = eps
                this.N = N
                this.alpha = result
                this.ncf = tNcf
            }
    }

    private fun MATheorCorr(N: Int, n: Int, multipliers: DoubleArray, correlation: DoubleArray): Double {
        return if (n <= N) {
            correlation[n]
        } else
            0.0
    }
}