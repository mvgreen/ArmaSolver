package com.mvgreen.utilities

fun main() {
    Task21.solve()
    Task22.solve()
    Task23.solve()
    Task24.solve()
    Task25.solve()
    Task26.solve()
}

fun DoubleArray.getOrZ(i: Int) = getOrElse(i) { 0.0 }

fun printSeparator() {
    println("______________________________________________________________________________________________________________________________" +
            "______________________________________________________________________________________________________________________________")
    println()
}