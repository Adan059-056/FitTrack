package com.example.proyectoe.data.datasource

import com.example.proyectoe.data.model.Lession
import com.example.proyectoe.data.model.Workout

object WorkoutDataProvider {

    fun getData(): List<Workout> = listOf(
        Workout("Running", "Ejercicios básicos para movilizarte y adaptar tu cuerpo para una vida más activa", "running", 160, "41 min", getLessions1()),
        Workout("Calentamiento", "Estiramiento tiene como objetivo principal mejorar la flexibilidad muscular y la movilidad de las articulaciones", "pic_2", 230, "30 min", getLessions2()),
        Workout("Yoga", "Una práctica milenaria que integra cuerpo, mente y respiración donde cada postura (asana) activa distintas partes del cuerpo y que combina movimiento con respiración consciente, lo que calma el sistema nervioso.", "pic_3", 180, "69 min", getLessions3())
    )

    fun getLessions1() = listOf(
        Lession("Sesion 1", "06:59", "X01SZlaeARQ", "pic_1_1"),
        Lession("Sesion 2", "13:07", "kLl14nXbD3c", "pic_1_2"),
        Lession("Sesion 3", "21:34", "Y_DymGhU-oo", "pic_1_3"),
    )
    fun getLessions2() = listOf(
        Lession("Sesion 1", "12:11", "YnePZyRGcBE", "pic_2_1"),
        Lession("Sesion 2", "11:32", "0GPm3qbmju0", "pic_2_2"),
        Lession("Sesion 3", "04:49", "XabAVprmsPE", "pic_2_3"),
        Lession("Sesion 4", "03:29", "5aAkskctYGY", "pic_2_4")
    )
    fun getLessions3() = listOf(
        Lession("Sesion 1", "38:12", "shvcnWXkDiY", "pic_3_1"),
        Lession("Sesion 2", "13:28", "qMzk83G5JgY", "pic_3_2"),
        Lession("Sesion 3", "07:25", "sETvbV0oobI", "pic_3_3"),
        Lession("Sesion 4", "11:10", "FU4OTllcOXM", "pic_3_4")
    )

}