package com.mvgreen.utilities

import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.random.asJavaRandom
import java.util.Locale.ENGLISH as EN

object Task25 {
    fun solve() {
        println("Задание 2.5")
        val ar = buildRP(ProjectData.bestAR, "АР")
        val ma = buildRP(ProjectData.bestMA, "СС")
        val arma = buildRP(ProjectData.bestARMA, "АРСС")
        Plotter.drawComparison(
            0,
            ProjectData.srcSequence!!.ncf.asArray(11),
            ProjectData.bestAR.ncf,
            ar.ncf.asArray(11),
            "АР(${ProjectData.bestAR.M})"
        )

        Plotter.drawComparison(
            1,
            ProjectData.srcSequence!!.ncf.asArray(11),
            ProjectData.bestMA.ncf,
            ma.ncf.asArray(11),
            "СС(${ProjectData.bestMA.N})"
        )

        Plotter.drawComparison(
            2,
            ProjectData.srcSequence!!.ncf.asArray(11),
            ProjectData.bestARMA.ncf,
            arma.ncf.asArray(11),
            "АРСС(${ProjectData.bestARMA.M}, ${ProjectData.bestARMA.N})"
        )
        ProjectData.bestSequenceModel = sequenceOf(ar, ma, arma).minBy { it.eps }
    }

    private fun buildRP(model: ArmaModel, name: String): SequenceData {
        val M = model.M
        val N = model.N
        val beta = model.beta
        val alpha = model.alpha
        val targetExpected = ProjectData.srcSequence!!.expectedValue
        val expModifier = targetExpected * (1 - beta.sum())

        val rand = Random(System.currentTimeMillis()).asJavaRandom()

        val xiValues = DoubleArray(11_000) { rand.nextGaussian() }

        val sequenceModel = SequenceData(DoubleArray(11_000))

        for ((xiIndex, seqIndex) in (0 until 11_000).withIndex()) {
            var sum = 0.0
            for ((i, a) in alpha.withIndex())
                sum += a * xiValues.getOrZ(xiIndex - i)
            // Здесь сдвиг на 1, т.к. в beta[0] лежит значение b1
            for (i in 1..M)
                sum += beta.getOrZ(i - 1) * sequenceModel.values.getOrZ(seqIndex - i)
            sum += expModifier
            sequenceModel.values[seqIndex] = sum
        }

        sequenceModel.values = sequenceModel.values.copyOfRange(1000, sequenceModel.values.size)

        sequenceModel.updateExpectedValue()
        sequenceModel.updateVariance()

        val srcNcf = ProjectData.srcSequence!!.ncf.asArray(11)
        val modelEps = sequenceModel.ncf.asArray(11).asSequence().mapIndexed { i, d -> (d - srcNcf[i]).pow(2) }.sum()
        sequenceModel.eps = modelEps

        println("$name($M, $N)")
        println(
            """Выборочное среднее: ${"%.4f".format(EN, sequenceModel.expectedValue)}
            |Дисперсия: ${"%.4f".format(EN, sequenceModel.variance)}
            |СКО: ${"%.4f".format(EN, sqrt(sequenceModel.variance))}
            |Теоретическая НКФ:
            |${model.ncf.joinToString(separator = " ") { "%.4f".format(EN, it) }}
            |НКФ модели:
            |${sequenceModel.ncf.asArray(11).joinToString(separator = " ") { "%.4f".format(EN, it) }}
            |Теоретическая погрешность модели: ${"%.4f".format(EN, model.eps)}
            |Погрешность смоделированного процесса: ${"%.4f".format(EN, modelEps)}""".trimMargin()
        )
        println()
        return sequenceModel
    }
}