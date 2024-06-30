package com.gouri.projectrecipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(recipe: Recipe) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe.title, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                        navController.popBackStack()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
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
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(8.dp),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(8.dp),
                color = Color.White

            )
            for (ingredient in recipe.extendedIngredients) {
                Text(
                    text = "${ingredient.name}: ${ingredient.amount} ${ingredient.unit}",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(8.dp),
                    color = Color.White

                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Instructions",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(8.dp),
                color = Color.White

            )
            Text(
                text = recipe.instructions,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(8.dp),
                color = Color.White

            )
        }
    }
}