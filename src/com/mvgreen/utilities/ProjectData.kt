package com.mvgreen.utilities

import kotlin.math.E

object ProjectData {
    const val POINTS_COUNT = 10000
    const val REVERSED_E = 1.0 / E

    var srcSequence: SequenceData? = null
    var bestSequenceModel: SequenceData? = null

    val bestAR = ArmaModel(eps = Double.POSITIVE_INFINITY)
    val bestMA = ArmaModel(eps = Double.POSITIVE_INFINITY)
    val bestARMA = ArmaModel(eps = Double.POSITIVE_INFINITY)

}