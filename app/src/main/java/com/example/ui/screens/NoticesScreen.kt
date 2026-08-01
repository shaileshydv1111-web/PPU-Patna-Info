package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.NoticeEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.NoticeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoticesScreen(
    notices: List<NoticeEntity>,
    selectedCategoryFilter: String,
    onCategoryFilterSelect: (String) -> Unit,
    onNoticeClick: (NoticeEntity) -> Unit,
    onBookmarkToggle: (NoticeEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Exam", "Admission", "General", "Academic", "Sports")

    val filteredNotices = remember(notices, selectedCategoryFilter, searchQuery) {
        notices.filter { notice ->
            val matchesCategory = if (selectedCategoryFilter == "All") true else notice.category.equals(selectedCategoryFilter, ignoreCase = true)
            val matchesQuery = if (searchQuery.isBlank()) true else {
                notice.title.contains(searchQuery, ignoreCase = true) ||
                notice.description.contains(searchQuery, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("notices_screen")
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
                placeholder = { Text("Search notices and circulars...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notices_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategoryFilter == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryFilterSelect(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("notices_chip_$category")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Notices List
        if (filteredNotices.isEmpty()) {
            EmptyStateView(
                title = "No Notices Found",
                subtitle = "There are no notices matching your filter or search query.",
                icon = Icons.Filled.Campaign
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredNotices) { notice ->
                    NoticeCard(
                        notice = notice,
                        onNoticeClick = onNoticeClick,
                        onBookmarkToggle = onBookmarkToggle
                    )
                }
            }
        }
    }
}
