package com.example.proyectoe.ui.Food

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoe.data.model.FoodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import com.example.proyectoe.data.repository.StepCounterRepository

data class ConsumedFoodEntry(
    var id: String = "",
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


class FoodViewModel(private val stepCounterRepository: StepCounterRepository) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String? get() = auth.currentUser?.uid

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

    private val _allCatalogFoodItems = MutableStateFlow<List<FoodItem>>(emptyList())
    private val _currentDay = MutableStateFlow(getCurrentDate())

    private val _burnedCalories = MutableStateFlow(0)
    val burnedCalories: StateFlow<Int> = _burnedCalories.asStateFlow()

    init {
        Log.d("FoodViewModelLifecycle", "FoodViewModel init called.")
        fetchFoodItemsCatalog()
        observeSearchAndCatalogChanges()
        startListeningForConsumedFoodEntries()

        viewModelScope.launch {
            stepCounterRepository.currentDailySteps.collect { steps ->
                // Usamos 0.04 como una estimación simple de calorías por paso
                val calories = (steps * 0.04).toInt()
                _burnedCalories.value = calories
            }
        }

        viewModelScope.launch {
            _currentDay.collect { date ->
                if (getCurrentDate() != date) {
                    Log.d("FoodViewModel", "Date changed. Fetching new day's consumed entries.")
                    _searchQuery.value = ""
                    fetchConsumedFoodEntries(getCurrentDate())
                }
            }
        }
    }

    // Métodos del catálogo
    fun fetchFoodItemsCatalog() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            Log.d("FoodViewModelFetch", "fetchFoodItemsCatalog started.")
            try {
                val snapshot = firestore.collection("foodItems").get().await()
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(FoodItem::class.java)?.copy(id = doc.id)
                }
                _allCatalogFoodItems.value = items
                _isLoading.value = false
                Log.d("FoodViewModelFetch", "Food items catalog fetched. Count: ${items.size}. First item (if any): ${items.firstOrNull()?.name}")
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar catálogo de alimentos: ${e.message}"
                _isLoading.value = false
                Log.e("FoodViewModelFetch", "Error fetching food catalog items", e)
            }
        }
    }

    fun addFood(foodItem: FoodItem, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            Log.d("FoodViewModelAddCatalog", "addFood called for: ${foodItem.name}")
            try {
                val docRef = if (foodItem.id.isBlank()) {
                    firestore.collection("foodItems").document()
                } else {
                    firestore.collection("foodItems").document(foodItem.id)
                }
                val foodToSave = foodItem.copy(id = docRef.id)

                docRef.set(foodToSave).await()
                Log.d("FoodViewModelAddCatalog", "Food saved to Firestore: ${foodToSave.name} with ID: ${foodToSave.id}")

                fetchFoodItemsCatalog()
                _isLoading.value = false
                onSuccess()
                Log.d("FoodViewModelAddCatalog", "addFood completed successfully. onSuccess called.")
            } catch (e: Exception) {
                val errorMsg = "Error al agregar alimento al catálogo: ${e.message}"
                _errorMessage.value = errorMsg
                _isLoading.value = false
                onFailure(errorMsg)
                Log.e("FoodViewModelAddCatalog", "Error adding food item to catalog", e)
            }
        }
    }

    fun updateFoodItem(foodItem: FoodItem, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            Log.d("FoodViewModelUpdateCatalog", "updateFoodItem called for: ${foodItem.name}")
            try {
                if (foodItem.id.isBlank()) {
                    val errorMsg = "ID de alimento no válido para actualizar."
                    _errorMessage.value = errorMsg
                    _isLoading.value = false
                    onFailure(errorMsg)
                    return@launch
                }
                firestore.collection("foodItems").document(foodItem.id).set(foodItem).await()
                Log.d("FoodViewModelUpdateCatalog", "Food updated in Firestore: ${foodItem.name} with ID: ${foodItem.id}")
                fetchFoodItemsCatalog()
                _isLoading.value = false
                onSuccess()
                Log.d("FoodViewModelUpdateCatalog", "updateFoodItem completed successfully. onSuccess called.")
            } catch (e: Exception) {
                val errorMsg = "Error al actualizar alimento del catálogo: ${e.message}"
                _errorMessage.value = errorMsg
                _isLoading.value = false
                onFailure(errorMsg)
                Log.e("FoodViewModelUpdateCatalog", "Error updating food item in catalog", e)
            }
        }
    }

    suspend fun getFoodItemById(foodId: String): FoodItem? {
        return _allCatalogFoodItems.value.firstOrNull { it.id == foodId }
    }

    // metodos para mandar el consumo a firebase
    private var consumedEntriesListener: com.google.firebase.firestore.ListenerRegistration? = null

    private fun startListeningForConsumedFoodEntries() {
        val userId = currentUserId
        val date = _currentDay.value

        if (userId == null) {
            _errorMessage.value = "Usuario no autenticado para cargar consumos."
            return
        }

        consumedEntriesListener?.remove()
        Log.d("FoodViewModelListen", "Starting listener for consumed food entries for user: $userId, date: $date")

        val docRef = firestore.collection("data_nutricional").document(userId)
            .collection("consumos_diarios").document(date)
            .collection("comidas_registradas")

        consumedEntriesListener = docRef
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _errorMessage.value = "Error al escuchar consumos diarios: ${e.message}"
                    Log.e("FoodViewModelListen", "Error listening for daily consumptions", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val entries = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ConsumedFoodEntry::class.java)?.copy(id = doc.id)
                    }
                    _consumedFoodEntries.value = entries
                    calculateDailyTotals(entries)
                    Log.d("FoodViewModelListen", "Consumed food entries updated (from Firebase): ${entries.size} items.")
                } else {
                    _consumedFoodEntries.value = emptyList()
                    calculateDailyTotals(emptyList())
                    Log.d("FoodViewModelListen", "No consumed food entries found for $date, or snapshot is empty.")
                }
            }
    }

    fun fetchConsumedFoodEntries(date: String) {
        val userId = currentUserId
        if (userId == null) {
            _errorMessage.value = "Usuario no autenticado para cargar consumos."
            return
        }
        consumedEntriesListener?.remove()
        Log.d("FoodViewModelFetchConsumed", "Fetching consumed food entries for user: $userId, date: $date (one-time fetch or new listener)")

        _currentDay.value = date
        startListeningForConsumedFoodEntries()
    }


    fun addConsumedFoodEntry(
        foodItem: FoodItem,
        mealType: String,
        quantity: Float,
        onComplete: (Boolean) -> Unit
    ) {
        _isLoading.value = true
        _errorMessage.value = null
        val userId = currentUserId
        val currentDate = _currentDay.value

        if (userId == null) {
            _errorMessage.value = "Usuario no autenticado."
            onComplete(false)
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            try {
                val entryId = UUID.randomUUID().toString()
                val entry = ConsumedFoodEntry(
                    id = entryId,
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

                firestore.collection("data_nutricional").document(userId)
                    .collection("consumos_diarios").document(currentDate)
                    .collection("comidas_registradas").document(entryId)
                    .set(entry).await()

                Log.d("FoodViewModelAddConsumed", "Consumed food entry added to Firestore: ${entry.name} on $currentDate")
                _isLoading.value = false
                onComplete(true)
            } catch (e: Exception) {
                val errorMsg = "Error al agregar consumo: ${e.message}"
                _errorMessage.value = errorMsg
                Log.e("FoodViewModelAddConsumed", "Error adding consumed food entry to Firestore", e)
                _isLoading.value = false
                onComplete(false)
            }
        }
    }

//    fun resetConsumedFoodEntriesForNewDay() {
//        _consumedFoodEntries.value = emptyList()
//        calculateDailyTotals(emptyList())
//        _currentDay.value = getCurrentDate()
//        Log.d("FoodViewModel", "Reinicio manual de los alimentos consumidos para un nuevo dia.")
//    }

    // metodos de busqueda y calculo de totales
    private fun observeSearchAndCatalogChanges() {
        viewModelScope.launch {
            Log.d("FoodViewModelObserve", "observeSearchAndCatalogChanges started.")
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
            Log.d("FoodViewModelFilter", "Query is blank. Search results cleared.")
        } else {
            val filteredList = allItems.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.details.contains(query, ignoreCase = true)
            }
            _searchResults.value = filteredList
        }
        Log.d("FoodViewModelFilter", "Filtering for query: '$query'. Found ${_searchResults.value.size} results. First result (if any): ${_searchResults.value.firstOrNull()?.name}")
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        Log.d("FoodViewModelSearch", "Search query changed to: '$query'")
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
        if (_currentDay.value != date) {
            _currentDay.value = date
            fetchConsumedFoodEntries(date)
            _errorMessage.value = null
            Log.d("FoodViewModel", "User selected date: $date. Fetching entries for this date.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        consumedEntriesListener?.remove()
        Log.d("FoodViewModelLifecycle", "FoodViewModel cleared. Firestore listener removed.")
    }
}

data class DailyTotals(
    val totalCalories: Float = 0f,
    val totalProtein: Float = 0f,
    val totalFat: Float = 0f,
    val totalCarbohydrates: Float = 0f
)