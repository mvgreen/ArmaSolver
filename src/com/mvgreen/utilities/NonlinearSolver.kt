package com.mvgreen.utilities

import java.lang.IllegalArgumentException
import kotlin.math.pow
import kotlin.math.sqrt

object NonlinearSolver {
    fun solve(
        M: Int = 0,
        N: Int = 0,
        correlation: DoubleArray,
        beta: DoubleArray = doubleArrayOf(-1.0),
        eps: Double = 0.00001,
        maxIterations: Int = 100
    ): DoubleArray? {

        val alphaOld = DoubleArray(N + 1)
        val alphaNew = DoubleArray(N + 1)

        alphaOld[0] = sqrt(correlation[0])
        alphaNew[0] = alphaOld[0]
        val betaMatrix = matrix(beta)

        for (iterations in 1..maxIterations) {
            for (i in N downTo 1) {
                var w = 0.0
                for (k in i + 1..N)
                    w += alphaOld[k] * abFun(alphaNew, beta, k - i)
                alphaNew[i] = (-corrSum(betaMatrix, correlation, i) - w) / alphaOld[0]
            }

            var checkSum = 0.0
            for (i in 1..N)
                checkSum += alphaNew[i] * abFun(alphaNew, beta, i)

            if (-corrSum(betaMatrix, correlation, 0) - checkSum < 0)
                return null

            alphaNew[0] = sqrt(-corrSum(betaMatrix, correlation, 0) - checkSum)

            var norm = 0.0
            for (i in 0..N)
                norm += (alphaOld[i] - alphaNew[i]).pow(2)
            norm = sqrt(norm)

            if (norm < eps)
                return alphaNew

            for (i in 0..N)
                alphaOld[i] = alphaNew[i]
        }
        return null
    }

    private fun abFun(alpha: DoubleArray, beta: DoubleArray, index: Int): Double {
        return when (index) {
            0 ->
                0.0
            1 ->
                alpha.getOrZ(0) * beta.getOrZ(1) + alpha.getOrZ(1)
            2 ->
                alpha.getOrZ(0) *
                        (beta.getOrZ(1).pow(2) + beta.getOrZ(2)) +
                        alpha.getOrZ(1) * beta.getOrZ(1) +
                        alpha.getOrZ(2)
            3 -> alpha.getOrZ(0) *
                    (beta.getOrZ(1).pow(3) + 2 * beta.getOrZ(1) * beta.getOrZ(2) + beta.getOrZ(3)) +
                    alpha.getOrZ(1) * (beta.getOrZ(1).pow(2) + beta.getOrZ(2)) +
                    alpha.getOrZ(2) * beta.getOrZ(1) +
                    alpha.getOrZ(3)
            4 ->
                0.0
            else ->
                throw IllegalArgumentException()
        }
    }

    private fun corrSum(betaMatrix: Matrix, correlation: DoubleArray, index: Int): Double {
        var sum = 0.0
        for (i in 0 until betaMatrix.columns)
            sum += betaMatrix[index, i] * correlation[i]
        return sum
    }

    private fun matrix(beta: DoubleArray) = Matrix(4, 4) { i, j ->
        when {
            i < j ->
                beta.getOrZ(i + j)
            (i - j) != (i + j) ->
                beta.getOrZ(i - j) + beta.getOrZ(i + j)
            else ->
                beta.getOrZ(i - j)
        }
    }
}