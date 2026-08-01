package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.PpuPatnaTheme
import com.example.viewmodel.PpuViewModel

enum class AppDestination {
    Splash,
    Onboarding,
    Auth,
    MainPortal,
    AdminPanel,
    PdfViewer,
    GlobalSearch
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: PpuViewModel = viewModel()
            val userState by viewModel.userState.collectAsStateWithLifecycle()

            PpuPatnaTheme(darkTheme = userState.isDarkMode) {
                PpuPatnaInfoApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PpuPatnaInfoApp(viewModel: PpuViewModel) {
    val context = LocalContext.current
    var currentDestination by remember { mutableStateOf(AppDestination.Splash) }
    var selectedBottomTab by remember { mutableIntStateOf(0) }

    // Collect ViewModel states
    val notices by viewModel.notices.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()
    val pyqs by viewModel.pyqs.collectAsStateWithLifecycle()
    val admissions by viewModel.admissions.collectAsStateWithLifecycle()
    val scholarships by viewModel.scholarships.collectAsStateWithLifecycle()
    val banners by viewModel.banners.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    val bookmarkedNotices by viewModel.bookmarkedNotices.collectAsStateWithLifecycle()
    val bookmarkedResults by viewModel.bookmarkedResults.collectAsStateWithLifecycle()
    val bookmarkedPyqs by viewModel.bookmarkedPyqs.collectAsStateWithLifecycle()

    val noticeCategoryFilter by viewModel.noticeCategoryFilter.collectAsStateWithLifecycle()
    val resultCourseFilter by viewModel.resultCourseFilter.collectAsStateWithLifecycle()
    val pyqCourseFilter by viewModel.pyqCourseFilter.collectAsStateWithLifecycle()
    val userState by viewModel.userState.collectAsStateWithLifecycle()
    val activePdfState by viewModel.activePdfState.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Status snackbar handler
    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentDestination) {
                AppDestination.Splash -> {
                    SplashScreen(
                        onSplashFinished = {
                            currentDestination = AppDestination.Onboarding
                        }
                    )
                }

                AppDestination.Onboarding -> {
                    OnboardingScreen(
                        onFinishOnboarding = {
                            currentDestination = AppDestination.Auth
                        }
                    )
                }

                AppDestination.Auth -> {
                    AuthScreen(
                        onGuestLogin = {
                            viewModel.loginAsGuest()
                            currentDestination = AppDestination.MainPortal
                        },
                        onStudentLogin = { email, name, rollNo ->
                            viewModel.loginWithEmail(email, name, rollNo)
                            currentDestination = AppDestination.MainPortal
                        },
                        onAdminLogin = { adminKey ->
                            viewModel.loginAsAdmin(adminKey)
                            if (viewModel.userState.value.isAdmin) {
                                currentDestination = AppDestination.MainPortal
                            }
                        }
                    )
                }

                AppDestination.MainPortal -> {
                    val unreadNotifs = notifications.count { !it.isRead }

                    Scaffold(
                        topBar = {
                            PpuTopBar(
                                title = when (selectedBottomTab) {
                                    0 -> "Home Portal"
                                    1 -> "Examination Results"
                                    2 -> "Notices & Circulars"
                                    3 -> "PYQ & Syllabus"
                                    else -> "Student Profile"
                                },
                                unreadNotificationCount = unreadNotifs,
                                onSearchClick = {
                                    currentDestination = AppDestination.GlobalSearch
                                },
                                onNotificationsClick = {
                                    viewModel.showStatus("Showing ${notifications.size} total notifications")
                                },
                                onAdminClick = if (userState.isAdmin) {
                                    { currentDestination = AppDestination.AdminPanel }
                                } else null
                            )
                        },
                        bottomBar = {
                            PpuBottomNav(
                                selectedTab = selectedBottomTab,
                                onTabSelected = { tabIndex -> selectedBottomTab = tabIndex }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (selectedBottomTab) {
                                0 -> HomeScreen(
                                    notices = notices,
                                    results = results,
                                    banners = banners,
                                    onNoticeClick = { notice ->
                                        viewModel.openPdfViewer(
                                            title = notice.title,
                                            subtitle = "Notice • ${notice.category} • ${notice.date}",
                                            pdfUrl = notice.pdfUrl,
                                            category = notice.category,
                                            date = notice.date
                                        )
                                        currentDestination = AppDestination.PdfViewer
                                    },
                                    onResultClick = { result ->
                                        viewModel.openPdfViewer(
                                            title = result.title,
                                            subtitle = "Result • ${result.course} (${result.session})",
                                            pdfUrl = result.pdfUrl,
                                            category = result.course,
                                            date = result.publishDate
                                        )
                                        currentDestination = AppDestination.PdfViewer
                                    },
                                    onBookmarkToggleNotice = { notice ->
                                        viewModel.toggleNoticeBookmark(notice.id, notice.isBookmarked)
                                    },
                                    onBookmarkToggleResult = { result ->
                                        viewModel.toggleResultBookmark(result.id, result.isBookmarked)
                                    },
                                    onQuickAccessClick = { serviceName ->
                                        when (serviceName) {
                                            "Admit Card", "Exam Form" -> {
                                                viewModel.setNoticeCategoryFilter("Exam")
                                                selectedBottomTab = 2
                                            }
                                            "UG Admission", "PG Admission" -> {
                                                viewModel.setNoticeCategoryFilter("Admission")
                                                selectedBottomTab = 2
                                            }
                                            "Scholarships" -> {
                                                viewModel.setNoticeCategoryFilter("General")
                                                selectedBottomTab = 2
                                            }
                                            "PYQ", "Syllabus" -> {
                                                selectedBottomTab = 3
                                            }
                                            else -> {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ppup.ac.in"))
                                                try { context.startActivity(intent) } catch (e: Exception) {
                                                    viewModel.showStatus("Opening official portal: https://ppup.ac.in")
                                                }
                                            }
                                        }
                                    },
                                    onViewAllNoticesClick = { selectedBottomTab = 2 },
                                    onViewAllResultsClick = { selectedBottomTab = 1 },
                                    onGlobalSearchClick = { currentDestination = AppDestination.GlobalSearch }
                                )

                                1 -> ResultsScreen(
                                    results = results,
                                    selectedCourseFilter = resultCourseFilter,
                                    onCourseFilterSelect = { filter -> viewModel.setResultCourseFilter(filter) },
                                    onResultClick = { result ->
                                        viewModel.openPdfViewer(
                                            title = result.title,
                                            subtitle = "Result • ${result.course} (${result.session})",
                                            pdfUrl = result.pdfUrl,
                                            category = result.course,
                                            date = result.publishDate
                                        )
                                        currentDestination = AppDestination.PdfViewer
                                    },
                                    onBookmarkToggle = { result ->
                                        viewModel.toggleResultBookmark(result.id, result.isBookmarked)
                                    }
                                )

                                2 -> NoticesScreen(
                                    notices = notices,
                                    selectedCategoryFilter = noticeCategoryFilter,
                                    onCategoryFilterSelect = { filter -> viewModel.setNoticeCategoryFilter(filter) },
                                    onNoticeClick = { notice ->
                                        viewModel.openPdfViewer(
                                            title = notice.title,
                                            subtitle = "Notice • ${notice.category} • ${notice.date}",
                                            pdfUrl = notice.pdfUrl,
                                            category = notice.category,
                                            date = notice.date
                                        )
                                        currentDestination = AppDestination.PdfViewer
                                    },
                                    onBookmarkToggle = { notice ->
                                        viewModel.toggleNoticeBookmark(notice.id, notice.isBookmarked)
                                    }
                                )

                                3 -> PyqScreen(
                                    pyqs = pyqs,
                                    selectedCourseFilter = pyqCourseFilter,
                                    onCourseFilterSelect = { filter -> viewModel.setPyqCourseFilter(filter) },
                                    onPyqClick = { pyq ->
                                        viewModel.openPdfViewer(
                                            title = pyq.title,
                                            subtitle = "PYQ Paper • ${pyq.course} (${pyq.semester}, ${pyq.year})",
                                            pdfUrl = pyq.pdfUrl,
                                            category = pyq.course,
                                            date = pyq.year
                                        )
                                        currentDestination = AppDestination.PdfViewer
                                    },
                                    onBookmarkToggle = { pyq ->
                                        viewModel.togglePyqBookmark(pyq.id, pyq.isBookmarked)
                                    }
                                )

                                4 -> ProfileScreen(
                                    userState = userState,
                                    bookmarkedNotices = bookmarkedNotices,
                                    bookmarkedResults = bookmarkedResults,
                                    bookmarkedPyqs = bookmarkedPyqs,
                                    onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                                    onTogglePushNotifications = { viewModel.togglePushNotifications(it) },
                                    onNoticeClick = { notice ->
                                        viewModel.openPdfViewer(
                                            title = notice.title,
                                            subtitle = "Notice • ${notice.category}",
                                            pdfUrl = notice.pdfUrl,
                                            category = notice.category,
                                            date = notice.date
                                        )
                                        currentDestination = AppDestination.PdfViewer
                                    },
                                    onResultClick = { result ->
                                        viewModel.openPdfViewer(
                                            title = result.title,
                                            subtitle = "Result • ${result.course}",
                                            pdfUrl = result.pdfUrl
                                        )
                                        currentDestination = AppDestination.PdfViewer
                                    },
                                    onPyqClick = { pyq ->
                                        viewModel.openPdfViewer(
                                            title = pyq.title,
                                            subtitle = "PYQ • ${pyq.course}",
                                            pdfUrl = pyq.pdfUrl
                                        )
                                        currentDestination = AppDestination.PdfViewer
                                    },
                                    onOpenAdminPanel = {
                                        if (userState.isAdmin) {
                                            currentDestination = AppDestination.AdminPanel
                                        } else {
                                            currentDestination = AppDestination.Auth
                                        }
                                    },
                                    onLogout = {
                                        viewModel.logout()
                                        currentDestination = AppDestination.Auth
                                    }
                                )
                            }
                        }
                    }
                }

                AppDestination.PdfViewer -> {
                    activePdfState?.let { pdfState ->
                        PdfViewerScreen(
                            pdfState = pdfState,
                            onClose = {
                                viewModel.closePdfViewer()
                                currentDestination = AppDestination.MainPortal
                            },
                            onPageChange = { delta -> viewModel.updatePdfPage(delta) },
                            onZoomChange = { delta -> viewModel.updatePdfZoom(delta) },
                            onSearchChange = { query -> viewModel.updatePdfSearchQuery(query) },
                            onDownloadPdf = {
                                viewModel.showStatus("PDF Download Started: ${pdfState.title}")
                            },
                            onSharePdf = {
                                viewModel.showStatus("Sharing document link...")
                            }
                        )
                    } ?: run {
                        currentDestination = AppDestination.MainPortal
                    }
                }

                AppDestination.GlobalSearch -> {
                    GlobalSearchScreen(
                        initialQuery = viewModel.globalSearchQuery.value,
                        notices = notices,
                        results = results,
                        pyqs = pyqs,
                        admissions = admissions,
                        scholarships = scholarships,
                        onClose = { currentDestination = AppDestination.MainPortal },
                        onNoticeClick = { notice ->
                            viewModel.openPdfViewer(
                                title = notice.title,
                                subtitle = "Notice • ${notice.category}",
                                pdfUrl = notice.pdfUrl
                            )
                            currentDestination = AppDestination.PdfViewer
                        },
                        onResultClick = { result ->
                            viewModel.openPdfViewer(
                                title = result.title,
                                subtitle = "Result • ${result.course}",
                                pdfUrl = result.pdfUrl
                            )
                            currentDestination = AppDestination.PdfViewer
                        },
                        onPyqClick = { pyq ->
                            viewModel.openPdfViewer(
                                title = pyq.title,
                                subtitle = "PYQ • ${pyq.course}",
                                pdfUrl = pyq.pdfUrl
                            )
                            currentDestination = AppDestination.PdfViewer
                        },
                        onBookmarkToggleNotice = { notice -> viewModel.toggleNoticeBookmark(notice.id, notice.isBookmarked) },
                        onBookmarkToggleResult = { result -> viewModel.toggleResultBookmark(result.id, result.isBookmarked) },
                        onBookmarkTogglePyq = { pyq -> viewModel.togglePyqBookmark(pyq.id, pyq.isBookmarked) }
                    )
                }

                AppDestination.AdminPanel -> {
                    AdminPanelScreen(
                        notices = notices,
                        results = results,
                        pyqs = pyqs,
                        onClose = { currentDestination = AppDestination.MainPortal },
                        onPublishNotice = { title, category, description, pdfUrl, isImportant ->
                            viewModel.adminPublishNotice(title, category, description, pdfUrl, isImportant)
                        },
                        onPublishResult = { title, course, session, pdfUrl ->
                            viewModel.adminPublishResult(title, course, session, pdfUrl)
                        },
                        onPublishPyq = { title, course, dept, year, sem, pdfUrl ->
                            viewModel.adminPublishPyq(title, course, dept, year, sem, pdfUrl)
                        },
                        onSendBroadcastNotification = { title, body, type ->
                            viewModel.adminSendBroadcast(title, body, type)
                        },
                        onDeleteNotice = { id -> viewModel.adminDeleteNotice(id) },
                        onDeleteResult = { id -> viewModel.adminDeleteResult(id) },
                        onDeletePyq = { id -> viewModel.adminDeletePyq(id) }
                    )
                }
            }
        }
    }
}
