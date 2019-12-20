package com.mvgreen.utilities

data class ArmaModel(
    var M: Int = 0,
    var N: Int = 0,
    var beta: DoubleArray = doubleArrayOf(),
    var alpha: DoubleArray = doubleArrayOf(),
    var ncf: DoubleArray = doubleArrayOf(),
    var eps: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArmaModel

        if (M != other.M) return false
        if (N != other.N) return false
        if (!beta.contentEquals(other.beta)) return false
        if (!alpha.contentEquals(other.alpha)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = M
        result = 31 * result + N
        result = 31 * result + beta.contentHashCode()
        result = 31 * result + alpha.contentHashCode()
        return result
    }
}