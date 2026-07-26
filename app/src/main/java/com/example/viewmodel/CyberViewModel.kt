package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CyberDataRepository
import com.example.data.CyberDatabase
import com.example.data.NoteEntity
import com.example.data.UserProgressEntity
import com.example.model.Badge
import com.example.model.CTFChallenge
import com.example.model.CTFSubmissionResult
import com.example.model.CTFSubmissionStatus
import com.example.model.CourseModule
import com.example.model.Lesson
import com.example.service.GeminiCyberMentorService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isUser: Boolean,
    val timestamp: String = "14:00"
)

class CyberViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CyberDatabase.getDatabase(application)
    private val userProgressDao = db.userProgressDao()
    private val noteDao = db.noteDao()

    val userProgressState: StateFlow<UserProgressEntity?> = userProgressDao.getUserProgress()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProgressEntity()
        )

    val notesState: StateFlow<List<NoteEntity>> = noteDao.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedTab = MutableStateFlow(0) // 0: Cours, 1: Labs, 2: CTF, 3: Mentor IA, 4: Profil
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _modulesList = MutableStateFlow<List<CourseModule>>(CyberDataRepository.courseModules)
    val modulesList: StateFlow<List<CourseModule>> = _modulesList.asStateFlow()

    private val _selectedModule = MutableStateFlow<CourseModule?>(null)
    val selectedModule: StateFlow<CourseModule?> = _selectedModule.asStateFlow()

    private val _activeLesson = MutableStateFlow<Lesson?>(null)
    val activeLesson: StateFlow<Lesson?> = _activeLesson.asStateFlow()

    private val _activeLabId = MutableStateFlow<String?>(null)
    val activeLabId: StateFlow<String?> = _activeLabId.asStateFlow()

    private val _selectedCtf = MutableStateFlow<CTFChallenge?>(null)
    val selectedCtf: StateFlow<CTFChallenge?> = _selectedCtf.asStateFlow()

    private val _ctfFeedback = MutableStateFlow<Pair<String, Boolean>?>(null) // Message, isSuccess
    val ctfFeedback: StateFlow<Pair<String, Boolean>?> = _ctfFeedback.asStateFlow()

    private val _ctfSubmissions = MutableStateFlow<Map<String, CTFSubmissionResult>>(emptyMap())
    val ctfSubmissions: StateFlow<Map<String, CTFSubmissionResult>> = _ctfSubmissions.asStateFlow()

    private val _quizAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val quizAnswers: StateFlow<Map<Int, Int>> = _quizAnswers.asStateFlow()

    private val _quizSubmitted = MutableStateFlow(false)
    val quizSubmitted: StateFlow<Boolean> = _quizSubmitted.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _mentorMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Bonjour Cyber-Agent ! Je suis votre HackGuard CyberMentor. Posz-moi toutes vos questions sur l'injection SQL, Nmap, la cryptographie ou les défis CTF !",
                isUser = false
            )
        )
    )
    val mentorMessages: StateFlow<List<ChatMessage>> = _mentorMessages.asStateFlow()

    private val _isMentorLoading = MutableStateFlow(false)
    val isMentorLoading: StateFlow<Boolean> = _isMentorLoading.asStateFlow()

    init {
        viewModelScope.launch {
            val current = userProgressDao.getUserProgressDirect()
            if (current == null) {
                userProgressDao.insertOrUpdateProgress(
                    UserProgressEntity(
                        id = 1,
                        xp = 0,
                        level = 1,
                        completedLessons = "",
                        completedLabs = "",
                        capturedFlags = "",
                        streakDays = 1
                    )
                )
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun selectModule(module: CourseModule?) {
        _selectedModule.value = module
        _activeLesson.value = null
    }

    fun moveModuleUp(module: CourseModule) {
        val currentList = _modulesList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == module.id }
        if (index > 0) {
            val item = currentList.removeAt(index)
            currentList.add(index - 1, item)
            _modulesList.value = currentList
        }
    }

    fun moveModuleDown(module: CourseModule) {
        val currentList = _modulesList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == module.id }
        if (index in 0 until currentList.size - 1) {
            val item = currentList.removeAt(index)
            currentList.add(index + 1, item)
            _modulesList.value = currentList
        }
    }

    fun navigateToNextModule() {
        val currentMod = _selectedModule.value ?: return
        val list = _modulesList.value
        val currentIndex = list.indexOfFirst { it.id == currentMod.id }
        if (currentIndex in 0 until list.size - 1) {
            selectModule(list[currentIndex + 1])
        }
    }

    fun navigateToPreviousModule() {
        val currentMod = _selectedModule.value ?: return
        val list = _modulesList.value
        val currentIndex = list.indexOfFirst { it.id == currentMod.id }
        if (currentIndex > 0) {
            selectModule(list[currentIndex - 1])
        }
    }

    fun navigateToNextLesson(currentLesson: Lesson) {
        val currentMod = _selectedModule.value ?: return
        val lessons = currentMod.lessons
        val lessonIndex = lessons.indexOfFirst { it.id == currentLesson.id }
        if (lessonIndex in 0 until lessons.size - 1) {
            selectLesson(lessons[lessonIndex + 1])
        } else {
            // End of lessons in this module, try to move to next module
            navigateToNextModule()
        }
    }

    fun navigateToPreviousLesson(currentLesson: Lesson) {
        val currentMod = _selectedModule.value ?: return
        val lessons = currentMod.lessons
        val lessonIndex = lessons.indexOfFirst { it.id == currentLesson.id }
        if (lessonIndex > 0) {
            selectLesson(lessons[lessonIndex - 1])
        }
    }

    fun selectLesson(lesson: Lesson?) {
        _activeLesson.value = lesson
        _quizAnswers.value = emptyMap()
        _quizSubmitted.value = false
    }

    fun openLab(labId: String?) {
        _activeLabId.value = labId
    }

    fun selectCtf(ctf: CTFChallenge?) {
        _selectedCtf.value = ctf
        _ctfFeedback.value = null
    }

    fun onQuizAnswerSelected(questionId: Int, optionIndex: Int) {
        val updated = _quizAnswers.value.toMutableMap()
        updated[questionId] = optionIndex
        _quizAnswers.value = updated
    }

    fun submitQuiz(lesson: Lesson) {
        val answers = _quizAnswers.value
        var correctCount = 0
        lesson.quizQuestions.forEach { q ->
            if (answers[q.id] == q.correctIndex) {
                correctCount++
            }
        }
        _quizScore.value = correctCount
        _quizSubmitted.value = true

        // Award XP
        val addedXp = correctCount * 25 + 25
        awardXpAndCompleteLesson(lesson.id, addedXp)
    }

    private val _newlyUnlockedBadge = MutableStateFlow<Badge?>(null)
    val newlyUnlockedBadge: StateFlow<Badge?> = _newlyUnlockedBadge.asStateFlow()

    fun dismissBadgeDialog() {
        _newlyUnlockedBadge.value = null
    }

    fun getCalculatedBadges(userProgress: UserProgressEntity?): List<Badge> {
        val progress = userProgress ?: UserProgressEntity()
        val completedLessonsSet = progress.completedLessons.split(",").filter { it.isNotBlank() }.toSet()
        val completedLabsSet = progress.completedLabs.split(",").filter { it.isNotBlank() }.toSet()
        val capturedFlagsSet = progress.capturedFlags.split(",").filter { it.isNotBlank() }.toSet()

        return CyberDataRepository.defaultBadges.map { badge ->
            var isUnlocked = false
            var current = 0
            var max = 1

            if (badge.requiredXp > 0) {
                current = progress.xp
                max = badge.requiredXp
                isUnlocked = current >= max
            } else if (badge.requiredLessons > 0) {
                current = completedLessonsSet.size
                max = badge.requiredLessons
                isUnlocked = current >= max
            } else if (badge.requiredLabs > 0) {
                current = completedLabsSet.size
                max = badge.requiredLabs
                isUnlocked = current >= max
            } else if (badge.requiredCtfs > 0) {
                current = capturedFlagsSet.size
                max = badge.requiredCtfs
                isUnlocked = current >= max
            } else if (badge.requiredModuleId != null) {
                val mod = CyberDataRepository.courseModules.find { it.id == badge.requiredModuleId }
                if (mod != null && mod.lessons.isNotEmpty()) {
                    val modLessons = mod.lessons.map { it.id }
                    current = modLessons.count { completedLessonsSet.contains(it) }
                    max = modLessons.size
                    isUnlocked = current >= max
                }
            }

            badge.copy(
                isUnlocked = isUnlocked,
                progressCurrent = current.coerceAtMost(max),
                progressMax = max
            )
        }
    }

    private fun checkBadgeUnlocks(oldProgress: UserProgressEntity, newProgress: UserProgressEntity) {
        val oldBadges = getCalculatedBadges(oldProgress).associateBy { it.id }
        val newBadges = getCalculatedBadges(newProgress)

        for (newBadge in newBadges) {
            val oldBadge = oldBadges[newBadge.id]
            if (newBadge.isUnlocked && (oldBadge == null || !oldBadge.isUnlocked)) {
                _newlyUnlockedBadge.value = newBadge
                break // Show first unlocked badge
            }
        }
    }

    private fun awardXpAndCompleteLesson(lessonId: String, addedXp: Int) {
        viewModelScope.launch {
            val current = userProgressDao.getUserProgressDirect() ?: UserProgressEntity()
            val lessonsList = current.completedLessons.split(",").filter { it.isNotBlank() }.toMutableSet()
            lessonsList.add(lessonId)

            val newXp = current.xp + addedXp
            val newLevel = (newXp / 250) + 1

            val updated = current.copy(
                xp = newXp,
                level = newLevel,
                completedLessons = lessonsList.joinToString(",")
            )
            userProgressDao.insertOrUpdateProgress(updated)
            checkBadgeUnlocks(current, updated)
        }
    }

    fun completeLab(labId: String) {
        viewModelScope.launch {
            val current = userProgressDao.getUserProgressDirect() ?: UserProgressEntity()
            val labsList = current.completedLabs.split(",").filter { it.isNotBlank() }.toMutableSet()
            if (!labsList.contains(labId)) {
                labsList.add(labId)
                val newXp = current.xp + 100
                val newLevel = (newXp / 250) + 1
                val updated = current.copy(
                    xp = newXp,
                    level = newLevel,
                    completedLabs = labsList.joinToString(",")
                )
                userProgressDao.insertOrUpdateProgress(updated)
                checkBadgeUnlocks(current, updated)
            }
        }
    }

    fun submitCtfFlag(challenge: CTFChallenge, inputFlag: String) {
        verifyAndSubmitCtfFlag(challenge, inputFlag)
    }

    fun verifyAndSubmitCtfFlag(challenge: CTFChallenge, inputFlag: String) {
        val trimmed = inputFlag.trim()
        if (trimmed.isBlank()) {
            val res = CTFSubmissionResult(
                challengeId = challenge.id,
                status = CTFSubmissionStatus.INVALID_FORMAT,
                message = "⚠️ Veuillez saisir un flag non vide (ex: FLAG{...})."
            )
            updateCtfSubmissionState(challenge.id, res)
            _ctfFeedback.value = Pair(res.message, false)
            return
        }

        viewModelScope.launch {
            // Set VERIFYING state to simulate backend response processing
            updateCtfSubmissionState(
                challenge.id,
                CTFSubmissionResult(
                    challengeId = challenge.id,
                    status = CTFSubmissionStatus.VERIFYING,
                    message = "⏳ Vérification du flag par le serveur backend..."
                )
            )

            // Simulate backend network/crypto verification delay
            delay(500)

            val current = userProgressDao.getUserProgressDirect() ?: UserProgressEntity()
            val flagsSet = current.capturedFlags.split(",").filter { it.isNotBlank() }.toMutableSet()

            if (flagsSet.contains(challenge.id)) {
                val res = CTFSubmissionResult(
                    challengeId = challenge.id,
                    status = CTFSubmissionStatus.ALREADY_CAPTURED,
                    message = "ℹ️ Vous avez déjà capturé et validé ce Drapeau !"
                )
                updateCtfSubmissionState(challenge.id, res)
                _ctfFeedback.value = Pair(res.message, true)
                return@launch
            }

            // Backend verification check: exact match (case-insensitive)
            val isCorrect = trimmed.equals(challenge.flag, ignoreCase = true)

            if (isCorrect) {
                flagsSet.add(challenge.id)
                val newXp = current.xp + challenge.points
                val newLevel = (newXp / 250) + 1
                val updated = current.copy(
                    xp = newXp,
                    level = newLevel,
                    capturedFlags = flagsSet.joinToString(",")
                )
                userProgressDao.insertOrUpdateProgress(updated)
                checkBadgeUnlocks(current, updated)

                val successRes = CTFSubmissionResult(
                    challengeId = challenge.id,
                    status = CTFSubmissionStatus.SUCCESS,
                    message = "🎉 FLAG CORRECT ! Validation backend réussie. +${challenge.points} XP accordés !",
                    xpEarned = challenge.points
                )
                updateCtfSubmissionState(challenge.id, successRes)
                _ctfFeedback.value = Pair(successRes.message, true)
            } else {
                val wrongRes = CTFSubmissionResult(
                    challengeId = challenge.id,
                    status = CTFSubmissionStatus.WRONG_FLAG,
                    message = "❌ Flag Incorrect ! La signature ne correspond pas à la clé enregistrée sur le serveur."
                )
                updateCtfSubmissionState(challenge.id, wrongRes)
                _ctfFeedback.value = Pair(wrongRes.message, false)
            }
        }
    }

    private fun updateCtfSubmissionState(challengeId: String, result: CTFSubmissionResult) {
        val currentMap = _ctfSubmissions.value.toMutableMap()
        currentMap[challengeId] = result
        _ctfSubmissions.value = currentMap
    }

    fun sendMentorMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(text = userText, isUser = true)
        _mentorMessages.value = _mentorMessages.value + userMsg
        _isMentorLoading.value = true

        viewModelScope.launch {
            val reply = GeminiCyberMentorService.askCyberMentor(userText)
            _isMentorLoading.value = false
            _mentorMessages.value = _mentorMessages.value + ChatMessage(text = reply, isUser = false)
        }
    }

    fun addNote(title: String, content: String, category: String) {
        if (title.isBlank() || content.isBlank()) return
        viewModelScope.launch {
            noteDao.insertNote(NoteEntity(title = title, content = content, category = category))
        }
    }

    fun deleteNote(noteId: Int) {
        viewModelScope.launch {
            noteDao.deleteNoteById(noteId)
        }
    }

    fun getUnlockedBadges(userXp: Int): List<Badge> {
        return CyberDataRepository.defaultBadges.map { badge ->
            badge.copy(isUnlocked = userXp >= badge.requiredXp)
        }
    }
}
