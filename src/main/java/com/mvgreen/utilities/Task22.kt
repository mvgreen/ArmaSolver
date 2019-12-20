package com.mvgreen.utilities

import java.util.Locale.ENGLISH as EN
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

object Task22 {
    fun solve() {
        println("Задание 2.2")
        val seq = ProjectData.srcSequence

        for (M in 0..3) {
            val correlations = DoubleArray((M + 1) * (M + 1))
            var arrPointer = 0
            for (k in 0..M) {
                if (M != 0)
                    for (m in 1..M) {
                        correlations[arrPointer] = seq!!.correlation[(k - m).absoluteValue]
                        arrPointer++
                    }
                correlations[arrPointer] = if (k == 0) 1.0 else 0.0
                arrPointer++
            }

            val A = Matrix(M + 1, M + 1, *correlations)
            val B = Matrix(M + 1, 1) { r, _ -> seq!!.correlation[r] }

            val result = A leftDiv B

            printAndCompareResults(result, seq!!)
            println()
        }
        println()
        printSeparator()
    }

    private fun printAndCompareResults(result: Matrix, seq: SequenceData) {
        val M = result.rows - 1
        println("АР($M):")
        for (i in 0 until result.rows - 1)
            println("beta${i + 1} = ${"%.4f".format(EN, result[i])}")
        println("alpha0 ^2 = ${"%.4f".format(EN, result[result.rows - 1])}")
        println("alpha0 = ${"%.4f".format(EN, sqrt(result[result.rows - 1]))}")

        println("Значения теоретической НКФ:")
        val tNcf = DoubleArray(11)
        var eps = 0.0
        for (m in 0..10) {
            val ncf = ARTheorCorr(M, m, result.asArray(), seq.correlation) / seq.variance
            tNcf[m] = ncf
            eps += (ncf - seq.ncf[m])
                .pow(2)
            print("${"%.4f".format(EN, ncf)} ")
        }
        println()
        println("Погрешность: ${"%.5f".format(EN, eps)}")

        if (eps < ProjectData.bestAR.eps)
            with(ProjectData.bestAR) {
                this.eps = eps
                this.M = M
                this.beta = doubleArrayOf(*result.asArray().copyOfRange(0, result.size - 1))
                this.alpha = doubleArrayOf(sqrt(result[result.size - 1]))
                this.ncf = tNcf
            }
    }

    private fun ARTheorCorr(M: Int, m: Int, multipliers: DoubleArray, correlation: CorrelationPairs): Double {
        if (m <= M)
            return correlation[m]

        var result = 0.0
        for (k in 1..M) {
            result += multipliers[k - 1] * ARTheorCorr(M, m - k, multipliers, correlation)
        }
        return result
    }

}