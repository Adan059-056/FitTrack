package com.example.proyectoe.ui.Food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoe.data.model.FoodItem

import com.example.proyectoe.ui.Profile.ProfileViewModel

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import com.example.proyectoe.MyApplication
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


// colres
val darkBlueBlack = Color(0xFF0A0E21)
val orangePrimary = Color(0xFFFF9800)
val orangeSecondary = Color(0xFFFF5722)
val darkSurface = Color(0xFF121212)
val textColor = Color(0xFFE0E0E0)
val cardColor = Color(0xFF1E1E2D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(
    onBack: () -> Unit = {},
    onAddFood: () -> Unit = {},
    onEditFood: (String) -> Unit = {},
    foodViewModel: FoodViewModel = provideFoodViewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val consumedFoodEntries by foodViewModel.consumedFoodEntries.collectAsState()
    val isLoading by foodViewModel.isLoading.collectAsState()
    val errorMessage by foodViewModel.errorMessage.collectAsState()
    val searchQuery by foodViewModel.searchQuery.collectAsState()
    val searchResults by foodViewModel.searchResults.collectAsState()
    val dailyTotals by foodViewModel.dailyTotals.collectAsState()
    val getCalculado by profileViewModel.getCalculado.collectAsState()
    val burnedCalories by foodViewModel.burnedCalories.collectAsState()
    val carbsTarget by profileViewModel.carbsTarget.collectAsState()
    val proteinTarget by profileViewModel.proteinTarget.collectAsState()
    val fatTarget by profileViewModel.fatTarget.collectAsState()

    val breakfastTarget by profileViewModel.breakfastTarget.collectAsState()
    val lunchTarget by profileViewModel.lunchTarget.collectAsState()
    val dinnerTarget by profileViewModel.dinnerTarget.collectAsState()
    val snacksTarget by profileViewModel.snacksTarget.collectAsState()

    var showAddFoodToMealDialog by remember { mutableStateOf(false) }
    var selectedFoodItemForMeal by remember { mutableStateOf<FoodItem?>(null) }
    var selectedMealTypeForDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        foodViewModel.fetchFoodItemsCatalog()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alimentación", color = textColor) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBlueBlack,
                    titleContentColor = textColor
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = textColor
                        )
                    }
                },
            )
        },
        //coemnte el boton de + por el momento
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = onAddFood,
//                containerColor = orangePrimary,
//                contentColor = Color.White
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Agregar alimento al catálogo")
//            }
//        },
        containerColor = darkBlueBlack
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                CalorieSummaryCard(dailyTotals.totalCalories.toInt(), getCalculado = getCalculado, burnedCalories = burnedCalories)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                MacronutrientsCard(
                    dailyTotals.totalProtein,
                    dailyTotals.totalFat,
                    dailyTotals.totalCarbohydrates,
                    carbsTarget = carbsTarget,
                    proteinTarget = proteinTarget,
                    fatTarget = fatTarget
                )
                Spacer(modifier = Modifier.height(16.dp))
            }


            // buscador de alimentos en el catalogo
            item {
                Text(
                    text = "Comidas del Día",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { foodViewModel.onSearchQueryChanged(it) },
                    label = { Text("Buscar alimento...", color = Color.White.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = orangePrimary,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = orangePrimary,
                        focusedLabelColor = orangePrimary,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = orangePrimary)
                    }
                } else if (errorMessage != null) {
                    Text(
                        text = "Error: $errorMessage",
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )
                } else if (searchQuery.isNotBlank() && searchResults.isEmpty()) {
                    Text(
                        text = "No se encontraron resultados para '${searchQuery}'.",
                        color = textColor.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onAddFood,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = orangePrimary)
                    ) {
                        Text("Añadir '${searchQuery}' al catálogo")
                    }
                }
            }

            items(searchResults) { food ->
                FoodItemCatalogRow(food = food, onAddClick = { selectedCatalogFood ->
                    selectedFoodItemForMeal = selectedCatalogFood
                    showAddFoodToMealDialog = true
                }, onEditClick = { foodId ->
                    onEditFood(foodId)
                })
                Divider(
                    color = textColor.copy(alpha = 0.1f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                MealBreakdownSection(
                    consumedFoodEntries = consumedFoodEntries,
                    onEditFoodEntry = { entryId -> },
                    onAddFoodToMeal = { mealType ->

                        selectedMealTypeForDialog = mealType
                        showAddFoodToMealDialog = true
                    },
                    breakfastTarget = breakfastTarget,
                    lunchTarget = lunchTarget,
                    dinnerTarget = dinnerTarget,
                    snacksTarget = snacksTarget
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showAddFoodToMealDialog && selectedFoodItemForMeal != null) {
        AddFoodToMealDialog(
            foodItem = selectedFoodItemForMeal!!,
            initialMealType = selectedMealTypeForDialog,
            onDismiss = {
                showAddFoodToMealDialog = false
                selectedFoodItemForMeal = null
                selectedMealTypeForDialog = null
            },
            onAdd = { food, mealType, quantity ->
                foodViewModel.addConsumedFoodEntry(food, mealType, quantity) { success ->
                    showAddFoodToMealDialog = false
                    selectedFoodItemForMeal = null
                    selectedMealTypeForDialog = null
                }
            }
        )
    } else if (showAddFoodToMealDialog && selectedFoodItemForMeal == null && selectedMealTypeForDialog != null) {
        // En este caso, el usuario intentó añadir desde la sección de comidas.
        // Podrías mostrar un mensaje de error o simplemente no hacer nada hasta que seleccione un alimento.
        // Por ahora, cerramos el diálogo para evitar estados inconsistentes.
        showAddFoodToMealDialog = false
    }

}

@Composable
fun CalorieSummaryCard(totalCalories: Int, getCalculado: Double?, burnedCalories: Int) { // Cambiado a Double?
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NutrientCircle(
                title = "Consumidas",
                value = totalCalories.toString(),
                total = if (getCalculado != null) "${"%.2f".format(getCalculado)} kcal/día" else "N/A",
                color = orangePrimary
            )
            NutrientCircle(
                title = "Restantes",
                value = if (getCalculado != null) (getCalculado - totalCalories).coerceAtLeast(0.0).toInt().toString() else "N/A",
                total = "",
                color = Color(0xFF4CAF50)
            )
            NutrientCircle(
                title = "Quemadas",
                value = burnedCalories.toString(),
                total = "",
                color = orangeSecondary)
        }
    }
}

