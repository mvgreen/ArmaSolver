package com.mvgreen.utilities

import java.util.Locale.ENGLISH as EN
import java.lang.IllegalStateException
import kotlin.math.absoluteValue
import kotlin.math.pow

object Task24 {
    fun solve() {
        println("Задание 2.4")
        val seq = ProjectData.srcSequence
        for (M in 1..3)
            for (N in 1..3) {
                buildModel(M, N, seq!!)
            }
        println()
        printSeparator()
    }

    private fun buildModel(M: Int, N: Int, seq: SequenceData) {
        val A = Matrix(Array(M) { row ->
            DoubleArray(M) { column ->
                seq.correlation[N + row - column]
            }
        })
        val B = Matrix(M, 1, *DoubleArray(M) { i -> seq.correlation[N + i + 1] })
        val rawBeta = (A leftDiv B).asArray()


        val isStable = when (rawBeta.size) {
            1 -> (rawBeta[0].absoluteValue < 1)
            2 -> (rawBeta[1].absoluteValue < 1 && rawBeta[0].absoluteValue < 1 - rawBeta[1])
            3 -> (rawBeta[2].absoluteValue < 1 &&
                    (rawBeta[0] + rawBeta[2]).absoluteValue < 1 - rawBeta[1] &&
                    (rawBeta[1] + rawBeta[0] * rawBeta[2]).absoluteValue < 1 - rawBeta[2].pow(2))
            else -> throw IllegalStateException()
        }

        if (!isStable) {
            println("Модель АРСС($M, $N) неустойчива")
            for (i in 0 until rawBeta.size)
                println("beta${i + 1} = ${"%.4f".format(EN, rawBeta[i])}")
            println()
            return
        }


        val specialBeta = DoubleArray(M + 1) { i ->
            if (i == 0)
                -1.0
            else
                rawBeta[i - 1]
        }

        val alpha = NonlinearSolver.solve(M, N, seq.correlation.asArray(4), specialBeta)

        if (alpha == null) {
            println("Модель АРСС($M, $N) не существует")
        }

        println("Модель АРСС($M, $N):")
        for (i in 1 until specialBeta.size)
            println("beta${i} = ${"%.4f".format(EN, specialBeta[i])}")
        if (alpha == null) {
            println()
            return
        }
        for (i in 0 until alpha.size)
            println("alpha$i = ${"%.4f".format(EN, alpha[i])}")

        print("Значения теоретической НКФ: ")
        var eps = 0.0
        val tNcf = DoubleArray(11)
        for (n in 0..10) {
            val ncf = ARMATheorCorrelation(M, N, n, specialBeta, seq.correlation.asArray(N + M + 1)) / seq.variance
            tNcf[n] = ncf
            eps += (ncf - seq.ncf[n])
                .pow(2)
            print("${"%.4f".format(EN, ncf)} ")
        }
        println()
        println("Погрешность модели: $eps")
        println()

        if (eps < ProjectData.bestARMA.eps)
            with(ProjectData.bestARMA) {
                this.eps = eps
                this.M = M
                this.N = N
                this.alpha = alpha
                this.beta = specialBeta.copyOfRange(1, specialBeta.size)
                this.ncf = tNcf
            }
    }


    fun ARMATheorCorrelation(M: Int, N: Int, n: Int, specialBeta: DoubleArray, selectedCor: DoubleArray): Double {
        return if (n <= N + M)
            selectedCor[n]
        else {
            var sum = 0.0
            for (i in 1..M)
                sum += specialBeta[i] * ARMATheorCorrelation(M, N, n - i, specialBeta, selectedCor)
            sum
        }
    }
}