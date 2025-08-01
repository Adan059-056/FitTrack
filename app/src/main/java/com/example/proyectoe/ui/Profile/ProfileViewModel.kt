package com.example.proyectoe.ui.Profile

import androidx.lifecycle.viewModelScope
import com.example.proyectoe.data.model.User
import com.example.proyectoe.data.model.CalculadoraGET
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.net.Uri

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate // Importa LocalDate
import java.time.format.DateTimeParseException // Importa DateTimeParseException

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import android.util.Log // Asegúrate de tener esta importación para Logcat

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "ProfileViewModel" // Añade un TAG para este ViewModel

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isSuccess = MutableStateFlow<String?>(null)
    val isSuccess: StateFlow<String?> = _isSuccess

    private val _profilePhotoUri = MutableStateFlow<Uri?>(null)
    val profilePhotoUri: StateFlow<Uri?> = _profilePhotoUri

    private val _getCalculado = MutableStateFlow<Double?>(null)
    val getCalculado: StateFlow<Double?> = _getCalculado

    private val _carbsTarget = MutableStateFlow<Float?>(null)
    val carbsTarget: StateFlow<Float?> = _carbsTarget

    private val _proteinTarget = MutableStateFlow<Float?>(null)
    val proteinTarget: StateFlow<Float?> = _proteinTarget

    private val _fatTarget = MutableStateFlow<Float?>(null)
    val fatTarget: StateFlow<Float?> = _fatTarget

    private val _breakfastTarget = MutableStateFlow<Int?>(null)
    val breakfastTarget: StateFlow<Int?> = _breakfastTarget

    private val _lunchTarget = MutableStateFlow<Int?>(null)
    val lunchTarget: StateFlow<Int?> = _lunchTarget

    private val _dinnerTarget = MutableStateFlow<Int?>(null)
    val dinnerTarget: StateFlow<Int?> = _dinnerTarget

    private val _snacksTarget = MutableStateFlow<Int?>(null)
    val snacksTarget: StateFlow<Int?> = _snacksTarget




    private val appContext = application.applicationContext

    fun clearSuccessMessage() {
        _isSuccess.value = null
    }

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    private val _editableUser = MutableStateFlow<User?>(null)
    val editableUser: StateFlow<User?> = _editableUser

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        _isLoading.value = true
        _errorMessage.value = null
        _isSuccess.value = null
        _getCalculado.value = null
        _carbsTarget.value = null
        _proteinTarget.value = null
        _fatTarget.value = null
        _breakfastTarget.value = null
        _lunchTarget.value = null
        _dinnerTarget.value = null
        _snacksTarget.value = null

        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val documentSnapshot = db.collection("usuarios").document(userId).get().await()
                    if (documentSnapshot.exists()) {
                        val userData = documentSnapshot.toObject(User::class.java)
                        _user.value = userData
                        _editableUser.value = userData?.copy()

                        // Recalcula el GET después de cargar y potencialmente corregir la fecha
                        userData?.let { user ->
                            // Asegúrate de que la fecha esté en el formato correcto antes de pasarla
                            val formattedUser = user.copy(fechaNacimiento = formatFechaNacimientoForCalculation(user.fechaNacimiento))
                            val getResult = CalculadoraGET.calcularGET(formattedUser)
                            _getCalculado.value = getResult
                            //_getCalculado.value = CalculadoraGET.calcularGET(formattedUser)


                            if (getResult > 0) {
                                calculateMacronutrientTargets(getResult)
                                calculateMealTargets(getResult)
                            } else {
                                // Si el GET es inválido, reinicia los objetivos
                                _carbsTarget.value = null
                                _proteinTarget.value = null
                                _fatTarget.value = null
                                _breakfastTarget.value = null
                                _lunchTarget.value = null
                                _dinnerTarget.value = null
                                _snacksTarget.value = null
                            }
                        } ?: run {
                            _getCalculado.value = null
                        }



                        val storedPhotoFileName = userData?.photoFileName
                        if (storedPhotoFileName != null && storedPhotoFileName.isNotEmpty()) {
                            val file = File(appContext.filesDir, storedPhotoFileName)
                            if (file.exists()) {
                                _profilePhotoUri.value = Uri.fromFile(file)
                            } else {
                                _profilePhotoUri.value = null
                            }
                        } else {
                            _profilePhotoUri.value = null
                        }
                    } else {
                        _errorMessage.value = "No se encontraron datos para el usuario."
                        _user.value = null
                        _editableUser.value = null
                        _profilePhotoUri.value = null
                        _getCalculado.value = null
                    }
                } else {
                    _errorMessage.value = "Usuario no autenticado."
                    _user.value = null
                    _editableUser.value = null
                    _profilePhotoUri.value = null
                    _getCalculado.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar el perfil: ${e.message}"
                Log.e(TAG, "Error al cargar el perfil: ${e.message}", e)
                _getCalculado.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun toggleEditMode() {
        _isEditing.value = !_isEditing.value
        _errorMessage.value = null
        _isSuccess.value = null

        if (_isEditing.value) {
            _editableUser.value = _user.value?.copy()
            val currentPhotoFileName = _user.value?.photoFileName
            _profilePhotoUri.value = if (currentPhotoFileName != null && currentPhotoFileName.isNotEmpty()) {
                val file = File(appContext.filesDir, currentPhotoFileName)
                if (file.exists()) Uri.fromFile(file) else null
            } else {
                null
            }
        } else {
            _editableUser.value = _user.value?.copy()
            val currentPhotoFileName = _user.value?.photoFileName
            _profilePhotoUri.value = if (currentPhotoFileName != null && currentPhotoFileName.isNotEmpty()) {
                val file = File(appContext.filesDir, currentPhotoFileName)
                if (file.exists()) Uri.fromFile(file) else null
            } else {
                null
            }
        }
    }

    fun updateEditableName(name: String) {
        _editableUser.value = _editableUser.value?.copy(nombre = name)
    }

    fun updateEditableApellidos(apellidos: String) {
        _editableUser.value = _editableUser.value?.copy(apellidos = apellidos)
    }

    fun updateEditableFechaNacimiento(fecha: String) {
        _editableUser.value = _editableUser.value?.copy(fechaNacimiento = fecha)
    }

    fun updateEditablePeso(peso: String) {
        _editableUser.value = _editableUser.value?.copy(peso = peso)
    }

    fun updateEditableAltura(altura: String) {
        _editableUser.value = _editableUser.value?.copy(altura = altura)
    }

    fun updateEditableGenero(genero: String) {
        _editableUser.value = _editableUser.value?.copy(genero = genero)
    }

    fun updateEditableActividad(actividad: String) {
        _editableUser.value = _editableUser.value?.copy(actividad = actividad)
    }

    fun updateEditableObjetivo(objetivo: String) {
        _editableUser.value = _editableUser.value?.copy(objetivo = objetivo)
    }

    fun saveProfileAndPhotoChanges() {
        _isLoading.value = true
        _errorMessage.value = null
        _isSuccess.value = null
        _getCalculado.value = null
        _carbsTarget.value = null
        _proteinTarget.value = null
        _fatTarget.value = null
        _breakfastTarget.value = null
        _lunchTarget.value = null
        _dinnerTarget.value = null
        _snacksTarget.value = null

        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            val userToSave = _editableUser.value
            val currentSelectedPhotoUri = _profilePhotoUri.value

            if (userId == null) {
                _errorMessage.value = "Usuario no autenticado."
                _isLoading.value = false
                return@launch
            }
            if (userToSave == null) {
                _errorMessage.value = "No hay datos de usuario para guardar."
                _isLoading.value = false
                return@launch
            }

            try {
                var photoFileNameToSave: String? = userToSave.photoFileName

                if (currentSelectedPhotoUri != null && currentSelectedPhotoUri.scheme == "content") {
                    val newFileName = "profile_photo_${userId}_${System.currentTimeMillis()}.jpg"
                    val destinationFile = File(appContext.filesDir, newFileName)

                    appContext.contentResolver.openInputStream(currentSelectedPhotoUri)?.use { inputStream ->
                        FileOutputStream(destinationFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    photoFileNameToSave = newFileName
                    _profilePhotoUri.value = Uri.fromFile(destinationFile)
                }

                // --- INICIO DE LA CORRECCIÓN DE LA FECHA ---
                val fechaNacimientoOriginal = userToSave.fechaNacimiento
                val fechaNacimientoFormateada = formatFechaNacimientoForSaving(fechaNacimientoOriginal)

                if (fechaNacimientoFormateada == null) {
                    _errorMessage.value = "Formato de fecha de nacimiento inválido. Use AAAA-MM-DD o AAAA, MM, DD."
                    _isLoading.value = false
                    return@launch
                }
                // --- FIN DE LA CORRECCIÓN DE LA FECHA ---

                val updates: Map<String, Any?> = mapOf(
                    "nombre" to userToSave.nombre,
                    "apellidos" to userToSave.apellidos,
                    "fechaNacimiento" to fechaNacimientoFormateada, // Guarda la fecha formateada
                    "peso" to userToSave.peso,
                    "altura" to userToSave.altura,
                    "genero" to userToSave.genero,
                    "actividad" to userToSave.actividad,
                    "objetivo" to userToSave.objetivo,
                    "email" to userToSave.email,
                    "photoFileName" to photoFileNameToSave
                )

                db.collection("usuarios").document(userId).update(updates).await()

                // Actualiza _user con los datos guardados (incluyendo la fecha formateada)
                _user.value = userToSave.copy(photoFileName = photoFileNameToSave, fechaNacimiento = fechaNacimientoFormateada)

                // Recalcula el GET con el usuario actualizado y la fecha corregida
                _user.value?.let { user ->
                    val getResult = CalculadoraGET.calcularGET(user)
                    _getCalculado.value = getResult

                    if (getResult > 0) {
                        calculateMacronutrientTargets(getResult)

                        calculateMealTargets(getResult)
                    }

                }

                _isEditing.value = false
                _isSuccess.value = "Perfil actualizado con éxito."

            } catch (e: IOException) {
                _errorMessage.value = "Error al guardar la foto localmente: ${e.message}"
                Log.e(TAG, "Error al guardar la foto localmente: ${e.message}", e)
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar los cambios en Firestore: ${e.message}"
                Log.e(TAG, "Error al guardar los cambios en Firestore: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfilePhotoUri(uri: Uri?) {
        _profilePhotoUri.value = uri
    }

    // Función auxiliar para formatear la fecha para guardar en Firestore
    private fun formatFechaNacimientoForSaving(dateString: String): String? {
        return try {
            // Intenta parsear el formato "AAAA, MM, DD"
            val parts = dateString.split(",").map { it.trim() }
            if (parts.size == 3) {
                val year = parts[0].padStart(4, '0')
                val month = parts[1].padStart(2, '0')
                val day = parts[2].padStart(2, '0')
                return "$year-$month-$day"
            }
            // Si no es el formato "AAAA, MM, DD", intenta parsear como "AAAA-MM-DD"
            LocalDate.parse(dateString).toString()
        } catch (e: DateTimeParseException) {
            Log.e(TAG, "Error al parsear fecha para guardar: $dateString", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error inesperado al formatear fecha para guardar: $dateString", e)
            null
        }
    }

    // Función auxiliar para formatear la fecha para el cálculo (si es diferente al formato de guardado)
    // En este caso, como CalculadoraGET espera "AAAA-MM-DD", esta función es la misma que la de guardado.
    private fun formatFechaNacimientoForCalculation(dateString: String): String {
        return try {
            // Intenta parsear el formato "AAAA, MM, DD"
            val parts = dateString.split(",").map { it.trim() }
            if (parts.size == 3) {
                val year = parts[0].padStart(4, '0')
                val month = parts[1].padStart(2, '0')
                val day = parts[2].padStart(2, '0')
                return "$year-$month-$day"
            }
            // Si no es el formato "AAAA, MM, DD", asume que ya está en "AAAA-MM-DD" o intenta parsearlo
            LocalDate.parse(dateString).toString() // Esto validará y devolverá el formato estándar
        } catch (e: DateTimeParseException) {
            Log.e(TAG, "Error al parsear fecha para cálculo: $dateString. Devolviendo cadena original.", e)
            // Si no se puede parsear, devuelve la cadena original. CalculadoraGET lo manejará como error.
            dateString
        } catch (e: Exception) {
            Log.e(TAG, "Error inesperado al formatear fecha para cálculo: $dateString", e)
            dateString
        }
    }
private fun calculateMacronutrientTargets(get: Double) {
    // Asumiendo una distribución estándar:
    // 40% de Carbohidratos, 30% de Proteínas, 30% de Grasas
    val proteinCalories = get * 0.30
    val fatCalories = get * 0.30
    val carbsCalories = get * 0.40

    // 1 gramo de proteína = 4 kcal
    // 1 gramo de grasa = 9 kcal
    // 1 gramo de carbohidratos = 4 kcal
    _proteinTarget.value = (proteinCalories / 4.0).toFloat()
    _fatTarget.value = (fatCalories / 9.0).toFloat()
    _carbsTarget.value = (carbsCalories / 4.0).toFloat()

    Log.d(TAG, "Targets calculados: Carbs=${_carbsTarget.value}, Prot=${_proteinTarget.value}, Fat=${_fatTarget.value}")
}
    private fun calculateMealTargets(get: Double) {
        // Distribución estándar de calorías por comida
        _breakfastTarget.value = (get * 0.25).toInt()
        _lunchTarget.value = (get * 0.35).toInt()
        _dinnerTarget.value = (get * 0.30).toInt()
        _snacksTarget.value = (get * 0.10).toInt()

        Log.d(TAG, "Targets de comidas calculados: Desayuno=${_breakfastTarget.value}, Almuerzo=${_lunchTarget.value}, Cena=${_dinnerTarget.value}, Snacks=${_snacksTarget.value}")
    }
}
