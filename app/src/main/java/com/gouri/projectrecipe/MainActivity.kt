package com.gouri.projectrecipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.gouri.projectrecipe.networking.RetrofitClient
import com.gouri.projectrecipe.networking.SpoonacularApi
import com.gouri.projectrecipe.room.RecipeDatabase
import com.gouri.projectrecipe.ui.theme.ProjectRecipeTheme
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    val database by lazy { RecipeDatabase.getDatabase(this) }

    private val viewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory(RecipeRepository(RetrofitClient.instance.create(SpoonacularApi::class.java), database.recipeDao()))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjectRecipeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun MyApp(viewModel: RecipeViewModel) {
    val navController = rememberNavController()
    Scaffold(
            bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        NavHost(navController, startDestination = BottomNavItem.Home.route) {
            composable(BottomNavItem.Home.route) { HomeScreen(viewModel) { recipeId ->
                navController.navigate("recipeDetail/$recipeId") {
                    launchSingleTop = true
                    restoreState = true
                }
            } }
            composable("recipeDetail/{recipeId}") { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId")
                val recipe = viewModel.getRecipeById(recipeId)
                recipe?.let { RecipeDetailScreen(it)  }
            }
            composable("search") { SearchScreen(viewModel) }
            composable(BottomNavItem.Favorites.route) { FavoriteScreen(viewModel) { recipeId ->
                navController.navigate("recipeDetail/$recipeId") {
                    launchSingleTop = true
                    restoreState = true
                }
            } }
        }
    }



}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: RecipeViewModel, navigateToRecipeDetail: (Int) -> Unit) {
    val recipes by viewModel.recipes.collectAsState()
    val favoriteRecipes by viewModel.favoriteRecipes.collectAsState()
    viewModel.setNavigationFunction(navigateToRecipeDetail)


    Column {
        TopAppBar(
            title = { Text("Recipe App") }
        )
        Spacer(modifier = Modifier.height(2.dp))
        TextField(
            value = viewModel.searchQuery.value,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(shape = RoundedCornerShape(30.dp, 30.dp, 30.dp, 30.dp))
            ,
            placeholder = { Text("Search any recipe here...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        RecipeList(recipes = recipes, onRecipeClick = { viewModel.selectRecipe(it) }, { recipe ->
            viewModel.toggleFavoriteRecipe(recipe)

        })

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(viewModel: RecipeViewModel, navigateToRecipeDetail: (Int) -> Unit) {
    val favoriteRecipes by viewModel.favoriteRecipes.collectAsState(initial = emptyList())
    viewModel.setNavigationFunction(navigateToRecipeDetail)
    Column {
        TopAppBar(
            title = { Text("Favorite Recipes") }
        )
        RecipeList(
            recipes = favoriteRecipes,
            onRecipeClick = { viewModel.selectRecipe(it) }) { recipe ->
            viewModel.toggleFavoriteRecipe(recipe)
        }
    }
}

@Composable
fun RecipeList(recipes: List<Recipe>, onRecipeClick: (Recipe) -> Unit, onToggleFavorite: (Recipe) -> Unit) {
    LazyColumn {
        itemsIndexed(recipes) { index, recipe ->
            if ((index + 1) % 6 == 0 && index != 0) {
                AdItem() // After 5 recipes, we'll show an ad
            }
            RecipeItem(recipe = recipe, onClick = { onRecipeClick(recipe) }) {
                onToggleFavorite(recipe)
            }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe, onClick: () -> Unit, onToggleFavorite: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Image(
                painter = rememberImagePainter(recipe.image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(8.dp)
            )
            IconButton(onClick = onToggleFavorite) {
                val icon = if (recipe.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                Icon(icon, contentDescription = null)
            }
        }
    }
}
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Favorites
    )
    BottomNavigation(backgroundColor = MaterialTheme.colors.surface, contentColor = MaterialTheme.colors.onSurface, elevation = 8.dp){
        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
sealed class BottomNavItem(val route: String, val icon: Int, val title: String) {
    object Home : BottomNavItem("home", R.drawable.ic_home, "Home")
    object Favorites : BottomNavItem("favorites",R.drawable.ic_fav, "Favourite")
}
@Composable
fun AdItem() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = "This is an Ad",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(8.dp)
        )
    }
}








