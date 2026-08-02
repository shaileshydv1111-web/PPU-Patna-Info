package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val courseOptions = listOf("All", "UG", "PG", "Vocational", "B.Tech", "B.Ed")

    val resultPortalUrl = "https://lu.indiaexaminfo.co.in/result_patliputra.aspx"
    val resultBotUrl = "https://t.me/PatliputraUniversityResultBot"

    val openUrl: (String) -> Unit = { url ->
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("results_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. PPU Result Portal Section ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "📄 Patliputra University Result",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "पाटलिपुत्र विश्वविद्यालय (PPU) द्वारा सभी UG, PG एवं अन्य Courses के परीक्षा परिणाम एक ही Result Portal पर जारी किए जाते हैं।",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "जैसे ही आपके किसी भी Course का Result प्रकाशित होता है, आप नीचे दिए गए आधिकारिक Result Portal पर जाकर अपना Marksheet/Result आसानी से देख एवं डाउनलोड कर सकते हैं।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🌐 PPU Result Portal:\n$resultPortalUrl",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Large Button 1
                    Button(
                        onClick = { openUrl(resultPortalUrl) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("open_result_portal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "📄 PPU Result Portal खोलें",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- 2. PPU Result Telegram Bot Section ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "🤖 PPU Result Telegram Bot",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "विद्यार्थियों की सुविधा के लिए Patliputra University Result Download करने हेतु एक Telegram Bot भी उपलब्ध है।",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "इस Bot की सहायता से आप अपना Roll Number दर्ज करके अपने Course का Result/Marksheet आसानी से डाउनलोड कर सकते हैं।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🤖 Result Bot:\n$resultBotUrl",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Large Button 2
                    Button(
                        onClick = { openUrl(resultBotUrl) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("open_result_bot_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SmartToy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🤖 PPU Result Bot खोलें",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- 3. Recent Course Results Search & Feed ---
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "Recently Announced Results",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(10.dp))

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
        }

        if (filteredResults.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No Results Found",
                    subtitle = "Try selecting a different course category or adjusting your search query.",
                    icon = Icons.Filled.Assessment
                )
            }
        } else {
            items(filteredResults) { result ->
                ResultCard(
                    result = result,
                    onResultClick = {
                        openUrl(resultPortalUrl)
                    },
                    onBookmarkToggle = onBookmarkToggle
                )
            }
        }
    }
}
