// FoodScreen.kt
package com.example.proyectoe.ui.Food

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectoe.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(onBack: () -> Unit = {}, onAddFood: () -> Unit = {}) {
    val darkBlue = Color(0xFF0A1128)
    val deepBlue = Color(0xFF0F1C3F)
    val navyBlue = Color(0xFF1A2C5C)
    val BlueAccent = Color(0x00FFFF)

    var searchQuery by remember { mutableStateOf("") }
    var activeSearch by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alimentación") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { activeSearch = true }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFood,
                containerColor = BlueAccent,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar alimento")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            deepBlue,
                            darkBlue,
                            Color.Black
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Barra de búsqueda
                item {
                    if (activeSearch) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = { activeSearch = false },
                            active = activeSearch,
                            onActiveChange = { activeSearch = it },
                            placeholder = { Text("Buscar alimentos...") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = Color.White
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    searchQuery = ""
                                    activeSearch = false
                                }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Cerrar",
                                        tint = Color.White
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = androidx.compose.material3.SearchBarDefaults.colors(
                                containerColor = navyBlue.copy(alpha = 0.7f)
                            )
                        ) {
                            // Resultados de búsqueda (puedes implementarlo luego)
                        }
                    }
                }

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
    val navyBlue = Color(0xFF1A2C5C)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = navyBlue.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        val LigthBlue = Color(0xADD8E6)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NutrientCircle(title = "Consumidas", value = "0", total = "1,686", color = LigthBlue)
            NutrientCircle(title = "Restantes", value = "1,686", total = "", color = Color(0xFF4CAF50))
            NutrientCircle(title = "Quemadas", value = "0", total = "", color = Color(0xFFFF9800))
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
                    color = Color.White
                )
                if (total.isNotEmpty()) {
                    Text(
                        text = "/ $total",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            color = Color.White.copy(alpha = 0.8f),
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
    val navyBlue = Color(0xFF1A2C5C)
    val MidnightBlue = Color(0x191970)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = navyBlue.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Macronutrientes",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            nutrients.forEach { nutrient ->
                MacronutrientRow(nutrient = nutrient)
                if (nutrient != nutrients.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(
                        color = MidnightBlue.copy(alpha = 0.3f),
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
                color = Color.White
            )
            Text(
                text = "${nutrient.consumed} / ${nutrient.total}",
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Barra de progreso
        val MidnightBlue = Color(0x191970)
        val LigthBlue = Color(0xADD8E6)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MidnightBlue.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(nutrient.progress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(LigthBlue)
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
    val darkBlue = Color(0xFF0A1128)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = darkBlue.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Comidas del día",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
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
    val darkBlue = Color(0xFF0A1128)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(darkBlue.copy(alpha = 0.5f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = meal.name,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
        )
        val LigthBlue = Color(0xADD8E6)
        Text(
            text = meal.calories,
            fontSize = 16.sp,
            color = LigthBlue
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
                val LigthBlue = Color(0xADD8E6)
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
                        containerColor = if (selectedCategory == index) LigthBlue else Color.Transparent,
                        contentColor = if (selectedCategory == index) Color.White else Color.White.copy(alpha = 0.7f)
                    ),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = if (selectedCategory == index) 4.dp else 0.dp
                    )
                ) {
                    Text(category)
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
            val LigthBlue = Color(0xADD8E6)
            Divider(
                color = LigthBlue.copy(alpha = 0.2f),
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
        val LigthBlue = Color(0xADD8E6)
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(LigthBlue.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            // Aquí iría una imagen representativa del alimento
            Text(
                text = food.name.take(1),
                color = LigthBlue,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = food.name,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = food.details,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        Text(
            text = food.calories,
            fontWeight = FontWeight.Bold,
            color = LigthBlue
        )
    }
}

data class FoodItem(val name: String, val details: String, val calories: String)