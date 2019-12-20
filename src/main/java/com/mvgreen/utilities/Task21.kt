package com.mvgreen.utilities

import com.mvgreen.utilities.Plotter.SRC_SEQUENCE
import com.mvgreen.utilities.ProjectData as PD
import java.util.Locale.ENGLISH as EN
import com.mvgreen.utilities.ProjectData.REVERSED_E
import java.io.File
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.sqrt

object Task21 {
    fun solve() {
        val seq = SequenceData(initData())
        PD.srcSequence = seq

        val normalizedCorrelations = DoubleArray(11)
        for (i in 0 until 11)
            normalizedCorrelations[i] = seq.ncf[i]
        val corInterval = corInterval(normalizedCorrelations)

        printResults(seq, corInterval, normalizedCorrelations)
    }

    fun printResults(
        seq: SequenceData,
        corInterval: Int,
        normalizedCorrelations: DoubleArray
    ) {
        println(
            """
            |Задание 2.1
            |Выборочное среднее: ${"%.4f".format(EN, seq.expectedValue)}
            |Дисперсия: ${"%.4f".format(EN, seq.variance)}
            |Среднее квадратичное отклонение: ${"%.4f".format(EN, sqrt(seq.variance))}
            |Коридор наиболее вероятных значений:
            |[${"%.4f".format(EN, seq.expectedValue - sqrt(seq.variance))}, ${"%.4f".format(
                EN,
                seq.expectedValue + sqrt(seq.variance)
            )}]
            |Корреляция и НКФ (первые 11 значений):""".trimMargin()
        )
        for (i in 0 until 11) {
            println("${"%.4f".format(EN, seq.correlation[i])}, ${"%.4f".format(EN, seq.ncf[i])}")
        }
        println("Интервал корреляции: $corInterval")
        println()
        println()
        printSeparator()
        Plotter.drawSequencePlot(SRC_SEQUENCE, seq)
        Plotter.drawCorrelationPlot(normalizedCorrelations, corInterval)
    }

    fun initData(): DoubleArray {
        val scanner = Scanner(File("""C:\test\test.txt""".trimMargin())).apply { useLocale(Locale.US) }
        val data = DoubleArray(PD.POINTS_COUNT)

        var i = 0
        while (scanner.hasNext()) {
            data[i] = scanner.nextDouble()
            i++
        }
        return data
    }

    fun corInterval(cor: DoubleArray): Int {
        var i = cor.size - 1
        while (i >= 0 && cor[i].absoluteValue < REVERSED_E)
            i--
        if (i == cor.size + 1)
            i = -1
        return i
    }
}