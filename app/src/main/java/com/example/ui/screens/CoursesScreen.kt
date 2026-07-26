package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.ui.components.verticalScrollbar
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CyberDataRepository
import com.example.data.UserProgressEntity
import com.example.model.CourseModule
import com.example.model.Lesson
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTerminalBg
import com.example.viewmodel.CyberViewModel

@Composable
fun CoursesScreen(
    viewModel: CyberViewModel,
    modifier: Modifier = Modifier
) {
    val selectedModule by viewModel.selectedModule.collectAsState()
    val activeLesson by viewModel.activeLesson.collectAsState()
    val userProgress by viewModel.userProgressState.collectAsState()
    val modulesList by viewModel.modulesList.collectAsState()

    val progress = userProgress ?: UserProgressEntity()

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when {
            activeLesson != null -> {
                LessonDetailScreen(
                    lesson = activeLesson!!,
                    viewModel = viewModel,
                    userProgress = progress,
                    onBack = { viewModel.selectLesson(null) }
                )
            }
            selectedModule != null -> {
                ModuleDetailScreen(
                    module = selectedModule!!,
                    viewModel = viewModel,
                    userProgress = progress,
                    onBack = { viewModel.selectModule(null) }
                )
            }
            else -> {
                CourseModulesListScreen(
                    modules = modulesList,
                    userProgress = progress,
                    onModuleClick = { viewModel.selectModule(it) },
                    onMoveUp = { viewModel.moveModuleUp(it) },
                    onMoveDown = { viewModel.moveModuleDown(it) }
                )
            }
        }
    }
}

