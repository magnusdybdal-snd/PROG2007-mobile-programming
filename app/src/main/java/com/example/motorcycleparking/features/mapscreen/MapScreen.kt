package com.example.motorcycleparking.features.mapscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motorcycleparking.core.core_ui.composable.SimpleSearchBar


/**
 * Displays Map screen
 */
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
){
    val cs = MaterialTheme.colorScheme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
    ) {
        Text(
            text = "MotorcycleParking",
            fontSize = 40.sp,
            fontWeight = FontWeight.SemiBold,
            color = cs.onBackground,
            modifier = Modifier.padding(top = 10.dp),
        )
        SimpleSearchBar(
            textFieldState = TextFieldState(),
            onSearch = {/*TODO*/},
            searchResults = listOf("Marius", "Ole", "Magnus"),
        )
    }
}
