package com.mvgreen.utilities

import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.LegendItem
import org.jfree.chart.LegendItemSource
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.block.BlockBorder
import org.jfree.chart.plot.ValueMarker
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.chart.title.LegendTitle
import org.jfree.data.Range
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.ui.RectangleEdge
import org.jfree.ui.RectangleInsets
import java.awt.*
import javax.swing.JFrame
import kotlin.math.exp
import kotlin.math.sqrt


object Plotter {
    class PlotterInstance {
        var panel = ChartPanel(null)
        val renderer = XYLineAndShapeRenderer(true, false)
        val dataset = XYSeriesCollection()
        val frame = JFrame().apply {
            name = ""
            size = Dimension(600, 600)
            contentPane = panel.apply {
                chart = JFreeChart(
                    name,
                    XYPlot(
                        dataset,
                        NumberAxis().apply { autoRangeIncludesZero = false },
                        NumberAxis().apply { autoRangeIncludesZero = false },
                        renderer
                    )
                )
            }
            isVisible = true
        }
    }

    const val SRC_SEQUENCE = 0
    const val MODEL_SEQUENCE = 5

    private val plotters: ArrayList<PlotterInstance> = ArrayList(5)

    init {
        plotters.apply {
            initPlotter(0) { g ->
                g.panel.chart.xyPlot.apply {
                    domainAxis.range = Range(0.0, 150.0)
                    renderer.setSeriesPaint(2, Color.GREEN)
                    renderer.setSeriesPaint(3, Color.GREEN)
                    renderer.setSeriesVisibleInLegend(3, false)
                    domainAxis.label = "Индекс"
                    rangeAxis.label = "Значения случайного процесса"
                }
            }
            initPlotter(1) { g ->
                g.panel.chart.xyPlot.apply {
                    domainAxis.range = Range(0.0, 10.0)
                    rangeAxis.range = Range(-1.5, 1.5)
                    renderer.setSeriesPaint(1, Color.BLUE)
                    renderer.setSeriesPaint(2, Color.BLUE)
                    renderer.setSeriesVisibleInLegend(2, false)
                    domainAxis.label = "Индекс"
                    rangeAxis.label = "Нормированная корреляционная функция"
                }
                g.frame.location = Point(plotters[0].frame.location.x + 700, plotters[0].frame.location.y)
            }
            initPlotter(2) { g ->
                g.panel.chart.xyPlot.apply {
                    domainAxis.range = Range(0.0, 10.0)
                    rangeAxis.range = Range(-1.5, 1.5)
                    renderer.setSeriesPaint(0, Color.RED)
                    renderer.setSeriesPaint(1, Color.GREEN)
                    renderer.setSeriesPaint(2, Color.BLUE)
                    domainAxis.label = "Индекс"
                    rangeAxis.label = "Нормированная корреляционная функция"
                }
            }
            initPlotter(3) { g ->
                g.panel.chart.xyPlot.apply {
                    domainAxis.range = Range(0.0, 10.0)
                    rangeAxis.range = Range(-1.5, 1.5)
                    renderer.setSeriesPaint(0, Color.RED)
                    renderer.setSeriesPaint(1, Color.GREEN)
                    renderer.setSeriesPaint(2, Color.BLUE)
                    domainAxis.label = "Индекс"
                    rangeAxis.label = "Нормированная корреляционная функция"
                }
            }
            initPlotter(4) { g ->
                g.panel.chart.xyPlot.apply {
                    domainAxis.range = Range(0.0, 10.0)
                    rangeAxis.range = Range(-1.5, 1.5)
                    renderer.setSeriesPaint(0, Color.RED)
                    renderer.setSeriesPaint(1, Color.GREEN)
                    renderer.setSeriesPaint(2, Color.BLUE)
                    domainAxis.label = "Индекс"
                    rangeAxis.label = "Нормированная корреляционная функция"
                }
            }
            initPlotter(5) { g ->
                g.panel.chart.xyPlot.apply {
                    domainAxis.range = Range(0.0, 150.0)
                    renderer.setSeriesPaint(2, Color.GREEN)
                    renderer.setSeriesPaint(3, Color.GREEN)
                    renderer.setSeriesVisibleInLegend(3, false)
                    domainAxis.label = "Индекс"
                    rangeAxis.label = "Значения смоделированного процесса"
                }
            }
        }
    }

    private fun initPlotter(index: Int, init: (PlotterInstance) -> Unit) {
        if (index == plotters.size)
            plotters.add(PlotterInstance())
        init(plotters[index])
    }

    fun drawSequencePlot(index: Int, seq: SequenceData) {
        addLine(index, "Процесс", PlottedSequence(sequence = seq))
        val d1 = seq.expectedValue + sqrt(seq.variance)
        val d2 = seq.expectedValue - sqrt(seq.variance)
        addLine(index, "Среднее значение", PlottedSequence { seq.expectedValue })
        addLine(index, "Стандартное отклонение", PlottedSequence { d1 })
        addLine(index, "", PlottedSequence { d2 })
    }

    fun drawCorrelationPlot(seq: DoubleArray, marker: Int) {
        addLine(1, "НКФ", PlottedSequence(start = 0, end = 10, step = 1, sequence = SequenceData(seq)))
        addLine(1, "1/e, -1/e", PlottedSequence(start = 0, end = 10, step = 10) { exp(-1.0) })
        addLine(1, "", PlottedSequence(start = 0, end = 10, step = 10) { -exp(-1.0) })
        addMarker(1, "Интервал корреляции", marker)
    }

    fun drawComparison(index: Int, src: DoubleArray, model: DoubleArray, imitation: DoubleArray, name: String) {
        addLine(index + 2, "Исходная", PlottedSequence(start = 0, end = 10, step = 1, sequence = SequenceData(src)))
        addLine(
            index + 2,
            "Модель $name",
            PlottedSequence(start = 0, end = 10, step = 1, sequence = SequenceData(model))
        )
        addLine(
            index + 2,
            "Имитация $name",
            PlottedSequence(start = 0, end = 10, step = 1, sequence = SequenceData(imitation))
        )
    }

    private fun addLine(index: Int, name: String, seq: PlottedSequence) {
        val series = XYSeries(name)
        var x = seq.start
        while (x <= seq.end) {
            series.add(x, seq(x))
            x += seq.step
        }
        plotters[index].dataset.addSeries(series)
        plotters[index].frame.invalidate()
    }

    private fun addMarker(index: Int, name: String, marker: Int) {
        val g = plotters[index]
        (g.panel.chart.plot as XYPlot).addDomainMarker(ValueMarker(marker.toDouble(), Color.GREEN, BasicStroke(2.0F)))
        val newLegend = g.renderer.legendItems
        newLegend.add(LegendItem(name, Color.GREEN).apply { shape = Rectangle(15, 1) })
        with(g.panel.chart) {
            removeLegend()
            addLegend(LegendTitle(LegendItemSource { newLegend }))
            legend.margin = RectangleInsets(1.0, 1.0, 1.0, 1.0)
            legend.position = RectangleEdge.BOTTOM
            legend.backgroundPaint = Color.WHITE
            legend.frame = BlockBorder(Color.BLACK)
        }
    }

}