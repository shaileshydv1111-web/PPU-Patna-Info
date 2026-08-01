package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ResultEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ResultCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    results: List<ResultEntity>,
    selectedCourseFilter: String,
    onCourseFilterSelect: (String) -> Unit,
    onResultClick: (ResultEntity) -> Unit,
    onBookmarkToggle: (ResultEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val courseOptions = listOf("All", "UG", "PG", "Vocational", "B.Tech", "B.Ed")

    val filteredResults = remember(results, selectedCourseFilter, searchQuery) {
        results.filter { result ->
            val matchesCourse = if (selectedCourseFilter == "All") true else result.course.equals(selectedCourseFilter, ignoreCase = true)
            val matchesQuery = if (searchQuery.isBlank()) true else {
                result.title.contains(searchQuery, ignoreCase = true) ||
                result.session.contains(searchQuery, ignoreCase = true)
            }
            matchesCourse && matchesQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("results_screen")
    ) {
        // Search & Filter Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by Course or Session (e.g. 2023-26)...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("results_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Course Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(courseOptions) { course ->
                    val isSelected = selectedCourseFilter == course
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCourseFilterSelect(course) },
                        label = { Text(course) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("results_chip_$course")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Results List
        if (filteredResults.isEmpty()) {
            EmptyStateView(
                title = "No Results Found",
                subtitle = "Try selecting a different course category or adjusting your search query.",
                icon = Icons.Filled.Assessment
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredResults) { result ->
                    ResultCard(
                        result = result,
                        onResultClick = onResultClick,
                        onBookmarkToggle = onBookmarkToggle
                    )
                }
            }
        }
    }
}
