package com.gouri.projectrecipe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
        RecipeList(recipes = searchResults, onRecipeClick = { viewModel.selectRecipe(it) }, {})
    }
}