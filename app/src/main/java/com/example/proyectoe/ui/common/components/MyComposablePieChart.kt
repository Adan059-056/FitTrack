package com.example.proyectoe.ui.common.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
import android.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry


@Composable
fun MyComposePieChart(
    modifier: Modifier = Modifier,
    data: Map<String, Float>,
    segmentColors: List<Int>,
    customLegendEntries: List<LegendEntry>
) {
    AndroidView(
        modifier = modifier,

        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                setUsePercentValues(true)
                setDrawEntryLabels(false)
                setDrawHoleEnabled(true)
                holeRadius = 58f
                transparentCircleRadius = 61f

                //centerText = "Mis Datos"

                setHoleColor(Color.TRANSPARENT)
                setCenterTextSize(10f)

                //para ajustar los textos debajo de la grafica
                val legend = legend
                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
                legend.xEntrySpace = 20f
                legend.yEntrySpace = 0f
                legend.yOffset = 5f
                legend.textSize = 10f
                legend.textColor = Color.WHITE

                //permite textos o leyendas personalizadas
                legend.setCustom(customLegendEntries)

                rotationAngle = 0f
                isRotationEnabled = false
                setHighlightPerTapEnabled(false)

            }
        },
        update = { pieChart ->
            val entries = ArrayList<PieEntry>()
            data.forEach { (label, value) ->
                entries.add(PieEntry(value, label))
            }

            val dataSet = PieDataSet(entries, "Objetivo").apply {
                colors = segmentColors
                sliceSpace = 2f
                selectionShift = 0f
            }
            //para ajustar los valores de la grafica

            val pieData = PieData(dataSet).apply {//muestra los porcentajes de la grafica

                setValueFormatter(com.github.mikephil.charting.formatter.PercentFormatter(pieChart))

            //para valores enteros:: setValueFormatter(com.github.mikephil.charting.formatter.ValueFormatter())
                setDrawValues(false)
            }


            //pieData.setValueTextSize(12f)
            //pieData.setValueTextColor(Color.WHITE)

            pieChart.data = pieData


            pieChart.setDrawEntryLabels(false) // No mostrar etiquetas dentro de la gráfica
            pieChart.description.isEnabled = false
            pieChart.legend.isEnabled = true

            //pieChart.animateY(500, Easing.EaseInOutQuad) // Animación
            pieChart.invalidate() // Redibujar el gráfico
        }
    )
}