@Composable
fun NutrientCircle(
    title: String,
    value: String,
    total: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                if (total.isNotEmpty()) {
                    Text(
                        text = "/ $total",
                        fontSize = 12.sp,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            color = textColor.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
    }
}

@Composable
fun MacronutrientsCard(
    protein: Float,
    fat: Float,
    carbohydrates: Float,
    carbsTarget: Float?,
    proteinTarget: Float?,
    fatTarget: Float?
) {
    // Si los objetivos son nulos, usamos valores predeterminados
    val targetCarbs = carbsTarget ?: 206f
    val targetProtein = proteinTarget ?: 82f
    val targetFat = fatTarget ?: 54f

    val nutrients = listOf(
        Nutrient("Carbohidratos", "${carbohydrates.toInt()} g", "${targetCarbs.toInt()} g", carbohydrates / targetCarbs),
        Nutrient("Proteínas", "${protein.toInt()} g", "${targetProtein.toInt()} g", protein / targetProtein),
        Nutrient("Grasas", "${fat.toInt()} g", "${targetFat.toInt()} g", fat / targetFat)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Macronutrientes",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            nutrients.forEach { nutrient ->
                MacronutrientRow(nutrient = nutrient)
                if (nutrient != nutrients.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(
                        color = textColor.copy(alpha = 0.1f),
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
@Composable
fun MacronutrientRow(nutrient: Nutrient) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = nutrient.name,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Text(
                text = "${nutrient.consumed} / ${nutrient.total}",
                color = textColor.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(textColor.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(nutrient.progress.coerceIn(0f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(orangePrimary)
            )
        }
    }
}

data class Nutrient(val name: String, val consumed: String, val total: String, val progress: Float)

@Composable
fun MealBreakdownSection(
    consumedFoodEntries: List<ConsumedFoodEntry>,
    onEditFoodEntry: (String) -> Unit,
    onAddFoodToMeal: (String) -> Unit,
    breakfastTarget: Int?,
    lunchTarget: Int?,
    dinnerTarget: Int?,
    snacksTarget: Int?
) {
    val mealsMap = consumedFoodEntries.groupBy { it.mealType }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val mealTypes = listOf("Desayuno", "Almuerzo", "Cena", "Snacks")
            mealTypes.forEach { mealType ->
                val foodsInMeal = mealsMap[mealType] ?: emptyList()
                val caloriesInMeal = foodsInMeal.sumOf { it.calories.toDouble() }.toInt()
                val totalMealCalories = when (mealType) {
                    "Desayuno" -> breakfastTarget ?: 0
                    "Almuerzo" -> lunchTarget ?: 0
                    "Cena" -> dinnerTarget ?: 0
                    "Snacks" -> snacksTarget ?: 0
                    else -> 0
                }

                MealRow(
                    mealName = mealType,
                    currentCalories = caloriesInMeal,
                    totalCaloriesTarget = totalMealCalories,
                    foodsForMeal = foodsInMeal,
                    onFoodClick = { entryId -> onEditFoodEntry(entryId) },
                    onAddFoodToMeal = { onAddFoodToMeal(mealType) }
                )
                if (mealType != mealTypes.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (consumedFoodEntries.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No hay alimentos registrados para hoy. Busca y añade para empezar.",
                    color = textColor.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MealRow(
    mealName: String,
    currentCalories: Int,
    totalCaloriesTarget: Int,
    foodsForMeal: List<ConsumedFoodEntry>,
    onFoodClick: (String) -> Unit,
    onAddFoodToMeal: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val progress = if (totalCaloriesTarget > 0) currentCalories.toFloat() / totalCaloriesTarget.toFloat() else 0f
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardColor.copy(alpha = 0.8f))
            .border(1.dp, textColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = mealName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textColor
            )
            Text(
                text = "$currentCalories / $totalCaloriesTarget kcal",
                fontSize = 16.sp,
                color = orangePrimary
            )
        }
        // Barra de progreso visual
        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            color = orangePrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            trackColor = textColor.copy(alpha = 0.1f)
        )
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))

            if (foodsForMeal.isEmpty()) {
                Text(
                    text = "No hay alimentos registrados para $mealName.",
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                foodsForMeal.forEach { entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFoodClick(entry.id) }
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${entry.name} (${entry.quantity.toInt()} unid.)", color = textColor.copy(alpha = 0.9f), fontSize = 14.sp)
                        Text(text = "${entry.calories.toInt()} kcal", color = textColor.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
//            OutlinedButton(
//                onClick = { onAddFoodToMeal(mealName) },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                colors = ButtonDefaults.outlinedButtonColors(
//                    contentColor = orangePrimary
//                ),
//                border = BorderStroke(1.dp, orangePrimary),
//                shape = RoundedCornerShape(8.dp)
//            ) {
//                Text("Añadir alimento al $mealName")
//            }
            //Spacer(modifier = Modifier.height(16.dp))
        }
    }

            Spacer(modifier = Modifier.height(16.dp))
        }



@Composable
fun FoodItemCatalogRow(food: FoodItem, onAddClick: (FoodItem) -> Unit, onEditClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(orangePrimary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = food.name.take(1).uppercase(),
                color = orangePrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = food.name,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Text(
                text = "${food.calories} kcal, P:${food.protein}g, G:${food.fat}g, C:${food.carbohydrates}g",
                color = textColor.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }

        IconButton(onClick = { onAddClick(food) }) {
            Icon(Icons.Default.Add, contentDescription = "Añadir a comida", tint = orangePrimary)
        }

//        IconButton(onClick = { onEditClick(food.id) }) {
//            Icon(Icons.Default.Search, contentDescription = "Ver detalles/Editar", tint = textColor.copy(alpha = 0.7f))
//        }
    }
}

@Composable
fun provideFoodViewModel(): FoodViewModel {
    val context = LocalContext.current
    val application = context.applicationContext as MyApplication
    val stepCounterRepository = application.stepCounterRepository

    return viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return FoodViewModel(stepCounterRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodToMealDialog(
    foodItem: FoodItem,
    onDismiss: () -> Unit,
    onAdd: (FoodItem, String, Float) -> Unit,
    initialMealType: String? = null // Nuevo parámetro para el tipo de comida inicial
) {
    var quantity by rememberSaveable { mutableStateOf("1") }
    val mealTypes = listOf("Desayuno", "Almuerzo", "Cena", "Snacks")
    var selectedMealType by rememberSaveable { mutableStateOf(initialMealType ?: mealTypes[0]) }
    var expandedDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir ${foodItem.name} a la comida", color = textColor) },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedMealType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Comida", color = textColor.copy(alpha = 0.7f)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        textStyle = TextStyle(
                            color = orangePrimary, // Color del texto en orangePrimary
                            fontSize = 16.sp
                        ),

//                        colors = TextFieldDefaults.colors(
//                            focusedTextColor = textColor,
//                            unfocusedTextColor = textColor,
//                            focusedContainerColor = Color.Transparent,
//                            unfocusedContainerColor = Color.Transparent,
//                            focusedBorderColor = orangePrimary,
//                            unfocusedBorderColor = textColor.copy(alpha = 0.3f),
//                            focusedLabelColor = orangePrimary,
//                            unfocusedLabelColor = textColor.copy(alpha = 0.7f)
//                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.background(darkBlueBlack)
                    ) {
                        mealTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type, color = orangePrimary) },
                                onClick = {
                                    selectedMealType = type
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { newValue ->
                        quantity = newValue.filter { it.isDigit() || (it == '.' && !quantity.contains('.')) }
                    },
                    label = { Text("Cantidad (unidades)", color = textColor.copy(alpha = 0.7f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = orangePrimary, // Color del texto en orangePrimary
                        fontSize = 16.sp
                    ),
//                    colors = TextFieldDefaults.colors(
//                        focusedTextColor = textColor,
//                        unfocusedTextColor = textColor,
//                        focusedContainerColor = Color.Transparent,
//                        unfocusedContainerColor = Color.Transparent,
//                        focusedBorderColor = orangePrimary,
//                        unfocusedBorderColor = textColor.copy(alpha = 0.3f),
//                        cursorColor = orangePrimary,
//                        focusedLabelColor = orangePrimary,
//                        unfocusedLabelColor = textColor.copy(alpha = 0.7f),
//                        selectionColors = TextSelectionColors(
//                            handleColor = orangePrimary,
//                            backgroundColor = orangePrimary.copy(alpha = 0.4f)
//                        )
//                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qtyFloat = quantity.toFloatOrNull() ?: 0f
                    if (qtyFloat > 0) {
                        onAdd(foodItem, selectedMealType, qtyFloat)
                    }
                },
                enabled = quantity.toFloatOrNull() != null && (quantity.toFloatOrNull() ?: 0f) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = orangePrimary)
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor)) {
                Text("Cancelar")
            }
        },
        containerColor = cardColor
    )
}