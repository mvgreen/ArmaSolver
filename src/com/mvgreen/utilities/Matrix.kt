package com.mvgreen.utilities

import kotlin.math.absoluteValue

class Matrix(
    val rows: Int,
    val columns: Int,
    private val epsilon: Double = 0.000000001,
    init: (row: Int, column: Int) -> Double = { _, _ -> 0.0 }
) {

    constructor(arr: Array<DoubleArray>) : this(rows = arr.size, columns = arr[0].size, init = { r, c ->
        val cols = arr[0].size
        for (i in 0 until arr.size) {
            if (arr[i].size != cols)
                throw IllegalArgumentException("Matrix must have the same column number in every row.")
        }
        arr[r][c]
    })

    constructor(rows: Int, columns: Int, vararg values: Double) : this(rows, columns, init = { i, j ->
        values[j + columns * i]
    })

    val size: Int
        get() {
            if (columns != 1 && rows != 1)
                throw IllegalStateException("This matrix is not a vector")
            return if (columns == 1)
                rows
            else
                columns
        }
    val matrixArray: Array<DoubleArray> = Array(rows) { row ->
        DoubleArray(columns) { column ->
            init(row, column)
        }
    }


    /** Сеттеры */
    operator fun set(i: Int, j: Int, value: Double) {
        matrixArray[i][j] = value
    }

    operator fun set(index: Int, value: Double) {
        if (rows != 1 && columns != 1)
            throw IllegalStateException("This matrix is not a vector")

        if (rows == 1)
            this[0, index] = value
        else
            this[index, 0] = value
    }

    operator fun set(rowRange: IntRange, column: Int, value: Matrix) {
        if ((value.rows != rowRange.last - rowRange.first + 1) || (value.columns != 1))
            throw ArithmeticException("Incompatible matrices")

        for ((j, i) in rowRange.withIndex()) {
            this[i, column] = value[j, 0]
        }
    }

    operator fun set(row: Int, columnRange: IntRange, value: Matrix) {
        if ((value.rows != 1) || (value.columns != columnRange.last - columnRange.first + 1))
            throw ArithmeticException("Incompatible matrices")

        for ((i, j) in columnRange.withIndex()) {
            this[row, j] = value[0, i]
        }
    }

    operator fun set(rowRange: IntRange, columnRange: IntRange, value: Matrix) {
        if ((value.rows != rowRange.last - rowRange.first + 1) ||
            (value.columns != columnRange.last - columnRange.first + 1)
        )
            throw ArithmeticException("Incompatible matrices")

        for ((p, i) in rowRange.withIndex()) {
            for ((q, j) in columnRange.withIndex()) {
                this[i, j] = value[p, q]
            }
        }
    }

    operator fun set(intRange: IntRange, value: Matrix) {
        if (rows != 1 && columns != 1)
            throw IllegalStateException("This matrix is not a vector")

        if (rows == 1)
            this[0, intRange] = value
        else
            this[intRange, 0] = value
    }

    /** Геттеры */
    operator fun get(i: Int, j: Int): Double {
        return matrixArray[i][j]
    }

    operator fun get(index: Int): Double {
        if (rows != 1 && columns != 1)
            throw IllegalStateException("This matrix is not a vector")

        return if (rows == 1)
            this[0, index]
        else
            this[index, 0]
    }

    operator fun get(rowRange: IntRange, column: Int) =
        Matrix(rowRange.last - rowRange.first + 1, 1) { r, _ ->
            this[rowRange.first + r, column]
        }

    operator fun get(row: Int, columnRange: IntRange) =
        Matrix(1, columnRange.last - columnRange.first + 1) { _, c ->
            this[row, columnRange.first + c]
        }

    operator fun get(rowRange: IntRange, columnRange: IntRange) =
        Matrix(rowRange.last - rowRange.first + 1, columnRange.last - columnRange.first + 1) { r, c ->
            this[rowRange.first + r, columnRange.first + c]
        }

    operator fun get(intRange: IntRange): Matrix {
        if (this.rows != 1 && this.columns != 1)
            throw IllegalStateException("This matrix is not a vector")

        return if (rows == 1)
            this[0, intRange]
        else
            this[intRange, 0]
    }


    /** Операции */
    operator fun plus(other: Matrix): Matrix {
        if (this.rows != other.rows || this.columns != other.columns)
            throw ArithmeticException("Can't add matrices with different dimensions: a is ${this.rows}x${this.columns}, b is ${other.rows}x${other.columns}.")

        return Matrix(this.rows, this.columns) { row, column ->
            this[row, column] + other[row, column]
        }
    }

    operator fun minus(other: Matrix): Matrix {
        if (this.rows != other.rows || this.columns != other.columns)
            throw ArithmeticException("Can't add matrices with different dimensions: a is ${this.rows}x${this.columns}, b is ${other.rows}x${other.columns}.")

        return Matrix(this.rows, this.columns) { row, column ->
            this[row, column] - other[row, column]
        }
    }

    operator fun times(other: Matrix): Matrix {
        if (this.columns != other.rows)
            throw ArithmeticException("Can't multiply matrices with incompatible dimensions: a is ${this.rows}x${this.columns}, b is ${other.rows}x${other.columns}.")

        val m = Matrix(this.rows, other.columns)
        for (i in 0 until rows) {
            for (j in 0 until other.columns) {
                var a = 0.0
                for (p in 0 until columns)
                    a += this[i, p] * other[p, j]
                m[i, j] = a
            }
        }

        return m
    }

    operator fun times(k: Double) = Matrix(this.rows, this.columns) { r, c -> this[r, c] * k }

    operator fun Double.times(matrix: Matrix) = matrix.times(this)


    infix fun backsub(other: Matrix): Matrix {
        if (this.rows != this.columns || this.rows != other.rows || other.columns != 1)
            throw ArithmeticException("Incompatible matrices")
        for (j in 0 until rows)
            for (i in j + 1 until columns)
                if (this[i, j].absoluteValue > epsilon)
                    throw IllegalStateException("Left matrix is invalid")

        val n = other.rows
        val X = Matrix(n, 1)
        X[n - 1] = other[n - 1] / this[n - 1, n - 1]
        for (k in n - 2 downTo 0)
            X[k] = (other[k] - (this[k, k + 1 until n] * X[k + 1 until n])[0]) / this[k, k]

        return X
    }

    infix fun leftDiv(other: Matrix): Matrix {
        val A = this
        val B = other

        val N = A.rows
        val X = Matrix(N, 1)

        val Aug = Matrix(N, N + 1) { r, c -> if (c == N) B[r, 0] else A[r, c] }

        for (p in 0 until N - 1) {
            val (_, j) = Aug.max(p until N, p)

            // Свапаем строки p и j
            val C = Aug[p, 0..N]
            Aug[p, 0..N] = Aug[j, 0..N]
            Aug[j, 0..N] = C

            for (k in p + 1 until N) {
                val m = Aug[k, p] / Aug[p, p]
                Aug[k, p..N] = Aug[k, p..N] - m * Aug[p, p..N]
            }
        }

        X[0 until N, 0] = Aug[0 until N, 0 until N] backsub Aug[0 until N, N]

        return X
    }

    override fun toString(): String {
        val s = StringBuffer()
        s.append("Matrix: rows = $rows, columns = $columns. Elements:\n[\n")
        for (row in matrixArray) {
            s.append("[")
            for (elem in row)
                s.append("$elem ")
            s.append("]\n")
        }
        s.append("]")
        return s.toString()
    }

    fun max(rows: IntRange, column: Int): Pair<Double, Int> {
        var maxElement = this[rows.first, column].absoluteValue
        var maxPos = rows.first
        for (i in rows)
            if (this[i, column].absoluteValue > maxElement.absoluteValue) {
                maxElement = this[i, column]
                maxPos = i
            }
        return Pair(maxElement, maxPos)
    }

    fun asArray(): DoubleArray {
        if (rows != 1 && columns != 1)
            throw IllegalStateException("This matrix is not vector")

        return DoubleArray((if (columns == 1) rows else columns)) { i -> this[i] }
    }

}
