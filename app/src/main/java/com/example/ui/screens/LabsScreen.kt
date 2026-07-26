package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.ui.components.verticalScrollbar
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.UserProgressEntity
import com.example.model.LabSimulation
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTerminalBg
import com.example.viewmodel.CyberViewModel
import java.security.MessageDigest

@Composable
fun LabsScreen(
    viewModel: CyberViewModel,
    modifier: Modifier = Modifier
) {
    val activeLabId by viewModel.activeLabId.collectAsState()
    val userProgress by viewModel.userProgressState.collectAsState()
    val progress = userProgress ?: UserProgressEntity()

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (activeLabId) {
            "terminal" -> TerminalLabScreen(viewModel, onBack = { viewModel.openLab(null) })
            "sqli" -> SqlInjectionLabScreen(viewModel, onBack = { viewModel.openLab(null) })
            "xss" -> XssLabScreen(viewModel, onBack = { viewModel.openLab(null) })
            "bruteforce" -> HashCrackerLabScreen(viewModel, onBack = { viewModel.openLab(null) })
            "phishing" -> PhishingInspectorLabScreen(viewModel, onBack = { viewModel.openLab(null) })
            else -> LabsListScreen(
                labs = CyberDataRepository.labSimulations,
                userProgress = progress,
                onLabClick = { viewModel.openLab(it.id) }
            )
        }
    }
}

