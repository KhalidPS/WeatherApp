package com.k.sekiro.weatherapp.presentation.home_screen.compnent

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchBar(
    places: List<String> = emptyList(),
    onPlaceClicked:(String) -> Unit,
) {
    var value by remember { mutableStateOf("") }
    val isExpanded by remember { derivedStateOf { value.isNotEmpty() && value.isNotEmpty() } }
    val filteredPlaces by remember { derivedStateOf { places.filter { place -> place.lowercase().contains(value.lowercase()) } } }


    SearchBar(
        modifier = Modifier.fillMaxWidth(.9f),
        inputField = {
            OutlinedTextField(
                value = value,
                onValueChange = {
                    value = it
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                placeholder = { Text("Search") },
                trailingIcon = {
                    if (isExpanded){
                        IconButton(
                            onClick = { value = "" }) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                        }
                    }

                }
            )
        },
        expanded = isExpanded,
        onExpandedChange = {
        }
    ) {
        LazyColumn {
            items(filteredPlaces) { place ->
                Row (
                   modifier =  Modifier
                       .clickable(onClick = {
                           onPlaceClicked(place)
                           value = ""
                       })
                       .fillMaxWidth()
                       .padding(start = 12.dp)
                       .height(50.dp)
,
                    verticalAlignment = Alignment.CenterVertically,

                ){
                    Text(
                        text = place
                    )
                }
            }
        }
    }
}