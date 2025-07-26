package com.example.proyectoe.ui.Food

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoe.database.FoodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ConsumedFoodEntry(
    val id: String = UUID.randomUUID().toString(),
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

    //private var allCatalogFoodItems: List<FoodItem> = emptyList()
    private val _allCatalogFoodItems = MutableStateFlow<List<FoodItem>>(emptyList())
    private val _currentDay = MutableStateFlow(getCurrentDate())

    init {
        // Al iniciar el ViewModel, siempre carga el catálogo de Firebase
        fetchFoodItemsCatalog()
        // Observa la query de búsqueda para filtrar el catálogo local
        //observeSearchQuery()
        observeSearchAndCatalogChanges()

        // Opcional: monitorear el cambio de día si la app permanece abierta
        viewModelScope.launch {
            _currentDay.collect { date ->
                if (getCurrentDate() != date) {
                    resetConsumedFoodEntriesForNewDay()
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
                val snapshot = firestore.collection("foodItems").get().await() // Usa get()
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(FoodItem::class.java)?.copy(id = doc.id)
                }
                _allCatalogFoodItems.value = items // Actualiza el StateFlow
                _isLoading.value = false
                //filterFoods(_searchQuery.value, items) // Refresca los resultados de búsqueda
                Log.d("FoodViewModel", "Food items catalog fetched. Count: ${items.size}")
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
                val docRef = if (foodItem.id.isBlank()) {
                    firestore.collection("foodItems").document()
                } else {
                    firestore.collection("foodItems").document(foodItem.id)
                }
                val foodToSave = foodItem.copy(id = docRef.id)

                docRef.set(foodToSave).await()
                fetchFoodItemsCatalog()
                _isLoading.value = false
                onSuccess()
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
                fetchFoodItemsCatalog()
                _isLoading.value = false
                onSuccess()
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
        // Busca en el catálogo que ya tienes cargado en _allCatalogFoodItems.value
        return _allCatalogFoodItems.value.firstOrNull { it.id == foodId }
    }

    // Esta función AHORA solo añade a la lista local
    fun addConsumedFoodEntry(
        foodItem: FoodItem,
        mealType: String,
        quantity: Float,
        onComplete: (Boolean) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: "local_user"
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
            carbohydrates = foodItem.carbohydrates * quantity,
            timestamp = System.currentTimeMillis()
        )

        // Agrega la nueva entrada a la lista actual de _consumedFoodEntries
        _consumedFoodEntries.value = _consumedFoodEntries.value + entry.copy()
        calculateDailyTotals(_consumedFoodEntries.value)
        Log.d("FoodViewModel", "Consumed food entry added locally: $entry")
        onComplete(true)
    }

    // Este método limpia los consumos diarios (localmente)
    fun resetConsumedFoodEntriesForNewDay() {
        _consumedFoodEntries.value = emptyList()
        calculateDailyTotals(emptyList())
        _currentDay.value = getCurrentDate()
        Log.d("FoodViewModel", "Consumed food entries reset for new day: ${getCurrentDate()}")
    }


    // ¡CAMBIO CLAVE AQUÍ!
    private fun observeSearchAndCatalogChanges() {
        viewModelScope.launch {
            // Combina los dos StateFlows. Cuando CUALQUIERA de ellos cambia, se ejecuta el bloque.
            combine(_searchQuery, _allCatalogFoodItems) { query, allItems ->
                Pair(query, allItems)
            }.collect { (query, allItems) ->
                filterFoods(query, allItems)
            }
        }
    }

    private fun filterFoods(query: String, allItems: List<FoodItem>) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
        } else {
            val filteredList = allItems.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.details.contains(query, ignoreCase = true)
            }
            _searchResults.value = filteredList
        }
        Log.d("FoodViewModel", "Filtering foods for query: '$query'. Results count: ${_searchResults.value.size}")
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

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

    fun setSelectedDate(date: String) {
        if (getCurrentDate() != date) {
            _consumedFoodEntries.value = emptyList()
            calculateDailyTotals(emptyList())
            _errorMessage.value = "Mostrando consumos para el día actual. Los datos anteriores no se guardan."
        } else {
            _errorMessage.value = null
        }
        _currentDay.value = date
        Log.d("FoodViewModel", "Selected date changed to: $date. Consumed entries updated accordingly.")
    }
}

data class DailyTotals(
    val totalCalories: Float = 0f,
    val totalProtein: Float = 0f,
    val totalFat: Float = 0f,
    val totalCarbohydrates: Float = 0f
)