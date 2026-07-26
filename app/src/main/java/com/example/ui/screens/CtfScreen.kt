package com.example.ui.screens

import android.util.Base64
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.ui.components.verticalScrollbar
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CyberDataRepository
import com.example.data.UserProgressEntity
import com.example.model.CTFChallenge
import com.example.model.CTFSubmissionResult
import com.example.model.CTFSubmissionStatus
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.viewmodel.CyberViewModel

@Composable
fun CtfScreen(
    viewModel: CyberViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProgress by viewModel.userProgressState.collectAsState()
    val ctfFeedback by viewModel.ctfFeedback.collectAsState()
    val ctfSubmissions by viewModel.ctfSubmissions.collectAsState()

    val progress = userProgress ?: UserProgressEntity()
    val capturedSet = progress.capturedFlags.split(",").filter { it.isNotBlank() }.toSet()

    var selectedCategory by remember { mutableStateOf("Tous") }
    var selectedDifficulty by remember { mutableStateOf("Tous") }

    val categories = listOf("Tous", "Reconnaissance", "Web Vulnerabilities", "Cryptography", "Forensics")
    val difficulties = listOf("Tous", "Facile", "Moyen", "Difficile")

    val ctfList = CyberDataRepository.ctfChallenges.filter { ctf ->
        (selectedCategory == "Tous" || ctf.category.equals(selectedCategory, ignoreCase = true)) &&
        (selectedDifficulty == "Tous" || ctf.difficulty.equals(selectedDifficulty, ignoreCase = true))
    }

    val badgeDrawableId = context.resources.getIdentifier(
        "ctf_badge_hero_1785013433686", "drawable", context.packageName
    )

    val ctfListState = rememberLazyListState()

    LazyColumn(
        state = ctfListState,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScrollbar(ctfListState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero CTF Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ctf_hero_card"),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (badgeDrawableId != 0) {
                        Image(
                            painter = painterResource(id = badgeDrawableId),
                            contentDescription = "Badge CTF",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = CyberGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CAPTURE THE FLAG (CTF)",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberGreen,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "Défis Pratiques Cyber",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Drapeaux capturés : ${capturedSet.size} / ${CyberDataRepository.ctfChallenges.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Feedback Banner
        if (ctfFeedback != null) {
            item {
                val (msg, isSuccess) = ctfFeedback!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSuccess) CyberGreen.copy(alpha = 0.2f) else CyberRed.copy(alpha = 0.2f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSuccess) CyberGreen else CyberRed)
                ) {
                    Text(
                        text = msg,
                        modifier = Modifier.padding(14.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Category & Difficulty Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Catégories :",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberGreen,
                                selectedLabelColor = Color.Black,
                                containerColor = CyberSurfaceVariant,
                                labelColor = Color.White
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Niveau :",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    difficulties.forEach { diff ->
                        FilterChip(
                            selected = selectedDifficulty == diff,
                            onClick = { selectedDifficulty = diff },
                            label = { Text(diff, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan,
                                selectedLabelColor = Color.Black,
                                containerColor = CyberSurfaceVariant,
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // CTF Cards
        items(ctfList) { ctf ->
            val isCaptured = capturedSet.contains(ctf.id)
            val subResult = ctfSubmissions[ctf.id]

            CtfChallengeCard(
                challenge = ctf,
                isCaptured = isCaptured,
                submissionResult = subResult,
                onSubmitFlag = { inputFlag ->
                    viewModel.verifyAndSubmitCtfFlag(ctf, inputFlag)
                }
            )
        }

        // Quick CTF Flag Decoder Tool
        item {
            CtfToolDecoderCard()
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CtfChallengeCard(
    challenge: CTFChallenge,
    isCaptured: Boolean,
    submissionResult: CTFSubmissionResult?,
    onSubmitFlag: (String) -> Unit
) {
    var flagInput by remember { mutableStateOf("") }
    var showHint by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }

    val isVerifying = submissionResult?.status == CTFSubmissionStatus.VERIFYING

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ctf_card_${challenge.id}"),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCaptured) CyberGreen else CyberSurfaceBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isCaptured) CyberGreen.copy(alpha = 0.2f) else CyberSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCaptured) Icons.Default.CheckCircle else Icons.Default.Flag,
                            contentDescription = null,
                            tint = if (isCaptured) CyberGreen else CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = challenge.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${challenge.category} • ${challenge.difficulty}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberAmber.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "+${challenge.points} XP",
                        color = CyberAmber,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )

            if (!challenge.targetAddress.isNull_or_blank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🎯 Cible Backend : ",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = challenge.targetAddress!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberGreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!isCaptured) {
                // Flag Input section with Backend Validation
                Text(
                    text = "Saisissez le Flag (Format: ${challenge.flagFormat}) :",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = flagInput,
                        onValueChange = { flagInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("flag_input_${challenge.id}"),
                        placeholder = { Text("FLAG{...}", color = Color.Gray, fontSize = 13.sp) },
                        trailingIcon = {
                            if (flagInput.isNotEmpty()) {
                                IconButton(onClick = { flagInput = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Effacer",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberGreen,
                            unfocusedBorderColor = CyberSurfaceBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onSubmitFlag(flagInput) },
                        enabled = !isVerifying,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberGreen,
                            disabledContainerColor = CyberSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("submit_flag_button_${challenge.id}")
                    ) {
                        if (isVerifying) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = CyberGreen,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Vérifier", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Submission Status Banner for this specific card
                if (submissionResult != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val statusColor = when (submissionResult.status) {
                        CTFSubmissionStatus.SUCCESS -> CyberGreen
                        CTFSubmissionStatus.WRONG_FLAG -> CyberRed
                        CTFSubmissionStatus.VERIFYING -> CyberAmber
                        CTFSubmissionStatus.INVALID_FORMAT -> CyberAmber
                        CTFSubmissionStatus.ALREADY_CAPTURED -> CyberCyan
                        CTFSubmissionStatus.IDLE -> Color.Gray
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .border(1.dp, statusColor, RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (submissionResult.status == CTFSubmissionStatus.VERIFYING) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = statusColor,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = submissionResult.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = statusColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions row: Details & Hint toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Hint Toggle
                    Row(
                        modifier = Modifier
                            .clickable { showHint = !showHint }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Indice",
                            tint = CyberAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showHint) "Masquer l'indice" else "Afficher un indice",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Details Toggle
                    Row(
                        modifier = Modifier
                            .clickable { showDetails = !showDetails }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Consignes",
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showDetails) "Masquer les consignes" else "Consignes & Méthode",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Hint Expandable
                AnimatedVisibility(
                    visible = showHint,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberAmber.copy(alpha = 0.1f))
                                .border(1.dp, CyberAmber.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "💡 Indice Serveur : ${challenge.hint}",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyberAmber
                            )
                        }
                    }
                }

                // Details / Instructions Expandable
                AnimatedVisibility(
                    visible = showDetails,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberSurfaceVariant)
                                .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "📋 Consignes du Défi :",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = challenge.instructions ?: "1. Ouvrez le module ou laboratoire associé.\n2. Exécutez l'analyse réseau ou le scan de vulnérabilités.\n3. Extrayez le flag commençant par FLAG{...}.\n4. Soumettez la clé ci-dessus pour validation backend.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

            } else {
                // Captured Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberGreen.copy(alpha = 0.15f))
                        .border(1.dp, CyberGreen, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CyberGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✓ DRAPEAU CAPTURÉ ET VALIDÉ (${challenge.flag})",
                            color = CyberGreen,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CtfToolDecoderCard() {
    var inputText by remember { mutableStateOf("") }
    var decodedResult by remember { mutableStateOf("") }
    var currentMode by remember { mutableStateOf("Base64") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "Outils CTF",
                    tint = CyberCyan,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🛠️ Décodeur & Boîte à Outils CTF",
                    style = MaterialTheme.typography.titleSmall,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Base64", "ROT13", "Reverse").forEach { mode ->
                    FilterChip(
                        selected = currentMode == mode,
                        onClick = { currentMode = mode },
                        label = { Text(mode, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = CyberSurfaceVariant,
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = {
                    inputText = it
                    decodedResult = when (currentMode) {
                        "Base64" -> try {
                            String(Base64.decode(it.trim(), Base64.DEFAULT), Charsets.UTF_8)
                        } catch (e: Exception) {
                            "Entrée Base64 Invalide"
                        }
                        "ROT13" -> it.map { char ->
                            when (char) {
                                in 'a'..'z' -> ((char - 'a' + 13) % 26 + 'a'.code).toChar()
                                in 'A'..'Z' -> ((char - 'A' + 13) % 26 + 'A'.code).toChar()
                                else -> char
                            }
                        }.joinToString("")
                        "Reverse" -> it.reversed()
                        else -> it
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Collez une chaîne à décoder (ex: RkxBR3tjeWJlcl9zaGllbGRfMjAyNn0=)...", color = Color.Gray, fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CyberSurfaceBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            if (decodedResult.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberCyan.copy(alpha = 0.15f))
                        .border(1.dp, CyberCyan, RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Résultat du décodeur ($currentMode) :",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan
                        )
                        Text(
                            text = decodedResult,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun String?.isNull_or_blank(): Boolean {
    return this == null || this.isBlank()
}
