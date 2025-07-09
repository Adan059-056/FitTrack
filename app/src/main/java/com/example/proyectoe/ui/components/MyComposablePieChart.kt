package com.example.proyectoe.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

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
                centerText = "Mis Datos"
                setCenterTextSize(10f)
                legend.isEnabled = true
            }
        },
        update = { pieChart ->
            val entries = ArrayList<PieEntry>()
            data.forEach { (label, value) ->
                entries.add(PieEntry(value, label))
            }

            val dataSet = PieDataSet(entries, "Categorías").apply {
                colors = ColorTemplate.COLORFUL_COLORS.toList()
                sliceSpace = 2f
                selectionShift = 5f
            }

            val pieData = PieData(dataSet)
            pieData.setValueTextSize(11f)
            pieData.setValueTextColors(ColorTemplate.COLORFUL_COLORS.toList())

            pieChart.data = pieData
            pieChart.invalidate()
        }
    )
}