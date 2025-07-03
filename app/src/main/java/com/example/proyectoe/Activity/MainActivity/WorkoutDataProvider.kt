package com.example.proyectoe.Activity.MainActivity

import com.example.proyectoe.Model.Lession
import com.example.proyectoe.Model.Workout

object WorkoutDataProvider {

    fun getData(): List<Workout> = listOf(
        Workout("Running", "Desc...", "pic_1", 160, "9 min", getLessions1()),
        Workout("Stretching", "Desc...", "pic_2", 230, "85 min", getLessions2()),
        Workout("Yoga", "Desc...", "pic_3", 180, "65 min", getLessions3())
    )

    fun getLessions1() = listOf(
        Lession("Lesson 1", "03:46", "HBPMvFkpNgE", "pic_1_1"),
        Lession("Lesson 2", "03:41", "K6I24WgiiPw", "pic_1_2"),
        Lession("Lesson 3", "01:57", "Zc08v4YYOeA", "pic_1_3"),
        Lession("Lesson 3", "01:57", "Zc08v4YYOeA", "pic_1_3"),
        Lession("Lesson 3", "01:57", "Zc08v4YYOeA", "pic_1_3"),
        Lession("Lesson 3", "01:57", "Zc08v4YYOeA", "pic_1_3"),
        Lession("Lesson 3", "01:57", "Zc08v4YYOeA", "pic_1_3"),
        Lession("Lesson 3", "01:57", "Zc08v4YYOeA", "pic_1_3")
    )
    fun getLessions2() = listOf(
        Lession("Lesson 1", "20:23", "L3eImBAXT7I", "pic_3_1"),
        Lession("Lesson 2", "18:27", "47ExgzO7FlU", "pic_3_2"),
        Lession("Lesson 3", "32:25", "OmLx8tmaQ-4", "pic_3_3"),
        Lession("Lesson 4", "07:52", "w86EalEoFRY", "pic_3_4")
    )
    fun getLessions3() = listOf(
        Lession("Lesson 1", "23:00", "v7AYKMP6rOE", "pic_3_1"),
        Lession("Lesson 2", "27:00", "Eml2xnoLpYE", "pic_3_2"),
        Lession("Lesson 3", "25:00", "v7SN-d4qXx0", "pic_3_3"),
        Lession("Lesson 4", "21:00", "LqXZ628YNj4", "pic_3_4")
    )

}