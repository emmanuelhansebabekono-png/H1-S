package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.ui.components.verticalScrollbar
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CyberDataRepository
import com.example.data.NoteEntity
import com.example.data.UserProgressEntity
import com.example.model.Badge
import com.example.model.CTFChallenge
import com.example.model.CourseModule
import com.example.model.LabSimulation
import com.example.model.Lesson
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.viewmodel.CyberViewModel

@Composable
fun ProfileNotesScreen(
    viewModel: CyberViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProgress by viewModel.userProgressState.collectAsState()
    val notes by viewModel.notesState.collectAsState()

    val progress = userProgress ?: UserProgressEntity()
    val allBadges = viewModel.getCalculatedBadges(progress)
    var selectedBadgeCategory by remember { mutableStateOf("Tous") }
    var selectedBadgeForDetail by remember { mutableStateOf<Badge?>(null) }

    val unlockedCount = allBadges.count { it.isUnlocked }
    val totalBadgesCount = allBadges.size

    val filteredBadges = remember(allBadges, selectedBadgeCategory) {
        when (selectedBadgeCategory) {
            "Débloqués" -> allBadges.filter { it.isUnlocked }
            "Tous" -> allBadges
            else -> allBadges.filter { it.category == selectedBadgeCategory }
        }
    }

    val completedLessonIds = progress.completedLessons.split(",").filter { it.isNotBlank() }.toSet()
    val completedLabIds = progress.completedLabs.split(",").filter { it.isNotBlank() }.toSet()
    val capturedFlagIds = progress.capturedFlags.split(",").filter { it.isNotBlank() }.toSet()

    // Module stats
    val allModules = CyberDataRepository.courseModules
    val totalLessonsCount = allModules.sumOf { it.lessons.size }
    val completedModulesCount = allModules.count { module ->
        module.lessons.isNotEmpty() && module.lessons.all { completedLessonIds.contains(it.id) }
    }

    val totalLabsCount = CyberDataRepository.labSimulations.size
    val completedLabsCount = completedLabIds.size

    val totalCtfsCount = CyberDataRepository.ctfChallenges.size
    val capturedCtfsCount = capturedFlagIds.size

    val rankTitle = when {
        progress.level >= 10 -> "Légende Cyber Sentinel"
        progress.level >= 5 -> "Hacker Éthique Certifié"
        progress.level >= 3 -> "Analyste SOC Novice"
        else -> "Initié de l'Ombre"
    }

    // Dynamic suggestions logic
    val nextLesson: Pair<CourseModule, Lesson>? = remember(completedLessonIds) {
        var found: Pair<CourseModule, Lesson>? = null
        for (module in allModules) {
            for (lesson in module.lessons) {
                if (!completedLessonIds.contains(lesson.id)) {
                    found = Pair(module, lesson)
                    break
                }
            }
            if (found != null) break
        }
        found
    }

    val nextLab: LabSimulation? = remember(completedLabIds) {
        CyberDataRepository.labSimulations.firstOrNull { !completedLabIds.contains(it.id) }
    }

    val nextCtf: CTFChallenge? = remember(capturedFlagIds) {
        CyberDataRepository.ctfChallenges.firstOrNull { !capturedFlagIds.contains(it.id) }
    }

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var newNoteTitle by remember { mutableStateOf("") }
    var newNoteContent by remember { mutableStateOf("") }

    val profileListState = rememberLazyListState()

    LazyColumn(
        state = profileListState,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScrollbar(profileListState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile & Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_profile_card"),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CyberGreen)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(CyberGreen.copy(alpha = 0.15f))
                                .border(2.dp, CyberGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CyberGreen,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Agent EthicTrack",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = rankTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = CyberGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // XP Level Bar
                    val nextLevelXp = progress.level * 250
                    val currentLevelXp = (progress.level - 1) * 250
                    val xpInLevel = progress.xp - currentLevelXp
                    val xpProgressFraction = (xpInLevel.toFloat() / 250f).coerceIn(0f, 1f)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Niveau ${progress.level}",
                            color = CyberGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${progress.xp} / $nextLevelXp XP",
                            color = CyberAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { xpProgressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = CyberGreen,
                        trackColor = CyberSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quick Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatMetricBox(
                            count = "$completedModulesCount / ${allModules.size}",
                            label = "Modules"
                        )
                        StatMetricBox(
                            count = "${completedLessonIds.size} / $totalLessonsCount",
                            label = "Leçons"
                        )
                        StatMetricBox(
                            count = "$completedLabsCount / $totalLabsCount",
                            label = "Labs"
                        )
                        StatMetricBox(
                            count = "$capturedCtfsCount / $totalCtfsCount",
                            label = "CTF Flags"
                        )
                    }
                }
            }
        }

        // --- SMART RECOMMENDATIONS & NEXT STEPS ---
        item {
            Text(
                text = "PROCHAINES ÉTAPES RECOMMANDÉES",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CyberGreen,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("next_steps_card"),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CyberAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Suggestions basées sur votre progression",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyberAmber
                        )
                    }

                    // Next Lesson Suggestion
                    if (nextLesson != null) {
                        val (module, lesson) = nextLesson
                        RecommendationRow(
                            icon = Icons.Default.School,
                            tag = "COURS REGULIER",
                            title = lesson.title,
                            subtitle = "Module : ${module.title}",
                            actionText = "Étudier",
                            onAction = {
                                viewModel.selectTab(0)
                                viewModel.selectModule(module)
                                viewModel.selectLesson(lesson)
                            }
                        )
                    } else {
                        Text(
                            text = "🎉 Tous les cours du programme ont été complétés avec succès !",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CyberGreen
                        )
                    }

                    // Next Lab Suggestion
                    if (nextLab != null) {
                        RecommendationRow(
                            icon = Icons.Default.Terminal,
                            tag = "SIMULATION LAB",
                            title = nextLab.title,
                            subtitle = nextLab.category,
                            actionText = "Lancer Lab",
                            onAction = {
                                viewModel.selectTab(1)
                                viewModel.openLab(nextLab.id)
                            }
                        )
                    }

                    // Next CTF Suggestion
                    if (nextCtf != null) {
                        RecommendationRow(
                            icon = Icons.Default.Flag,
                            tag = "DÉFI CTF",
                            title = nextCtf.title,
                            subtitle = "${nextCtf.category} (+${nextCtf.points} XP)",
                            actionText = "Capturer Flag",
                            onAction = {
                                viewModel.selectTab(2)
                                viewModel.selectCtf(nextCtf)
                            }
                        )
                    }
                }
            }
        }

        // --- SECTION: MODULES TERMINÉS ET PROGRESSION ---
        item {
            Text(
                text = "SUIVI DES MODULES & LEÇONS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CyberGreen,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        items(allModules) { module ->
            val completedInModule = module.lessons.count { completedLessonIds.contains(it.id) }
            val moduleProgressFraction = if (module.lessons.isNotEmpty()) {
                completedInModule.toFloat() / module.lessons.size
            } else 0f
            val isModuleComplete = completedInModule == module.lessons.size && module.lessons.isNotEmpty()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("module_progress_${module.id}"),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(
                    1.dp,
                    if (isModuleComplete) CyberGreen else CyberSurfaceBorder
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = module.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = module.difficulty,
                                style = MaterialTheme.typography.bodySmall,
                                color = CyberCyan
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isModuleComplete) CyberGreen.copy(alpha = 0.2f) else CyberSurfaceVariant
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$completedInModule / ${module.lessons.size} Leçons",
                                color = if (isModuleComplete) CyberGreen else Color.LightGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { moduleProgressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (isModuleComplete) CyberGreen else CyberCyan,
                        trackColor = CyberSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lessons checklist
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        module.lessons.forEach { lesson ->
                            val isLessonDone = completedLessonIds.contains(lesson.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberSurfaceVariant.copy(alpha = 0.5f))
                                    .clickable {
                                        viewModel.selectTab(0)
                                        viewModel.selectModule(module)
                                        viewModel.selectLesson(lesson)
                                    }
                                    .padding(vertical = 8.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isLessonDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isLessonDone) CyberGreen else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = lesson.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isLessonDone) Color.White else Color.LightGray,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${lesson.durationMinutes} min",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- SECTION: EXERCICES & QUIZZ SCORES ---
        item {
            Text(
                text = "SCORES AUX EXERCICES & QUIZZ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CyberGreen,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_scores_card"),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, CyberSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Bilan des Évaluations",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "${completedLessonIds.size} Quizz Validés",
                            color = CyberGreen,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val totalQuizQuestions = allModules.flatMap { it.lessons }.sumOf { it.quizQuestions.size }
                    val estimatedPointsGained = completedLessonIds.size * 50

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Questions totales du programme",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                        Text(
                            text = "$totalQuizQuestions Questions",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "XP cumulés via les exercices",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                        Text(
                            text = "+$estimatedPointsGained XP",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- SECTION: SIMULATIONS & LABS RÉUSSIS ---
        item {
            Text(
                text = "SIMULATIONS RÉUSSIES & CTFs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CyberGreen,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CyberDataRepository.labSimulations.forEach { lab ->
                    val isLabCompleted = completedLabIds.contains(lab.id)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectTab(1)
                                viewModel.openLab(lab.id)
                            }
                            .testTag("lab_status_${lab.id}"),
                        colors = CardDefaults.cardColors(containerColor = CyberSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isLabCompleted) CyberGreen else CyberSurfaceBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isLabCompleted) CyberGreen.copy(alpha = 0.2f) else CyberSurfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = null,
                                    tint = if (isLabCompleted) CyberGreen else Color.Gray,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lab.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = lab.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CyberCyan
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isLabCompleted) CyberGreen.copy(alpha = 0.2f) else CyberSurfaceVariant
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isLabCompleted) "RÉUSSI ✓" else "À RÉALISER",
                                    color = if (isLabCompleted) CyberGreen else Color.Gray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- SECTION: BADGES & ACCOMPLISSEMENTS ---
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "BADGES & RECOMPENSES DE PROGRESSION",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CyberGreen,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("badges_summary_card"),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = CyberAmber,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Progression des Trophées",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Text(
                                text = "$unlockedCount / $totalBadgesCount Badges",
                                color = CyberAmber,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val badgeProgressFraction = if (totalBadgesCount > 0) unlockedCount.toFloat() / totalBadgesCount else 0f
                        LinearProgressIndicator(
                            progress = { badgeProgressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = CyberAmber,
                            trackColor = CyberSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Category filter chips
                        val badgeCategories = listOf("Tous", "Débloqués", "Modules", "CTF", "Labs", "XP")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            badgeCategories.forEach { cat ->
                                val isSelected = selectedBadgeCategory == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) CyberAmber else CyberSurfaceVariant)
                                        .clickable { selectedBadgeCategory = cat }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("badge_filter_$cat")
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSelected) Color.Black else Color.LightGray,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (filteredBadges.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucun badge dans cette catégorie pour le moment.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    filteredBadges.forEach { badge ->
                        BadgeCard(
                            badge = badge,
                            onClick = { selectedBadgeForDetail = badge }
                        )
                    }
                }
            }
        }

        // --- SECTION: BLOC-NOTES DU HACKER ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BLOC-NOTES DU HACKER",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CyberGreen,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                IconButton(
                    onClick = { showAddNoteDialog = !showAddNoteDialog },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(CyberGreen)
                        .size(32.dp)
                        .testTag("add_note_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ajouter Note",
                        tint = Color.Black
                    )
                }
            }
        }

        if (showAddNoteDialog) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CyberCyan)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Nouvelle Note d'Étude",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newNoteTitle,
                            onValueChange = { newNoteTitle = it },
                            label = { Text("Titre (ex: Commandes Nmap)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("note_title_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberGreen,
                                unfocusedBorderColor = CyberSurfaceBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newNoteContent,
                            onValueChange = { newNoteContent = it },
                            label = { Text("Contenu / Mémo commande") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("note_content_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberGreen,
                                unfocusedBorderColor = CyberSurfaceBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    if (newNoteTitle.isNotBlank() && newNoteContent.isNotBlank()) {
                                        viewModel.addNote(newNoteTitle, newNoteContent, "Général")
                                        newNoteTitle = ""
                                        newNoteContent = ""
                                        showAddNoteDialog = false
                                        Toast.makeText(context, "Note enregistrée !", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
                            ) {
                                Text("Enregistrer la Note", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (notes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aucune note personnelle.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )
                        Text(
                            text = "Ajoutez des mémos sur les commandes et les flags CTF !",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(notes) { note ->
                NoteCard(
                    note = note,
                    onDelete = { viewModel.deleteNote(note.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    selectedBadgeForDetail?.let { badge ->
        BadgeDetailModal(
            badge = badge,
            onDismiss = { selectedBadgeForDetail = null },
            onNavigateToCategory = {
                selectedBadgeForDetail = null
                when (badge.category) {
                    "Modules" -> viewModel.selectTab(0)
                    "CTF" -> viewModel.selectTab(2)
                    "Labs" -> viewModel.selectTab(1)
                    else -> viewModel.selectTab(0)
                }
            }
        )
    }
}

@Composable
fun BadgeDetailModal(
    badge: Badge,
    onDismiss: () -> Unit,
    onNavigateToCategory: () -> Unit
) {
    val iconVector = when (badge.icon) {
        "school" -> Icons.Default.School
        "terminal" -> Icons.Default.Terminal
        "code" -> Icons.Default.Code
        "flag" -> Icons.Default.Flag
        "bolt" -> Icons.Default.AutoAwesome
        "trophy" -> Icons.Default.Lightbulb
        "star" -> Icons.Default.Star
        else -> Icons.Default.Shield
    }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("badge_detail_modal"),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, if (badge.isUnlocked) CyberAmber else CyberCyan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (badge.isUnlocked) CyberAmber.copy(alpha = 0.2f) else CyberSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (badge.isUnlocked) iconVector else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (badge.isUnlocked) CyberAmber else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = badge.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Catégorie : ${badge.category}",
                        fontSize = 11.sp,
                        color = CyberCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progression exigée :",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "${badge.progressCurrent} / ${badge.progressMax}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (badge.isUnlocked) CyberAmber else CyberGreen,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val progressFraction = if (badge.progressMax > 0) {
                    (badge.progressCurrent.toFloat() / badge.progressMax.toFloat()).coerceIn(0f, 1f)
                } else 0f

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (badge.isUnlocked) CyberAmber else CyberGreen,
                    trackColor = CyberSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Fermer", color = Color.White, fontSize = 12.sp)
                    }

                    if (!badge.isUnlocked) {
                        Button(
                            onClick = onNavigateToCategory,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = when (badge.category) {
                                    "CTF" -> "Aller au CTF"
                                    "Labs" -> "Ouvrir Labs"
                                    else -> "Continuer Cours"
                                },
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatMetricBox(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = CyberGreen,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.LightGray,
            fontSize = 11.sp
        )
    }
}

@Composable
fun RecommendationRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tag: String,
    title: String,
    subtitle: String,
    actionText: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurfaceVariant.copy(alpha = 0.6f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CyberGreen.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tag,
                fontSize = 9.sp,
                color = CyberCyan,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onAction,
            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = actionText,
                    color = Color.Black,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun BadgeCard(
    badge: Badge,
    onClick: () -> Unit
) {
    val iconVector = when (badge.icon) {
        "school" -> Icons.Default.School
        "terminal" -> Icons.Default.Terminal
        "code" -> Icons.Default.Code
        "flag" -> Icons.Default.Flag
        "bolt" -> Icons.Default.AutoAwesome
        "trophy" -> Icons.Default.Lightbulb
        "star" -> Icons.Default.Star
        else -> Icons.Default.Shield
    }

    val progressFraction = if (badge.progressMax > 0) {
        (badge.progressCurrent.toFloat() / badge.progressMax.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("badge_item_${badge.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) CyberSurface else CyberSurfaceVariant.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (badge.isUnlocked) CyberAmber else CyberSurfaceBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (badge.isUnlocked) CyberAmber.copy(alpha = 0.25f) else CyberSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (badge.isUnlocked) iconVector else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (badge.isUnlocked) CyberAmber else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = badge.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (badge.isUnlocked) Color.White else Color.LightGray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberSurfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge.category,
                                fontSize = 9.sp,
                                color = CyberCyan,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = badge.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (badge.isUnlocked) CyberAmber.copy(alpha = 0.2f) else CyberSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (badge.isUnlocked) "DÉBLOQUÉ ✓" else "${badge.progressCurrent}/${badge.progressMax}",
                        color = if (badge.isUnlocked) CyberAmber else Color.Gray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (!badge.isUnlocked && badge.progressMax > 1) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = CyberCyan,
                    trackColor = CyberSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun NoteCard(
    note: NoteEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CyberSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CyberGreen
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = CyberRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

