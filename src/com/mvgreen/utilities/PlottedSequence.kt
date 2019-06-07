package com.mvgreen.utilities

class PlottedSequence(
    val start: Int = 0,
    val end: Int = 150,
    val step: Int = 1,
    val sequence: SequenceData? = null,
    val function: (x: Int) -> Double = { 0.0 }
) {

    operator fun invoke(i: Int): Double {
        return if (sequence == null || sequence.values.size <= i)
            function(i)
        else
            sequence.values[i]
    }

}