package com.example.proyectoe.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
import android.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;


@Composable
fun MyComposePieChart(
    modifier: Modifier = Modifier,
    data: Map<String, Float>
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

                val legend = legend
                legend.isEnabled = true // Asegúrate de que la leyenda esté habilitada
                legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER // Alineación vertical
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT // Alineación horizontal a la derecha
                legend.orientation = Legend.LegendOrientation.VERTICAL // Orientación vertical de los ítems
                legend.setDrawInside(false) // Dibuja la leyenda fuera del gráfico
                legend.xEntrySpace = 7f // Espacio entre ítems horizontalmente
                legend.yEntrySpace = 0f // Espacio entre ítems verticalmente
                legend.yOffset = 0f // Desplazamiento en el eje Y

                // Ajustar el tamaño del texto de la leyenda
                legend.textSize = 20f // Cambia el tamaño del texto, por ejemplo a 12f

                // Ajustar el color del texto de la leyenda
                legend.textColor = Color.WHITE

            }
        },
        update = { pieChart ->
            val entries = ArrayList<PieEntry>()
            data.forEach { (label, value) ->
                entries.add(PieEntry(value, label))
            }

            val dataSet = PieDataSet(entries, "Objetivo").apply {
                colors = ColorTemplate.COLORFUL_COLORS.toList()
                sliceSpace = 2f
                selectionShift = 5f
            }

            val pieData = PieData(dataSet)
            pieData.setValueTextSize(11f)
            pieData.setValueTextColors(ColorTemplate.COLORFUL_COLORS.toList())

            pieChart.data = pieData
            pieChart.animateY(1400, Easing.EaseInOutQuad)

            pieChart.invalidate()
        }
    )
}