@Composable
fun CourseModulesListScreen(
    modules: List<CourseModule>,
    userProgress: UserProgressEntity,
    onModuleClick: (CourseModule) -> Unit,
    onMoveUp: (CourseModule) -> Unit,
    onMoveDown: (CourseModule) -> Unit
) {
    val context = LocalContext.current
    val bannerDrawableId = context.resources.getIdentifier(
        "cyber_banner_1785013422965", "drawable", context.packageName
    )

    val completedSet = userProgress.completedLessons.split(",").toSet()
    val modulesListState = rememberLazyListState()

    LazyColumn(
        state = modulesListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScrollbar(modulesListState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("course_hero_banner"),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
            ) {
                Column {
                    if (bannerDrawableId != 0) {
                        Image(
                            painter = painterResource(id = bannerDrawableId),
                            contentDescription = "Cyber Banner",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CyberGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PARCOURS PÉDAGOGIQUE",
                                style = MaterialTheme.typography.labelMedium,
                                color = CyberGreen,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Devenez un Hacker Éthique",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Apprenez les bases de la cybersécurité, du terminal Linux aux vulnérabilités web OWASP à travers 5 modules progressifs.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        // Horizontal Scroll Bar (Ascenseur horizontal de navigation rapide par module)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Navigation rapide par défilement horizontal :",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    modules.forEach { mod ->
                        FilterChip(
                            selected = false,
                            onClick = { onModuleClick(mod) },
                            label = { Text(mod.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = null,
                                    tint = CyberGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = CyberSurfaceVariant,
                                labelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = false,
                                borderColor = CyberSurfaceBorder
                            )
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MODULES D'APPRENTISSAGE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CyberGreen,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Utilisez ▲/▼ pour réorganiser",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan
                )
            }
        }

        itemsIndexed(modules) { index, module ->
            val totalLessons = module.lessons.size
            val completedInModule = module.lessons.count { completedSet.contains(it.id) }
            val progressFraction = if (totalLessons > 0) completedInModule.toFloat() / totalLessons else 0f

            ModuleCard(
                module = module,
                completedCount = completedInModule,
                totalCount = totalLessons,
                progressFraction = progressFraction,
                onClick = { onModuleClick(module) },
                onMoveUp = { onMoveUp(module) },
                onMoveDown = { onMoveDown(module) },
                isFirst = index == 0,
                isLast = index == modules.size - 1
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ModuleCard(
    module: CourseModule,
    completedCount: Int,
    totalCount: Int,
    progressFraction: Float,
    onClick: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean
) {
    val iconVector = when (module.iconName) {
        "shield" -> Icons.Default.Shield
        "radar" -> Icons.Default.Radar
        "code" -> Icons.Default.Code
        "email" -> Icons.Default.Email
        "lock" -> Icons.Default.Lock
        else -> Icons.Default.Book
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("module_card_${module.id}"),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberGreen.copy(alpha = 0.15f))
                            .border(1.dp, CyberGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = CyberGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = module.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${module.lessons.size} Leçons • ${module.difficulty}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberCyan
                        )
                    }
                }

                // Monter / Descendre reordering buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onMoveUp,
                        enabled = !isFirst,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Monter le module",
                            tint = if (!isFirst) CyberGreen else Color.Gray.copy(alpha = 0.3f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(
                        onClick = onMoveDown,
                        enabled = !isLast,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Descendre le module",
                            tint = if (!isLast) CyberGreen else Color.Gray.copy(alpha = 0.3f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = module.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = CyberGreen,
                    trackColor = CyberSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$completedCount/$totalCount Fait",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (completedCount == totalCount && totalCount > 0) CyberGreen else CyberAmber,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ModuleDetailScreen(
    module: CourseModule,
    viewModel: CyberViewModel,
    userProgress: UserProgressEntity,
    onBack: () -> Unit
) {
    val completedSet = userProgress.completedLessons.split(",").toSet()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_button_module")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = CyberGreen
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = module.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = { viewModel.navigateToPreviousModule() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Préc.", fontSize = 11.sp)
                }

                Button(
                    onClick = { viewModel.navigateToNextModule() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Suiv.", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Module Summary Header Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = module.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Niveau: ${module.difficulty} • ${module.lessons.size} Leçons au total",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberGreen,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val lessonsListState = rememberLazyListState()

        LazyColumn(
            state = lessonsListState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .verticalScrollbar(lessonsListState)
        ) {
            items(module.lessons) { lesson ->
                val isCompleted = completedSet.contains(lesson.id)
                LessonCard(
                    lesson = lesson,
                    isCompleted = isCompleted,
                    onClick = { viewModel.selectLesson(lesson) }
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { viewModel.navigateToPreviousModule() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Module Précédent", color = CyberCyan, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.navigateToNextModule() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
                    ) {
                        Text("Module Suivant", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun LessonCard(
    lesson: Lesson,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("lesson_card_${lesson.id}"),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCompleted) CyberGreen.copy(alpha = 0.5f) else CyberSurfaceBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) CyberGreen.copy(alpha = 0.2f) else CyberSurfaceVariant)
                    .border(1.dp, if (isCompleted) CyberGreen else CyberSurfaceBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (isCompleted) CyberGreen else CyberCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${lesson.durationMinutes} min • +50 XP",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberCyan
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lesson.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun LessonDetailScreen(
    lesson: Lesson,
    viewModel: CyberViewModel,
    userProgress: UserProgressEntity,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val quizAnswers by viewModel.quizAnswers.collectAsState()
    val quizSubmitted by viewModel.quizSubmitted.collectAsState()
    val quizScore by viewModel.quizScore.collectAsState()

    val isCompleted = userProgress.completedLessons.split(",").contains(lesson.id)
    val lessonDetailState = rememberLazyListState()

    LazyColumn(
        state = lessonDetailState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScrollbar(lessonDetailState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_button_lesson")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = CyberGreen
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = lesson.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (isCompleted) {
                            Text(
                                text = "✓ Leçon Terminée (+50 XP)",
                                color = CyberGreen,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.navigateToPreviousLesson(lesson) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Préc.", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { viewModel.navigateToNextLesson(lesson) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Suiv.", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // Lesson Content Body
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = lesson.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray,
                        lineHeight = 22.sp
                    )

                    // Code Snippet Box
                    if (!lesson.codeSnippet.isNull_or_blank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "TERMINAL / CODE EXEMPLE :",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberTerminalBg)
                                .border(1.dp, CyberGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "bash",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberGreen,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                            val clip = android.content.ClipData.newPlainText("Code", lesson.codeSnippet ?: "")
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Code copié !", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copier",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    color = CyberSurfaceBorder
                                )
                                Text(
                                    text = lesson.codeSnippet ?: "",
                                    color = CyberGreen,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quiz Section
        if (lesson.quizQuestions.isNotEmpty()) {
            item {
                Text(
                    text = "QUIZ DE VALIDATION DES COMPÉTENCES",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CyberGreen,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            items(lesson.quizQuestions) { question ->
                QuizQuestionCard(
                    question = question,
                    selectedOption = quizAnswers[question.id],
                    isSubmitted = quizSubmitted,
                    onOptionSelected = { optionIndex ->
                        if (!quizSubmitted) {
                            viewModel.onQuizAnswerSelected(question.id, optionIndex)
                        }
                    }
                )
            }

            item {
                if (!quizSubmitted) {
                    Button(
                        onClick = { viewModel.submitQuiz(lesson) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_quiz_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Valider les Réponses (+50 XP)",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberGreen.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎉 Quiz Validé ! Score : $quizScore / ${lesson.quizQuestions.size}",
                                style = MaterialTheme.typography.titleMedium,
                                color = CyberGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Points XP attribués et enregistrés dans votre progression.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.navigateToPreviousLesson(lesson) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Leçon Précédente", color = CyberCyan, fontSize = 12.sp)
                }

                Button(
                    onClick = { viewModel.navigateToNextLesson(lesson) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
                ) {
                    Text("Leçon Suivante", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun QuizQuestionCard(
    question: com.example.model.QuizQuestion,
    selectedOption: Int?,
    isSubmitted: Boolean,
    onOptionSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Question : ${question.question}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            question.options.forEachIndexed { index, optionText ->
                val isCorrect = index == question.correctIndex
                val isSelected = selectedOption == index

                val optionBg = when {
                    isSubmitted && isCorrect -> CyberGreen.copy(alpha = 0.2f)
                    isSubmitted && isSelected && !isCorrect -> Color.Red.copy(alpha = 0.2f)
                    isSelected -> CyberCyan.copy(alpha = 0.15f)
                    else -> CyberSurfaceVariant
                }

                val optionBorder = when {
                    isSubmitted && isCorrect -> CyberGreen
                    isSubmitted && isSelected && !isCorrect -> Color.Red
                    isSelected -> CyberCyan
                    else -> CyberSurfaceBorder
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(optionBg)
                        .border(1.dp, optionBorder, RoundedCornerShape(8.dp))
                        .clickable { onOptionSelected(index) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onOptionSelected(index) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = if (isSubmitted && isCorrect) CyberGreen else CyberCyan
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = optionText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    if (isSubmitted && isCorrect) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Correct",
                            tint = CyberGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (isSubmitted) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "💡 Explication : ${question.explanation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberCyan
                )
            }
        }
    }
}

private fun String?.isNull_or_blank(): Boolean {
    return this == null || this.isBlank()
}
