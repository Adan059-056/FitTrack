package com.example.proyectoe.ui.Food

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoe.database.FoodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ConsumedFoodEntry(
    val id: String = "",
    val foodItemId: String = "",
    val userId: String = "",
    val date: String = "",
    val mealType: String = "",
    val quantity: Float = 0f,
    val name: String = "",
    val calories: Float = 0f,
    val protein: Float = 0f,
    val fat: Float = 0f,
    val carbohydrates: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)


class FoodViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    // private val _foodItems = MutableStateFlow<List<FoodItem>>(emptyList()) // No necesitamos exponer esto directamente

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _consumedFoodEntries = MutableStateFlow<List<ConsumedFoodEntry>>(emptyList())
    val consumedFoodEntries: StateFlow<List<ConsumedFoodEntry>> = _consumedFoodEntries.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<FoodItem>>(emptyList())
    val searchResults: StateFlow<List<FoodItem>> = _searchResults.asStateFlow()

    private val _dailyTotals = MutableStateFlow(DailyTotals())
    val dailyTotals: StateFlow<DailyTotals> = _dailyTotals.asStateFlow()

    private var allCatalogFoodItems: List<FoodItem> = emptyList()

    init {
        fetchFoodItemsCatalog()
        auth.currentUser?.uid?.let { userId ->
            fetchConsumedFoodEntries(userId, getCurrentDate())
        } ?: run {
            _errorMessage.value = "Usuario no autenticado."
            Log.e("FoodViewModel", "Usuario no autenticado al iniciar ViewModel.")
        }

        viewModelScope.launch {
            searchQuery.collect { query ->
                _searchResults.value = if (query.isBlank()) {
                    emptyList()
                } else {
                    allCatalogFoodItems.filter {
                        it.name.contains(query, ignoreCase = true) ||
                                it.details.contains(query, ignoreCase = true)
                    }
                }
            }
        }
    }

    // metodos

    fun fetchFoodItemsCatalog() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("foodItems").get().await()
                allCatalogFoodItems = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(FoodItem::class.java)?.copy(id = doc.id)
                }
                _isLoading.value = false
                // Actualiza los resultados de búsqueda si ya hay una query
                onSearchQueryChanged(_searchQuery.value)
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar catálogo de alimentos: ${e.message}"
                _isLoading.value = false
                Log.e("FoodViewModel", "Error fetching food catalog items", e)
            }
        }
    }

    fun addFood(foodItem: FoodItem, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val newDocRef = firestore.collection("foodItems").add(foodItem).await()
                val addedFoodItemWithId = foodItem.copy(id = newDocRef.id)
                allCatalogFoodItems = allCatalogFoodItems + addedFoodItemWithId
                _isLoading.value = false
                onSuccess()
                onSearchQueryChanged(_searchQuery.value)
            } catch (e: Exception) {
                val errorMsg = "Error al agregar alimento al catálogo: ${e.message}"
                _errorMessage.value = errorMsg
                _isLoading.value = false
                onFailure(errorMsg)
                Log.e("FoodViewModel", "Error adding food item to catalog", e)
            }
        }
    }

    fun updateFoodItem(foodItem: FoodItem, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                if (foodItem.id.isBlank()) {
                    val errorMsg = "ID de alimento no válido para actualizar."
                    _errorMessage.value = errorMsg
                    _isLoading.value = false
                    onFailure(errorMsg)
                    return@launch
                }
                firestore.collection("foodItems").document(foodItem.id).set(foodItem).await()
                allCatalogFoodItems = allCatalogFoodItems.map {
                    if (it.id == foodItem.id) foodItem else it
                }
                _isLoading.value = false
                onSuccess()
                onSearchQueryChanged(_searchQuery.value)
            } catch (e: Exception) {
                val errorMsg = "Error al actualizar alimento del catálogo: ${e.message}"
                _errorMessage.value = errorMsg
                _isLoading.value = false
                onFailure(errorMsg)
                Log.e("FoodViewModel", "Error al actualizar el alimento en el catalogo", e)
            }
        }
    }

    //metodos para el registro de consumo diario por usuario

    suspend fun getFoodItemById(foodId: String): FoodItem? {
        return try {
            val doc = firestore.collection("foodItems").document(foodId).get().await()
            doc.toObject(FoodItem::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            _errorMessage.value = "Error al obtener alimento por ID: ${e.message}"
            Log.e("FoodViewModel", "Error al obtener alimento por ID: $foodId", e)
            null
        }
    }

    fun fetchConsumedFoodEntries(userId: String, date: String) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection("daily_consumptions")
                    .document(date)
                    .collection("meals")
                    .get()
                    .await()

                val entries = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ConsumedFoodEntry::class.java)?.copy(id = doc.id)
                }
                _consumedFoodEntries.value = entries
                calculateDailyTotals(entries)
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar consumos diarios: ${e.message}"
                _isLoading.value = false
                Log.e("FoodViewModel", "Error fetching daily consumptions for user $userId on $date", e)
            }
        }
    }

    fun addConsumedFoodEntry(
        foodItem: FoodItem,
        mealType: String,
        quantity: Float,
        onComplete: (Boolean) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _errorMessage.value = "Usuario no autenticado para registrar consumo."
            onComplete(false)
            return
        }

        val currentDate = getCurrentDate()
        val entry = ConsumedFoodEntry(
            foodItemId = foodItem.id,
            userId = userId,
            date = currentDate,
            mealType = mealType,
            quantity = quantity,
            name = foodItem.name,
            calories = foodItem.calories * quantity,
            protein = foodItem.protein * quantity,
            fat = foodItem.fat * quantity,
            carbohydrates = foodItem.carbohydrates * quantity
        )

        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val docRef = firestore.collection("users")
                    .document(userId)
                    .collection("daily_consumptions")
                    .document(currentDate)
                    .collection("meals")
                    .add(entry)
                    .await()

                val addedEntryWithId = entry.copy(id = docRef.id)
                _consumedFoodEntries.value = _consumedFoodEntries.value + addedEntryWithId
                calculateDailyTotals(_consumedFoodEntries.value)
                _isLoading.value = false
                onComplete(true)
            } catch (e: Exception) {
                _errorMessage.value = "Error al registrar consumo: ${e.message}"
                _isLoading.value = false
                onComplete(false)
                Log.e("FoodViewModel", "Error adding consumed food entry", e)
            }
        }
    }

    // metodo para actualizar la búsqueda
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // para calcular los numeros totales diarios
    private fun calculateDailyTotals(entries: List<ConsumedFoodEntry>) {
        var totalCalories = 0f
        var totalProtein = 0f
        var totalFat = 0f
        var totalCarbohydrates = 0f

        entries.forEach { entry ->
            totalCalories += entry.calories
            totalProtein += entry.protein
            totalFat += entry.fat
            totalCarbohydrates += entry.carbohydrates
        }

        _dailyTotals.value = DailyTotals(
            totalCalories = totalCalories,
            totalProtein = totalProtein,
            totalFat = totalFat,
            totalCarbohydrates = totalCarbohydrates
        )
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}

data class DailyTotals(
    val totalCalories: Float = 0f,
    val totalProtein: Float = 0f,
    val totalFat: Float = 0f,
    val totalCarbohydrates: Float = 0f
)