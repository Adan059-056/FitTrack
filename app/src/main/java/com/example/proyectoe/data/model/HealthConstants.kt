package com.example.proyectoe.data.model

//import com.example.proyectoe.data.model.User
import java.time.LocalDate
import java.time.Period

object CalculadoraGET {
    private val factoresActividad = mapOf(
        "Sedentario" to 1.2,
        "Ligero" to 1.375,
        "Moderado" to 1.55,
        "Activo" to 1.725,
        "Muy activo" to 1.9
    )

    private fun calcularEdad(fechaNacimientoString: String): Int {
        val fechaNacimiento = LocalDate.parse(fechaNacimientoString)
        val fechaActual = LocalDate.now()
        return Period.between(fechaNacimiento, fechaActual).years
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

        if (peso == null || altura == null) {
            println("Error: Peso o altura no son números válidos para ${user.nombre}.")
            return 0.0
        }

        val edad = calcularEdad(user.fechaNacimiento)
        val ger = calcularGER(peso, altura, edad, genero)

        val factorActividad = factoresActividad[actividad]
        if (factorActividad == null) {
            println("Error: Nivel de actividad '${actividad}' no reconocido para ${user.nombre}.")
            return 0.0
        }

        return ger * factorActividad
    }
}
