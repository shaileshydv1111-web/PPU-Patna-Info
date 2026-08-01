package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.*
import com.example.ui.components.*

@Composable
fun HomeScreen(
    notices: List<NoticeEntity>,
    results: List<ResultEntity>,
    banners: List<BannerEntity>,
    onNoticeClick: (NoticeEntity) -> Unit,
    onResultClick: (ResultEntity) -> Unit,
    onBookmarkToggleNotice: (NoticeEntity) -> Unit,
    onBookmarkToggleResult: (ResultEntity) -> Unit,
    onQuickAccessClick: (String) -> Unit,
    onViewAllNoticesClick: () -> Unit,
    onViewAllResultsClick: () -> Unit,
    onGlobalSearchClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_column"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Quick Search Banner Button
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onGlobalSearchClick() }
                        .testTag("home_search_bar")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Search Notices, Results, PYQs, Admissions...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Hero Image Banner Carousel
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_ppu_banner),
                            contentDescription = "PPU Campus Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.tertiary
                            ) {
                                Text(
                                    text = "OFFICIAL PPU PORTAL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Patliputra University, Patna",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "UG & PG Admission 2026, Admit Cards, Exam Forms & Results",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }

        // Quick Access Services Grid (8 Services)
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Quick Portal Access",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                val colorPrimary = MaterialTheme.colorScheme.primary
                val colorSecondary = MaterialTheme.colorScheme.secondary
                val colorTertiary = MaterialTheme.colorScheme.tertiary

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickAccessItem(
                        title = "Admit Card",
                        icon = Icons.Outlined.Badge,
                        color = colorPrimary,
                        onClick = { onQuickAccessClick("Admit Card") },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessItem(
                        title = "Exam Form",
                        icon = Icons.Outlined.EditNote,
                        color = colorSecondary,
                        onClick = { onQuickAccessClick("Exam Form") },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessItem(
                        title = "UG Admission",
                        icon = Icons.Outlined.School,
                        color = colorPrimary,
                        onClick = { onQuickAccessClick("UG Admission") },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessItem(
                        title = "PG Admission",
                        icon = Icons.Outlined.MenuBook,
                        color = Color(0xFF2E7D32),
                        onClick = { onQuickAccessClick("PG Admission") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickAccessItem(
                        title = "Scholarships",
                        icon = Icons.Outlined.AccountBalance,
                        color = Color(0xFFD97706),
                        onClick = { onQuickAccessClick("Scholarships") },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessItem(
                        title = "PYQs Papers",
                        icon = Icons.Outlined.Folder,
                        color = colorPrimary,
                        onClick = { onQuickAccessClick("PYQ") },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessItem(
                        title = "Syllabus",
                        icon = Icons.Outlined.AutoStories,
                        color = colorSecondary,
                        onClick = { onQuickAccessClick("Syllabus") },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAccessItem(
                        title = "Important Links",
                        icon = Icons.Outlined.Link,
                        color = Color(0xFF0284C7),
                        onClick = { onQuickAccessClick("Important Links") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Latest Notices Section
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Campaign,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Latest Notices & Circulars",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = onViewAllNoticesClick,
                        modifier = Modifier.testTag("home_view_all_notices")
                    ) {
                        Text("View All")
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        items(notices.take(3)) { notice ->
            NoticeCard(
                notice = notice,
                onNoticeClick = onNoticeClick,
                onBookmarkToggle = onBookmarkToggleNotice,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Latest Results Section
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.FactCheck,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recently Published Results",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = onViewAllResultsClick,
                        modifier = Modifier.testTag("home_view_all_results")
                    ) {
                        Text("View All")
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        items(results.take(2)) { result ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                ResultCard(
                    result = result,
                    onResultClick = onResultClick,
                    onBookmarkToggle = onBookmarkToggleResult
                )
            }
        }
    }
}
