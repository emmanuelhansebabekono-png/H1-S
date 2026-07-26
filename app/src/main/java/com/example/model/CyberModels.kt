package com.example.model

data class CourseModule(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val difficulty: String, // "Débutant", "Intermédiaire", "Avancé"
    val lessons: List<Lesson>
)

data class Lesson(
    val id: String,
    val moduleId: String,
    val title: String,
    val durationMinutes: Int,
    val summary: String,
    val content: String, // Full lesson text with markdown style headers and code
    val codeSnippet: String? = null,
    val quizQuestions: List<QuizQuestion> = emptyList()
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class LabSimulation(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val difficulty: String,
    val isCompleted: Boolean = false
)

data class CTFChallenge(
    val id: String,
    val title: String,
    val category: String, // "Reconnaissance", "Web Vulnerabilities", "Cryptography", "Forensics", "Steganography"
    val points: Int,
    val difficulty: String, // "Facile", "Moyen", "Difficile"
    val description: String,
    val hint: String,
    val targetAddress: String? = null,
    val flag: String, // e.g. "FLAG{sql_injection_mastered}"
    val isCaptured: Boolean = false,
    val instructions: String? = null,
    val flagFormat: String = "FLAG{...}",
    val hashVerification: String? = null
)

enum class CTFSubmissionStatus {
    IDLE,
    VERIFYING,
    SUCCESS,
    WRONG_FLAG,
    ALREADY_CAPTURED,
    INVALID_FORMAT
}

data class CTFSubmissionResult(
    val challengeId: String,
    val status: CTFSubmissionStatus,
    val message: String,
    val xpEarned: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val requiredXp: Int = 0,
    val requiredLessons: Int = 0,
    val requiredLabs: Int = 0,
    val requiredCtfs: Int = 0,
    val requiredModuleId: String? = null,
    val category: String = "Général", // "XP", "Modules", "CTF", "Labs"
    val isUnlocked: Boolean = false,
    val progressCurrent: Int = 0,
    val progressMax: Int = 1
)
