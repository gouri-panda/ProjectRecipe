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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.gouri.projectrecipe.networking.RetrofitClient
import com.gouri.projectrecipe.networking.SpoonacularApi
import com.gouri.projectrecipe.ui.theme.ProjectRecipeTheme

class MainActivity : ComponentActivity() {
    private val viewModel: RecipeViewModel by viewModels {
        RecipeViewModelFactory(RecipeRepository(RetrofitClient.instance.create(SpoonacularApi::class.java)))
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

    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(viewModel) }
        composable("recipeDetail/{recipeId}") { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")
            val recipe = viewModel.getRecipeById(recipeId)
            recipe?.let { RecipeDetailScreen(it)  }//todo: navigate to detail screen
        }
        composable("search") { SearchScreen(viewModel) }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: RecipeViewModel) {
    val recipes by viewModel.recipes.collectAsState()
    val favoriteRecipes by viewModel.favoriteRecipes.collectAsState()

    Column {
        TopAppBar(
            title = { androidx.compose.material3.Text("Recipe App") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = viewModel.searchQuery.value,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search any recipe") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Popular Recipes",
//            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(16.dp)
        )
        RecipeList(recipes = recipes, onRecipeClick = { viewModel.selectRecipe(it) })
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "All Recipes",
//            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(16.dp)
        )
        RecipeList(recipes = recipes, onRecipeClick = { viewModel.selectRecipe(it) })
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Favorite Recipes",
//            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(16.dp)
        )
        RecipeList(recipes = favoriteRecipes, onRecipeClick = { viewModel.selectRecipe(it) })
    }
}

@Composable
fun RecipeList(recipes: List<Recipe>, onRecipeClick: (Recipe) -> Unit) {
    LazyColumn {
        items(recipes) { recipe ->
            RecipeItem(recipe = recipe, onClick = { onRecipeClick(recipe) })
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe, onClick: () -> Unit) {
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
//                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: RecipeViewModel) {
    val searchResults by viewModel.searchResults.collectAsState()

    Column {
        TopAppBar(
            title = { Text("Search Recipes") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = viewModel.searchQuery.value,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search any recipe") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        RecipeList(recipes = searchResults, onRecipeClick = { viewModel.selectRecipe(it) })
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(recipe: Recipe) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe.title) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberImagePainter(recipe.image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = recipe.title,
//                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ingredients",
//                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(8.dp)
            )
            for (ingredient in recipe.extendedIngredients) {
                Text(
                    text = "${ingredient.name}: ${ingredient.amount} ${ingredient.unit}",
//                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Instructions",
//                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = recipe.instructions,
//                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}





