package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class PortalType {
    ADMISSION,
    EXAM_FORM,
    ADMIT_CARD,
    SCHOLARSHIPS,
    PYQ_PAPERS,
    SYLLABUS,
    IMPORTANT_LINKS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalInfoScreen(
    portalType: PortalType,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val title = when (portalType) {
        PortalType.ADMISSION -> "PPU All Courses Admission"
        PortalType.EXAM_FORM -> "PPU Exam Form Portal"
        PortalType.ADMIT_CARD -> "PPU Admit Card Portal"
        PortalType.SCHOLARSHIPS -> "🎓 Scholarships"
        PortalType.PYQ_PAPERS -> "📚 Previous Year Question Papers (PYQ)"
        PortalType.SYLLABUS -> "📘 COURSE STRUCTURE & SYLLABUS"
        PortalType.IMPORTANT_LINKS -> "🔗 Important Links"
    }

    val openUrl: (String) -> Unit = { url ->
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("portal_info_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (portalType) {
                PortalType.ADMISSION, PortalType.EXAM_FORM, PortalType.ADMIT_CARD -> {
                    StandardPortalContent(portalType = portalType, onOpenUrl = openUrl)
                }
                PortalType.SCHOLARSHIPS -> {
                    ScholarshipsContent(onOpenUrl = openUrl)
                }
                PortalType.PYQ_PAPERS -> {
                    PyqPapersContent(onOpenUrl = openUrl)
                }
                PortalType.SYLLABUS -> {
                    SyllabusContent(onOpenUrl = openUrl)
                }
                PortalType.IMPORTANT_LINKS -> {
                    ImportantLinksContent(onOpenUrl = openUrl)
                }
            }
        }
    }
}

@Composable
private fun StandardPortalContent(
    portalType: PortalType,
    onOpenUrl: (String) -> Unit
) {
    val targetUrl = when (portalType) {
        PortalType.ADMISSION -> "https://ppupadm.samarth.edu.in/"
        PortalType.EXAM_FORM -> "https://ppuponline.in/exam_form_search_student_semester.php"
        PortalType.ADMIT_CARD -> "https://ppuponline.in/"
        else -> "https://ppup.ac.in"
    }

    val buttonText = when (portalType) {
        PortalType.ADMISSION -> "🌐 PPU Samarth Portal खोलें"
        PortalType.EXAM_FORM -> "📝 Exam Form भरें"
        PortalType.ADMIT_CARD -> "📄 Admit Card डाउनलोड करें"
        else -> "Open Portal"
    }

    // Header Banner Card
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (portalType) {
                            PortalType.ADMISSION -> Icons.Outlined.School
                            PortalType.EXAM_FORM -> Icons.Outlined.EditNote
                            PortalType.ADMIT_CARD -> Icons.Outlined.Badge
                            else -> Icons.Outlined.Link
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = when (portalType) {
                        PortalType.ADMISSION -> "Samarth Admission Portal"
                        PortalType.EXAM_FORM -> "Examination Portal"
                        PortalType.ADMIT_CARD -> "Admit Card Download"
                        else -> "PPU Portal"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Patliputra University, Patna",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }

    // Main Info Content Box
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (portalType) {
                PortalType.ADMISSION -> {
                    Text(
                        text = "पाटलिपुत्र विश्वविद्यालय (PPU) में सभी UG, PG एवं अन्य पाठ्यक्रमों में नामांकन अब समार्थ (Samarth) पोर्टल के माध्यम से किया जाता है।",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 24.sp
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        text = "ऑनलाइन आवेदन करने से पहले कृपया आधिकारिक निर्देश ध्यानपूर्वक पढ़ें। इसके बाद नीचे दिए गए लिंक पर क्लिक करके Samarth Portal पर जाकर अपना आवेदन करें।",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Column {
                        Text(
                            text = "PPU Samarth Portal",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "https://ppupadm.samarth.edu.in/",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                PortalType.EXAM_FORM -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "📝 परीक्षा फॉर्म (Exam Form)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        text = "पाटलिपुत्र विश्वविद्यालय द्वारा सभी Courses का Examination Form ऑनलाइन भरा जाता है।",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 24.sp
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        text = "अपने Course का Examination Form भरने के लिए नीचे दिए गए आधिकारिक Portal पर जाएँ।",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Column {
                        Text(
                            text = "PPU Exam Form Link:-",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "https://ppuponline.in/exam_form_search_student_semester.php",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                PortalType.ADMIT_CARD -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "📄 प्रवेश पत्र (Admit Card)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        text = "पाटलिपुत्र विश्वविद्यालय द्वारा सभी Courses का ऑनलाइन Examination Form भरने के बाद, जब विश्वविद्यालय द्वारा Admit Card जारी किया जाता है, तो उसका लिंक इस पेज पर उपलब्ध करा दिया जाता है।",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 24.sp
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Text(
                        text = "आप अपने Course के Admit Card लिंक पर क्लिक करके अपना Admit Card आसानी से डाउनलोड कर सकते हैं।",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Column {
                        Text(
                            text = "Admit Card Link:-",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "https://ppuponline.in/",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                else -> {}
            }
        }
    }

    // Large Action Button
    Button(
        onClick = { onOpenUrl(targetUrl) },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("portal_info_action_button")
    ) {
        Text(
            text = buttonText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
    }

    // Separate Note Card for Admit Card or Exam Form
    val noteText = when (portalType) {
        PortalType.ADMIT_CARD -> "यदि Admit Card डाउनलोड करते समय \"Not Verify by College\" संदेश दिखाई देता है, तो कृपया कुछ समय बाद पुनः प्रयास करें। आपके महाविद्यालय द्वारा परीक्षा फॉर्म का सत्यापन (Verification) होते ही आपका Admit Card जारी कर दिया जाएगा।"
        PortalType.EXAM_FORM -> "Examination Form को Final Submit करने से पहले एक बार सभी विवरण (Details) अच्छी तरह जाँच लें। परीक्षा फॉर्म भरने की अंतिम तिथि एवं अन्य महत्वपूर्ण निर्देशों के लिए पाटलिपुत्र विश्वविद्यालय द्वारा जारी आधिकारिक सूचना अवश्य देखें।"
        else -> null
    }

    if (noteText != null) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Note",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "📌 महत्वपूर्ण नोट:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = noteText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ScholarshipsContent(onOpenUrl: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Intro Banner Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "इस पेज पर पाटलिपुत्र विश्वविद्यालय (PPU) एवं भारत सरकार/बिहार सरकार द्वारा संचालित प्रमुख छात्रवृत्ति योजनाओं की जानकारी उपलब्ध है। कृपया अपनी पात्रता के अनुसार संबंधित छात्रवृत्ति योजना का चयन करें।",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    lineHeight = 22.sp
                )
            }
        }

        // 1. Bihar Post Matric Scholarship (PMS)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "1. Bihar Post Matric Scholarship (PMS) 2026-27",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "बिहार पोस्ट मैट्रिक छात्रवृत्ति (PMS) बिहार सरकार द्वारा संचालित एक महत्वपूर्ण छात्रवृत्ति योजना है। इस योजना के अंतर्गत बिहार के पात्र छात्र-छात्राओं को उच्च शिक्षा के लिए आर्थिक सहायता प्रदान की जाती है।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
  