@Composable
fun LabsListScreen(
    labs: List<LabSimulation>,
    userProgress: UserProgressEntity,
    onLabClick: (LabSimulation) -> Unit
) {
    val completedSet = userProgress.completedLabs.split(",").toSet()
    val labsListState = rememberLazyListState()

    LazyColumn(
        state = labsListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScrollbar(labsListState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "SIMULATION & LABS INTERACTIFS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CyberGreen,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mettez en pratique vos connaissances dans des environnements virtuels sécurisés sans risque.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
            }
        }

        items(labs) { lab ->
            val isCompleted = completedSet.contains(lab.id)

            val iconVector = when (lab.id) {
                "terminal" -> Icons.Default.Terminal
                "sqli" -> Icons.Default.Code
                "xss" -> Icons.Default.BugReport
                "bruteforce" -> Icons.Default.Key
                "phishing" -> Icons.Default.Email
                else -> Icons.Default.Shield
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLabClick(lab) }
                    .testTag("lab_card_${lab.id}"),
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
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isCompleted) CyberGreen.copy(alpha = 0.2f) else CyberSurfaceVariant)
                            .border(1.dp, if (isCompleted) CyberGreen else CyberCyan, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = if (isCompleted) CyberGreen else CyberCyan,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = lab.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Validé",
                                    tint = CyberGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${lab.category} • ${lab.difficulty}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lab.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

// 1. TERMINAL SIMULATOR LAB
@Composable
fun TerminalLabScreen(
    viewModel: CyberViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var inputCommand by remember { mutableStateOf("") }
    val terminalLogs = remember {
        mutableStateListOf(
            "HackGuard Cyber Terminal v2.4 (Kali-Linux Simulated)",
            "Tapez 'help' pour voir la liste des commandes disponibles.",
            "Cible réseau active : 192.168.1.105 (target-corp.org)",
            "--------------------------------------------------"
        )
    }

    fun executeCmd(cmd: String) {
        if (cmd.isBlank()) return
        val cleanCmd = cmd.trim()
        terminalLogs.add("user@hackguard:~$ $cleanCmd")

        when {
            cleanCmd.startsWith("help", ignoreCase = true) -> {
                terminalLogs.add("Commandes disponibles :")
                terminalLogs.add("  nmap -sV 192.168.1.105   : Scanner les ports ouverts de la cible")
                terminalLogs.add("  whois target-corp.org   : Consulter la fiche de domaine")
                terminalLogs.add("  ping -c 3 192.168.1.105 : Vérifier la connectivité ICMP")
                terminalLogs.add("  sqlmap -u target.org    : Tester la vulnérabilité SQL")
                terminalLogs.add("  wireshark -r capture.pcap : Analyser la capture réseau")
                terminalLogs.add("  hydra -l admin -P word.txt ssh : Test d'attaque par dictionnaire SSH")
                terminalLogs.add("  echo STRING | base64 -d : Décoder une chaîne Base64")
                terminalLogs.add("  clear                    : Effacer l'écran")
            }
            cleanCmd.startsWith("clear", ignoreCase = true) -> {
                terminalLogs.clear()
            }
            cleanCmd.contains("nmap", ignoreCase = true) -> {
                terminalLogs.add("Démarrage de Nmap 7.94 ( https://nmap.org )")
                terminalLogs.add("Scan de l'hôte 192.168.1.105 [1000 ports]...")
                terminalLogs.add("Hôte actif (0.0042s latence).")
                terminalLogs.add("PORT     STATE SERVICE VERSION")
                terminalLogs.add("22/tcp   open  ssh     OpenSSH 8.9p1")
                terminalLogs.add("80/tcp   open  http    Apache httpd 2.4.52")
                terminalLogs.add("443/tcp  open  ssl/http Nginx 1.18.0")
                terminalLogs.add("8080/tcp open  http-alt Jetty 9.4.z (ADMIN PORT DECOUVERT!)")
                terminalLogs.add("Nmap terminé : 1 adresse IP scannée (1 hôte actif).")
            }
            cleanCmd.contains("whois", ignoreCase = true) -> {
                terminalLogs.add("Domain Name: TARGET-CORP.ORG")
                terminalLogs.add("Registry Domain ID: D108293-LROR")
                terminalLogs.add("Registrar: CyberShield Registrar LLC")
                terminalLogs.add("Name Server: NS1.TARGET-CORP.ORG (192.168.1.105)")
                terminalLogs.add("Admin Email: admin-tech@target-corp.org")
                terminalLogs.add("Created Date: 2021-04-12T10:00:00Z")
            }
            cleanCmd.contains("ping", ignoreCase = true) -> {
                terminalLogs.add("PING 192.168.1.105 (192.168.1.105) 56(84) octets de données.")
                terminalLogs.add("64 octets de 192.168.1.105 : icmp_seq=1 ttl=64 temps=2.12 ms")
                terminalLogs.add("64 octets de 192.168.1.105 : icmp_seq=2 ttl=64 temps=1.98 ms")
                terminalLogs.add("64 octets de 192.168.1.105 : icmp_seq=3 ttl=64 temps=2.05 ms")
                terminalLogs.add("--- 192.168.1.105 statistiques ping ---")
                terminalLogs.add("3 paquets transmis, 3 reçus, 0% perte de paquets.")
            }
            cleanCmd.contains("base64", ignoreCase = true) -> {
                terminalLogs.add("Chaîne décodée : FLAG{cyber_shield_2026}")
            }
            cleanCmd.contains("wireshark", ignoreCase = true) -> {
                terminalLogs.add("Analyse du fichier capture.pcap...")
                terminalLogs.add("[Frame 42] HTTP POST /login.php")
                terminalLogs.add("  User-Agent: Mozilla/5.0")
                terminalLogs.add("  Form Data: username=agent_007&password=FLAG{http_cleartext_pass_p3nt3st}")
            }
            else -> {
                terminalLogs.add("Commande '$cleanCmd' exécutée dans l'environnement virtuel.")
            }
        }
    }

    val terminalScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(terminalScrollState)
            .verticalScrollbar(terminalScrollState)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = CyberGreen
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Terminal Virtuel Kali Linux",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Command Bar Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val quickCmds = listOf("help", "nmap", "whois", "ping", "wireshark", "clear")
            quickCmds.forEach { cmd ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberSurfaceVariant)
                        .border(1.dp, CyberGreen.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .clickable {
                            if (cmd == "nmap") executeCmd("nmap -sV 192.168.1.105")
                            else if (cmd == "ping") executeCmd("ping -c 3 192.168.1.105")
                            else if (cmd == "whois") executeCmd("whois target-corp.org")
                            else if (cmd == "wireshark") executeCmd("wireshark -r capture.pcap")
                            else executeCmd(cmd)
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cmd,
                        color = CyberGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Terminal Log Window
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 220.dp, max = 300.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CyberTerminalBg)
                .border(1.dp, CyberGreen, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                terminalLogs.forEach { log ->
                    Text(
                        text = log,
                        color = if (log.contains("DECOUVERT") || log.contains("FLAG")) CyberAmber else CyberGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input Line
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputCommand,
                onValueChange = { inputCommand = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("terminal_input"),
                placeholder = { Text("Entrez une commande (ex: nmap, help)", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberGreen,
                    unfocusedBorderColor = CyberSurfaceBorder,
                    focusedTextColor = CyberGreen,
                    unfocusedTextColor = CyberGreen
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    executeCmd(inputCommand)
                    inputCommand = ""
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberGreen)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Exécuter",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                viewModel.completeLab("terminal")
                Toast.makeText(context, "Lab Terminal Validé ! +100 XP", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Valider ce Lab (+100 XP)", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

// 2. SQL INJECTION LAB
@Composable
fun SqlInjectionLabScreen(
    viewModel: CyberViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var usernameInput by remember { mutableStateOf("admin") }
    var passwordInput by remember { mutableStateOf("secret") }
    var isSecureMode by remember { mutableStateOf(false) }
    var queryResultText by remember { mutableStateOf("") }
    var queryResultRows by remember { mutableStateOf<List<String>>(emptyList()) }

    fun runQuery() {
        if (!isSecureMode) {
            val constructedQuery = "SELECT * FROM users WHERE username = '$usernameInput' AND password = '$passwordInput';"
            queryResultText = constructedQuery

            if (usernameInput.contains("' OR '1'='1") || usernameInput.contains("' OR 1=1")) {
                queryResultRows = listOf(
                    "ID: 1 | User: admin | Role: Administrator | Hash: 5ebe2294ecd...",
                    "ID: 2 | User: agent_007 | Role: Analyst | Hash: 81dc9bdb52d...",
                    "⚠️ ATTENTION : L'injection SQL a annulé la clause WHERE. Accès Administrateur accordé !"
                )
            } else if (usernameInput == "admin" && passwordInput == "secret") {
                queryResultRows = listOf("ID: 1 | User: admin | Role: Administrator | Auth: OK")
            } else {
                queryResultRows = listOf("0 lignes retournées. Identifiants incorrects.")
            }
        } else {
            val secureCode = "SELECT * FROM users WHERE username = ? AND password = ?;"
            queryResultText = "$secureCode (Mode Requête Préparée - PreparedStatement)"

            if (usernameInput == "admin" && passwordInput == "secret") {
                queryResultRows = listOf("ID: 1 | User: admin | Role: Administrator | Auth: OK")
            } else {
                queryResultRows = listOf("0 lignes retournées. Attaque par injection neutralisée par la requête préparée !")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = CyberGreen
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Playground Injection SQL",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Vulnerable vs Secure Mode Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isSecureMode) "🛡️ Mode Sécurisé (Requête Préparée)" else "⚠️ Mode Vulnérable (Concaténation)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSecureMode) CyberGreen else CyberRed
                    )
                    Text(
                        text = if (isSecureMode) "Les variables sont traitées comme de simples chaînes sans interprétation SQL." else "Le texte saisi par l'utilisateur est directement interprété comme du code SQL.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
                Switch(
                    checked = isSecureMode,
                    onCheckedChange = { isSecureMode = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyberGreen,
                        checkedTrackColor = CyberGreen.copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Payload Buttons
        Text("PAYLOADS INJECTION TEST :", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { usernameInput = "' OR '1'='1"; passwordInput = "anything" },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
            ) {
                Text("' OR '1'='1", color = CyberAmber, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            }
            Button(
                onClick = { usernameInput = "admin'--"; passwordInput = "" },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
            ) {
                Text("admin'--", color = CyberAmber, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Inputs
        OutlinedTextField(
            value = usernameInput,
            onValueChange = { usernameInput = it },
            label = { Text("Nom d'utilisateur (Username)") },
            modifier = Modifier.fillMaxWidth().testTag("sqli_user_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberGreen,
                unfocusedBorderColor = CyberSurfaceBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text("Mot de passe (Password)") },
            modifier = Modifier.fillMaxWidth().testTag("sqli_pass_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberGreen,
                unfocusedBorderColor = CyberSurfaceBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { runQuery() },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
        ) {
            Text("Exécuter la Requête SQL", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SQL Query Output & Result Box
        if (queryResultText.isNotBlank()) {
            Text("REQUÊTE GENERÉE :", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberTerminalBg)
                    .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(queryResultText, color = CyberGreen, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("RÉSULTAT BASE DE DONNÉES :", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberTerminalBg)
                    .border(1.dp, if (queryResultRows.any { it.contains("ATTENTION") }) CyberRed else CyberGreen, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    queryResultRows.forEach { row ->
                        Text(
                            text = row,
                            color = if (row.contains("ATTENTION")) CyberRed else Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.completeLab("sqli")
                Toast.makeText(context, "Lab SQLi Validé ! +100 XP", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
        ) {
            Text("Valider ce Lab (+100 XP)", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

// 3. XSS LAB
@Composable
fun XssLabScreen(
    viewModel: CyberViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var commentInput by remember { mutableStateOf("<script>alert('XSS-FOUND')</script>") }
    var isSanitized by remember { mutableStateOf(false) }
    var triggerAlertBanner by remember { mutableStateOf(false) }

    fun postComment() {
        if (!isSanitized && commentInput.contains("<script>", ignoreCase = true)) {
            triggerAlertBanner = true
        } else {
            triggerAlertBanner = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = CyberGreen
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Lab Cross-Site Scripting (XSS)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sanitization Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isSanitized) "🛡️ Nettoyage HTML Actif (Sanitized)" else "⚠️ Rendu HTML Brut Vulnérable",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSanitized) CyberGreen else CyberRed
                    )
                    Text(
                        text = if (isSanitized) "Les balises HTML/Script sont converties en entités texte (&lt;script&gt;)." else "Le code JavaScript injecté est exécuté directement par le navigateur.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
                Switch(
                    checked = isSanitized,
                    onCheckedChange = {
                        isSanitized = it
                        triggerAlertBanner = false
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyberGreen)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = commentInput,
            onValueChange = { commentInput = it },
            label = { Text("Écrire un commentaire sur le blog") },
            modifier = Modifier.fillMaxWidth().testTag("xss_comment_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberGreen,
                unfocusedBorderColor = CyberSurfaceBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { postComment() },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
        ) {
            Text("Publier le Commentaire", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (triggerAlertBanner) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberRed.copy(alpha = 0.2f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberRed)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚨 ALERTE JAVASCRIPT XSS DÉCLENCHÉE !",
                        style = MaterialTheme.typography.titleMedium,
                        color = CyberRed,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Le script injecté a été exécuté. Dans un scénario réel, l'attaquant aurait dérobé le cookie de session : document.cookie = 'session_id=7a8b9c...'",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text("RENDU DU COMMENTAIRE DU BLOG :", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberTerminalBg)
                .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            if (isSanitized) {
                Text(
                    text = commentInput.replace("<", "&lt;").replace(">", "&gt;"),
                    color = CyberGreen,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                Text(
                    text = commentInput,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.completeLab("xss")
                Toast.makeText(context, "Lab XSS Validé ! +100 XP", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
        ) {
            Text("Valider ce Lab (+100 XP)", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

// 4. HASH CRACKER LAB
@Composable
fun HashCrackerLabScreen(
    viewModel: CyberViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var inputPass by remember { mutableStateOf("secret123") }
    var useSalt by remember { mutableStateOf(false) }
    var isCracking by remember { mutableStateOf(false) }
    var crackProgress by remember { mutableStateOf(0f) }
    var crackResultText by remember { mutableStateOf("") }

    val salt = "HackGuardSalt_99"
    val passToHash = if (useSalt) "$inputPass$salt" else inputPass

    val md5Hash = remember(passToHash) { hashString("MD5", passToHash) }
    val sha256Hash = remember(passToHash) { hashString("SHA-256", passToHash) }

    fun runCracker() {
        isCracking = true
        crackProgress = 0.5f
        if (!useSalt) {
            crackResultText = "✅ SUCCÈS : Hash MD5 trouvé dans le dictionnaire en 0.042s ! Mot de passe d'origine : '$inputPass'"
        } else {
            crackResultText = "❌ ÉCHEC DU CRACKING : Le Sel (Salt) ajouté au hash a rendu l'attaque par dictionnaire impossible !"
        }
        isCracking = false
        crackProgress = 1f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = CyberGreen
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Lab Hachage & Bruteforce",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = inputPass,
            onValueChange = { inputPass = it },
            label = { Text("Mot de passe à hacher") },
            modifier = Modifier.fillMaxWidth().testTag("hash_pass_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberGreen,
                unfocusedBorderColor = CyberSurfaceBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = useSalt,
                onCheckedChange = { useSalt = it },
                colors = CheckboxDefaults.colors(checkedColor = CyberGreen)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Ajouter un Sel (Salt) aléatoire unique",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("MD5 Hash :", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(md5Hash, color = CyberGreen, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("SHA-256 Hash :", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(sha256Hash, color = CyberGreen, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { runCracker() },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberAmber)
        ) {
            Text("Simuler Attaque par Dictionnaire (John/Hashcat)", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        if (crackResultText.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberTerminalBg)
                    .border(1.dp, if (crackResultText.contains("SUCCÈS")) CyberAmber else CyberGreen, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = crackResultText,
                    color = if (crackResultText.contains("SUCCÈS")) CyberAmber else CyberGreen,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.completeLab("bruteforce")
                Toast.makeText(context, "Lab Hachage Validé ! +100 XP", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
        ) {
            Text("Valider ce Lab (+100 XP)", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

// 5. PHISHING INSPECTOR LAB
@Composable
fun PhishingInspectorLabScreen(
    viewModel: CyberViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedEmailIndex by remember { mutableStateOf(0) }
    var showHeaders by remember { mutableStateOf(false) }
    var evaluationFeedback by remember { mutableStateOf("") }

    val emails = listOf(
        PhishingEmail(
            subject = "URGENT : Suspension immédiate de votre compte bancaire",
            senderName = "Service Sécurité Banque",
            senderEmail = "no-reply@banque-securite-login9.com",
            body = "Cher client, une activité suspecte a été détectée. Veuillez cliquer sur le lien ci-dessous pour vérifier votre identité sous 2 heures, sinon votre compte sera bloqué.",
            headers = "From: Banque Security <no-reply@banque-securite-login9.com>\nReturn-Path: <spoofed@185.220.101.5>\nReceived: from attacker-vps.org (185.220.101.5)\nDKIM-Signature: FAIL",
            isPhishing = true,
            explanation = "PHISHING CONFIRMÉ : Le domaine expéditeur est trompeur, l'IP réelle appartient à un serveur malveillant et la signature DKIM a échoué."
        ),
        PhishingEmail(
            subject = "Reçu de votre commande #49281",
            senderName = "Service Clients officiel",
            senderEmail = "commandes@boutique-officielle.fr",
            body = "Bonjour, nous vous confirmons la bonne réception de votre commande #49281. Vous pouvez suivre votre colis sur notre portail sécurisé.",
            headers = "From: boutique-officielle.fr <commandes@boutique-officielle.fr>\nReturn-Path: <commandes@boutique-officielle.fr>\nReceived: from mail.boutique-officielle.fr\nDKIM-Signature: PASS",
            isPhishing = false,
            explanation = "EMAIL LÉGITIME : Domaine expéditeur officiel valide, en-tête SPF/DKIM valide et pas d'urgence suspecte."
        )
    )

    val currentEmail = emails[selectedEmailIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = CyberGreen
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Inspecteur de Phishing",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            emails.forEachIndexed { idx, _ ->
                Button(
                    onClick = {
                        selectedEmailIndex = idx
                        evaluationFeedback = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedEmailIndex == idx) CyberGreen else CyberSurfaceVariant
                    )
                ) {
                    Text(
                        text = "Email #${idx + 1}",
                        color = if (selectedEmailIndex == idx) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "De : ${currentEmail.senderName} <${currentEmail.senderEmail}>", color = CyberCyan, fontWeight = FontWeight.Bold)
                Text(text = "Sujet : ${currentEmail.subject}", color = Color.White, fontWeight = FontWeight.Bold)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CyberSurfaceBorder)
                Text(text = currentEmail.body, color = Color.LightGray)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = showHeaders,
                onCheckedChange = { showHeaders = it },
                colors = CheckboxDefaults.colors(checkedColor = CyberGreen)
            )
            Text("Inspecter les en-têtes techniques (Headers)", color = Color.White)
        }

        if (showHeaders) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberTerminalBg)
                    .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(currentEmail.headers, color = CyberGreen, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    evaluationFeedback = if (!currentEmail.isPhishing) "✅ BONNE RÉPONSE ! Cet e-mail est Légitime." else "❌ ERREUR ! C'était un Phishing."
                },
                modifier = Modifier.weight(1f).height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
            ) {
                Text("LÉGITIME", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    evaluationFeedback = if (currentEmail.isPhishing) "✅ BONNE RÉPONSE ! C'est un Phishing." else "❌ ERREUR ! Cet email était légitime."
                },
                modifier = Modifier.weight(1f).height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberRed)
            ) {
                Text("PHISHING", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        if (evaluationFeedback.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(evaluationFeedback, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(currentEmail.explanation, style = MaterialTheme.typography.bodySmall, color = CyberCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.completeLab("phishing")
                Toast.makeText(context, "Lab Phishing Validé ! +100 XP", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
        ) {
            Text("Valider ce Lab (+100 XP)", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

data class PhishingEmail(
    val subject: String,
    val senderName: String,
    val senderEmail: String,
    val body: String,
    val headers: String,
    val isPhishing: Boolean,
    val explanation: String
)

private fun hashString(type: String, input: String): String {
    val bytes = MessageDigest.getInstance(type).digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
