package com.mvgreen.utilities

import java.util.*

class SequenceData(
    var values: DoubleArray
) {
    var expectedValue: Double = Double.NaN
    var variance: Double = Double.NaN
    var eps: Double = 0.0
    private var _correlations: SortedMap<Int, Pair<Double, Double>> = sortedMapOf()
    val correlation = object : CorrelationPairs {
        override operator fun get(t: Int): Double {
            if (!_correlations.containsKey(t))
                findCorr(t)
            return _correlations[t]!!.first
        }
    }
    val ncf = object : CorrelationPairs {
        override operator fun get(t : Int): Double {
            if (!_correlations.containsKey(t))
                findCorr(t)
            return _correlations[t]!!.second
        }
    }

    init {
        updateExpectedValue()
        updateVariance()
    }


    private fun findCorr(t: Int) {
        if (t < 0) {
            _correlations[t] = Pair(0.0, 0.0)
            return
        }

        var sum = 0.0
        for (i in 0 until (values.size - t))
            sum += (values[i] - expectedValue) * (values[i + t] - expectedValue)

        val R = sum / (values.size - t - 1)
        val r = R / variance

        _correlations[t] = Pair(R, r)
    }

    fun updateExpectedValue() {
        var avgVal = 0.0

        for (v in values) {
            avgVal += v
        }
        avgVal /= values.size

        expectedValue = avgVal
    }

    fun updateVariance() {
        var a = 0.0
        for (v in values) {
            a += (v - expectedValue) * (v - expectedValue)
        }
        a /= (values.size - 1)

        variance = a
    }
}

interface CorrelationPairs {
    operator fun get(t: Int): Double
    fun asArray(size: Int): DoubleArray {
        val result = DoubleArray(size)
        for (i in 0 until size)
            result[i] = this[i]
        return result
    }
}