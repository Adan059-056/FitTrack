package com.example.proyectoe.ui.Food

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Definición de colores para el tema oscuro
val darkBlueBlack = Color(0xFF0A0E21)
val orangePrimary = Color(0xFFFF9800)
val orangeSecondary = Color(0xFFFF5722)
val darkSurface = Color(0xFF121212)
val textColor = Color(0xFFE0E0E0)
val cardColor = Color(0xFF1E1E2D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(onBack: () -> Unit = {}, onAddFood: () -> Unit = {}) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var activeSearch by rememberSaveable { mutableStateOf(false) }

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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddFood() },
                containerColor = orangePrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar alimento")
            }
        },
        containerColor = darkBlueBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                
                // Resumen de calorías
                item {
                    CalorieSummaryCard()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Macronutrientes
                item {
                    MacronutrientsCard()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Desglose por comidas
                item {
                    MealBreakdownSection()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Selector de categorías
                item {
                    CategorySelector()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Lista de alimentos
                item {
                    FoodItemsList()
                }
            }
        }
    }
}

@Composable
fun CalorieSummaryCard() {
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
            NutrientCircle(title = "Consumidas", value = "0", total = "1,686", color = orangePrimary)
            NutrientCircle(title = "Restantes", value = "1,686", total = "", color = Color(0xFF4CAF50))
            NutrientCircle(title = "Quemadas", value = "0", total = "", color = orangeSecondary)
        }
    }
}

@Composable
fun NutrientCircle(title: String, value: String, total: String, color: Color) {
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
fun MacronutrientsCard() {
    val nutrients = listOf(
        Nutrient("Carbohidratos", "0 g", "206 g", 0.2f),
        Nutrient("Proteínas", "0 g", "82 g", 0.1f),
        Nutrient("Grasas", "0 g", "54 g", 0.05f)
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

        // Barra de progreso
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(textColor.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(nutrient.progress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(orangePrimary)
            )
        }
    }
}

data class Nutrient(val name: String, val consumed: String, val total: String, val progress: Float)

@Composable
fun MealBreakdownSection() {
    val meals = listOf(
        Meal("Desayuno", "0 / 506 kcal"),
        Meal("Almuerzo", "0 / 674 kcal"),
        Meal("Cena", "0 / 421 kcal"),
        Meal("Snacks", "0 / 84 kcal")
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
                text = "Comidas del día",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            meals.forEach { meal ->
                MealRow(meal = meal)
                if (meal != meals.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun MealRow(meal: Meal) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardColor.copy(alpha = 0.8f))
            .border(1.dp, textColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = meal.name,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = textColor
        )
        Text(
            text = meal.calories,
            fontSize = 16.sp,
            color = orangePrimary
        )
    }
}

data class Meal(val name: String, val calories: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategorySelector() {
    val categories = listOf("FRECUENTES", "RECIENTES", "FAVORITOS")
    val pagerState = rememberPagerState(pageCount = { categories.size })
    val coroutineScope = rememberCoroutineScope()
    var selectedCategory by remember { mutableIntStateOf(0) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            categories.forEachIndexed { index, category ->
                Button(
                    onClick = {
                        selectedCategory = index
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCategory == index) orangePrimary else Color.Transparent,
                        contentColor = if (selectedCategory == index) Color.White else textColor.copy(alpha = 0.7f)
                    ),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = if (selectedCategory == index) 4.dp else 0.dp,
                        pressedElevation = 0.dp,
                        hoveredElevation = 0.dp
                    ),
                    border = if (selectedCategory != index) BorderStroke(1.dp, textColor.copy(alpha = 0.3f)) else null
                ) {
                    Text(category, fontSize = 12.sp)
                }

                if (index < categories.lastIndex) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(state = pagerState) { page ->
            FoodItemsList()
        }
    }
}

@Composable
fun FoodItemsList() {
    val foodItems = listOf(
        FoodItem("Arroz blanco, cocinado", "1 taza (158 g)", "205 kcal"),
        FoodItem("Aceite de oliva", "1 cucharada (13% g)", "119 kcal"),
        FoodItem("Sandía", "1 rodaja (286 g)", "86 kcal"),
        FoodItem("Manzana", "1 pieza de fruta (182 g)", "95 kcal"),
        FoodItem("Café", "1 taza (237 mL)", "2 kcal"),
        FoodItem("Tortilla francesa 2 huevos", "Casera, 1 ración (100 g)", "141 kcal"),
        FoodItem("Jamón", "1 rodaja (28 g) Listo", "46 kcal")
    )

    Column {
        foodItems.forEach { food ->
            FoodItemRow(food = food)
            Divider(
                color = textColor.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun FoodItemRow(food: FoodItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Acción al seleccionar */ }
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
                text = food.name.take(1),
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
                text = food.details,
                color = textColor.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        Text(
            text = food.calories,
            fontWeight = FontWeight.Bold,
            color = orangePrimary
        )
    }
}

data class FoodItem(val name: String, val details: String, val calories: String)