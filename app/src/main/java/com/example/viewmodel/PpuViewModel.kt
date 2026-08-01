package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UserProfileState(
    val isLoggedIn: Boolean = false,
    val email: String = "guest@ppu.ac.in",
    val name: String = "Guest Student",
    val course: String = "B.A / B.Sc / BCA",
    val rollNo: String = "2026PPU10492",
    val isAdmin: Boolean = false,
    val pushNotificationsEnabled: Boolean = true,
    val isDarkMode: Boolean = false
)

data class PdfDocumentState(
    val title: String = "",
    val subtitle: String = "",
    val pdfUrl: String = "",
    val category: String = "",
    val date: String = "",
    val totalPages: Int = 12,
    val currentPage: Int = 1,
    val zoomLevel: Float = 1.0f,
    val searchQuery: String = "",
    val isBookmarked: Boolean = false
)

class PpuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PpuRepository

    // State flows
    val notices: StateFlow<List<NoticeEntity>>
    val results: StateFlow<List<ResultEntity>>
    val pyqs: StateFlow<List<PyqEntity>>
    val admissions: StateFlow<List<AdmissionEntity>>
    val scholarships: StateFlow<List<ScholarshipEntity>>
    val banners: StateFlow<List<BannerEntity>>
    val notifications: StateFlow<List<UserNotificationEntity>>

    val bookmarkedNotices: StateFlow<List<NoticeEntity>>
    val bookmarkedResults: StateFlow<List<ResultEntity>>
    val bookmarkedPyqs: StateFlow<List<PyqEntity>>

    // Search and filters
    private val _globalSearchQuery = MutableStateFlow("")
    val globalSearchQuery: StateFlow<String> = _globalSearchQuery.asStateFlow()

    private val _noticeCategoryFilter = MutableStateFlow("All")
    val noticeCategoryFilter: StateFlow<String> = _noticeCategoryFilter.asStateFlow()

    private val _resultCourseFilter = MutableStateFlow("All")
    val resultCourseFilter: StateFlow<String> = _resultCourseFilter.asStateFlow()

    private val _pyqCourseFilter = MutableStateFlow("All")
    val pyqCourseFilter: StateFlow<String> = _pyqCourseFilter.asStateFlow()

    // User Profile & Settings
    private val _userState = MutableStateFlow(UserProfileState())
    val userState: StateFlow<UserProfileState> = _userState.asStateFlow()

    // Active PDF Document in Viewer
    private val _activePdfState = MutableStateFlow<PdfDocumentState?>(null)
    val activePdfState: StateFlow<PdfDocumentState?> = _activePdfState.asStateFlow()

    // Status Message / Toast feedback
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        val database = PpuDatabase.getDatabase(application)
        repository = PpuRepository(database.ppuDao())

        // ViewModel scope initial setup
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }

        notices = repository.allNotices.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        results = repository.allResults.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        pyqs = repository.allPyqs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        admissions = repository.allAdmissions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        scholarships = repository.allScholarships.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        banners = repository.allBanners.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        notifications = repository.allNotifications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        bookmarkedNotices = repository.bookmarkedNotices.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        bookmarkedResults = repository.bookmarkedResults.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        bookmarkedPyqs = repository.bookmarkedPyqs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun updateSearchQuery(query: String) {
        _globalSearchQuery.value = query
    }

    fun setNoticeCategoryFilter(category: String) {
        _noticeCategoryFilter.value = category
    }

    fun setResultCourseFilter(course: String) {
        _resultCourseFilter.value = course
    }

    fun setPyqCourseFilter(course: String) {
        _pyqCourseFilter.value = course
    }

    fun toggleNoticeBookmark(id: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleNoticeBookmark(id, isBookmarked)
            showStatus(if (!isBookmarked) "Notice added to Bookmarks" else "Notice removed from Bookmarks")
        }
    }

    fun toggleResultBookmark(id: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleResultBookmark(id, isBookmarked)
            showStatus(if (!isBookmarked) "Result added to Bookmarks" else "Result removed from Bookmarks")
        }
    }

    fun togglePyqBookmark(id: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.togglePyqBookmark(id, isBookmarked)
            showStatus(if (!isBookmarked) "PYQ added to Bookmarks" else "PYQ removed from Bookmarks")
        }
    }

    fun openPdfViewer(title: String, subtitle: String, pdfUrl: String, category: String = "", date: String = "") {
        _activePdfState.value = PdfDocumentState(
            title = title,
            subtitle = subtitle,
            pdfUrl = pdfUrl,
            category = category,
            date = date
        )
    }

    fun closePdfViewer() {
        _activePdfState.value = null
    }

    fun updatePdfPage(delta: Int) {
        _activePdfState.value?.let { current ->
            val newPage = (current.currentPage + delta).coerceIn(1, current.totalPages)
            _activePdfState.value = current.copy(currentPage = newPage)
        }
    }

    fun updatePdfZoom(delta: Float) {
        _activePdfState.value?.let { current ->
            val newZoom = (current.zoomLevel + delta).coerceIn(0.5f, 3.0f)
            _activePdfState.value = current.copy(zoomLevel = newZoom)
        }
    }

    fun updatePdfSearchQuery(query: String) {
        _activePdfState.value?.let { current ->
            _activePdfState.value = current.copy(searchQuery = query)
        }
    }

    // Auth actions
    fun loginAsGuest() {
        _userState.value = _userState.value.copy(
            isLoggedIn = true,
            email = "guest@ppu.ac.in",
            name = "Guest Student",
            isAdmin = false
        )
        showStatus("Logged in as Guest")
    }

    fun loginWithEmail(email: String, name: String, rollNo: String) {
        _userState.value = _userState.value.copy(
            isLoggedIn = true,
            email = email,
            name = name,
            rollNo = rollNo,
            isAdmin = false
        )
        showStatus("Welcome, $name!")
    }

    fun loginAsAdmin(adminKey: String) {
        if (adminKey == "admin123" || adminKey == "ppu2026") {
            _userState.value = _userState.value.copy(
                isLoggedIn = true,
                email = "admin@ppup.ac.in",
                name = "PPU Portal Admin",
                isAdmin = true
            )
            showStatus("Admin Access Granted!")
        } else {
            showStatus("Invalid Admin Key! Try 'admin123'")
        }
    }

    fun logout() {
        _userState.value = UserProfileState()
        showStatus("Logged out successfully")
    }

    fun toggleDarkMode(enabled: Boolean) {
        _userState.value = _userState.value.copy(isDarkMode = enabled)
    }

    fun togglePushNotifications(enabled: Boolean) {
        _userState.value = _userState.value.copy(pushNotificationsEnabled = enabled)
        showStatus(if (enabled) "Push Notifications Enabled" else "Push Notifications Muted")
    }

    // Admin Publishing operations
    fun adminPublishNotice(title: String, category: String, description: String, pdfUrl: String, isImportant: Boolean) {
        viewModelScope.launch {
            val notice = NoticeEntity(
                id = "n_${System.currentTimeMillis()}",
                title = title,
                category = category,
                date = "Today",
                description = description,
                pdfUrl = if (pdfUrl.isBlank()) "https://ppup.ac.in/notices/Official_Notice.pdf" else pdfUrl,
                isImportant = isImportant
            )
            repository.insertNotice(notice)
            showStatus("Notice Published & Broadcasted!")
        }
    }

    fun adminPublishResult(title: String, course: String, session: String, pdfUrl: String) {
        viewModelScope.launch {
            val result = ResultEntity(
                id = "r_${System.currentTimeMillis()}",
                title = title,
                course = course,
                session = session,
                publishDate = "Today",
                pdfUrl = if (pdfUrl.isBlank()) "https://ppup.ac.in/results/Result_Official.pdf" else pdfUrl
            )
            repository.insertResult(result)
            showStatus("Result Published!")
        }
    }

    fun adminPublishPyq(title: String, course: String, department: String, year: String, semester: String, pdfUrl: String) {
        viewModelScope.launch {
            val pyq = PyqEntity(
                id = "p_${System.currentTimeMillis()}",
                title = title,
                course = course,
                department = department,
                year = year,
                semester = semester,
                pdfUrl = if (pdfUrl.isBlank()) "https://ppup.ac.in/pyq/PYQ_Paper.pdf" else pdfUrl
            )
            repository.insertPyq(pyq)
            showStatus("PYQ Paper Uploaded!")
        }
    }

    fun adminDeleteNotice(id: String) {
        viewModelScope.launch {
            repository.deleteNotice(id)
            showStatus("Notice deleted")
        }
    }

    fun adminDeleteResult(id: String) {
        viewModelScope.launch {
            repository.deleteResult(id)
            showStatus("Result deleted")
        }
    }

    fun adminDeletePyq(id: String) {
        viewModelScope.launch {
            repository.deletePyq(id)
            showStatus("PYQ deleted")
        }
    }

    fun adminSendBroadcast(title: String, body: String, type: String) {
        viewModelScope.launch {
            repository.sendBroadcastNotification(title, body, type)
            showStatus("Push Notification Sent to All Users!")
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun showStatus(msg: String) {
        _statusMessage.value = msg
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
