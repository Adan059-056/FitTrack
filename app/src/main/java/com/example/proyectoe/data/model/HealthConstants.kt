package com.example.proyectoe.data.model

import java.time.LocalDate
import java.time.Period
import android.util.Log

object CalculadoraGET {
    private val TAG = "CalculadoraGET"

    private val factoresActividad = mapOf(
        "Sedentario" to 1.2,
        "Ligero" to 1.375,
        "Moderado" to 1.55,
        "Activo" to 1.725,
        "Muy activo" to 1.9
    )

    private fun calcularEdad(fechaNacimientoString: String): Int {
        return try {
            val fechaNacimiento = LocalDate.parse(fechaNacimientoString)
            val fechaActual = LocalDate.now()
            Period.between(fechaNacimiento, fechaActual).years
        } catch (e: Exception) {
            Log.e(TAG, "Error al parsear fecha de nacimiento: $fechaNacimientoString", e)
            0
        }
    }

    private fun calcularGER(peso: Double, altura: Double, edad: Int, genero: String): Double {
        return if (genero.equals("Masculino", ignoreCase = true)) {
            (10 * peso) + (6.25 * altura) - (5 * edad) + 5
        } else {
            (10 * peso) + (6.25 * altura) - (5 * edad) - 161
        }
    }

    fun calcularGET(user: User): Double {
        val peso = user.peso.toDoubleOrNull()
        val altura = user.altura.toDoubleOrNull()
        val genero = user.genero
        val actividad = user.actividad

        Log.d(TAG, "DEBUG: User details - Nombre: ${user.nombre}, Peso: ${user.peso}, Altura: ${user.altura}, Género: $genero, Actividad: $actividad, Fecha Nacimiento: ${user.fechaNacimiento}")

        if (peso == null || altura == null || peso <= 0 || altura <= 0) {
            Log.e(TAG, "ERROR: Peso ($peso) o altura ($altura) no son números válidos o son cero/negativos para ${user.nombre}.")
            return 0.0
        }

        val edad = calcularEdad(user.fechaNacimiento)
        if (edad <= 0) {
            Log.e(TAG, "ERROR: Edad calculada ($edad) no válida para ${user.nombre}.")
            return 0.0
        }
        Log.d(TAG, "DEBUG: Edad calculada: $edad")

        val ger = calcularGER(peso, altura, edad, genero)
        Log.d(TAG, "DEBUG: GER calculado: $ger")

        val factorActividad = factoresActividad[actividad]
        if (factorActividad == null) {
            Log.e(TAG, "ERROR: Nivel de actividad '${actividad}' no reconocido para ${user.nombre}. Factores esperados: ${factoresActividad.keys}")
            return 0.0
        }
        Log.d(TAG, "DEBUG: Factor de actividad: $factorActividad")

        val getResult = ger * factorActividad
        Log.d(TAG, "DEBUG: GET final calculado: $getResult")
        return getResult
    }
}