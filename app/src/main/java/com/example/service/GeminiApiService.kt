package com.example.service

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiCyberMentorService {

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun askCyberMentor(userQuery: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineSmartResponse(userQuery)
        }

        try {
            val systemInstruction = "Tu es HackGuard CyberMentor, un expert pédagogique en cybersécurité et piratage éthique. Tu réponds en français clair, structuré, adapté aux débutants avec des puces et des exemples de code si utile. Rappelle toujours l'éthique et la légalité."

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", userQuery))
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemInstruction))
                    })
                })
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseText = response.body?.string() ?: ""

            if (response.isSuccessful && responseText.isNotBlank()) {
                val jsonResponse = JSONObject(responseText)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "Pas de réponse.")
                    }
                }
            }
            return@withContext getOfflineSmartResponse(userQuery)
        } catch (e: Exception) {
            return@withContext getOfflineSmartResponse(userQuery)
        }
    }

    private fun getOfflineSmartResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("sql") || q.contains("injection") -> """
                🛡️ **Explication de l'Injection SQL (SQLi) :**
                Une injection SQL se produit lorsqu'une application prend du texte saisi par l'utilisateur et le concatène directement dans une requête SQL sans précaution.

                **Exemple de faille :**
                `SELECT * FROM users WHERE user = 'INPUT' AND pass = 'INPUT'`

                **Solution de protection (Requêtes Préparées) :**
                Utilisez toujours des *Prepared Statements* (ex: `PreparedStatement` en Java/Kotlin ou PDO en PHP) qui séparent le code SQL des données utilisateur.
            """.trimIndent()

            q.contains("xss") || q.contains("script") -> """
                ⚠️ **Explication du Cross-Site Scripting (XSS) :**
                Le XSS consiste à injecter du code JavaScript malveillant dans un site web pour qu'il s'exécute dans le navigateur d'autres utilisateurs.

                **Prévention :**
                - Échapper/Assainir toutes les entrées HTML (HTML Entity Encoding).
                - Mettre en place un en-tête HTTP `Content-Security-Policy` (CSP).
                - Utiliser les attributs `HttpOnly` pour protéger les cookies de session.
            """.trimIndent()

            q.contains("nmap") || q.contains("scan") || q.contains("port") -> """
                🔍 **Comprendre le Scan de Ports avec Nmap :**
                Nmap permet de découvrir les machines hôtes sur un réseau et d'identifier les services ouverts (ex: HTTP sur port 80, SSH sur port 22).

                **Commandes clés Nmap :**
                - `nmap -sS 192.168.1.1` : Scan SYN furtif
                - `nmap -sV 192.168.1.1` : Détection des versions de services
                - `nmap -O 192.168.1.1` : Détection du système d'exploitation
            """.trimIndent()

            q.contains("ctf") || q.contains("indice") || q.contains("flag") -> """
                🎯 **Conseils pour réussir les défis CTF :**
                1. **Analyse toujours la consigne** et cherche les détails dans la description.
                2. Dans le **Terminal Virtuel**, teste les commandes suggérées (`nmap`, `whois`, `base64`).
                3. Pour l'injection SQL, n'oublie pas le payload classique `' OR '1'='1`.
                4. Pour déchiffrer le Base64 : `echo STRING | base64 -d`.
            """.trimIndent()

            else -> """
                🤖 **Bienvenue sur HackGuard CyberMentor !**
                Je suis votre assistant spécialisé en cybersécurité et piratage éthique.

                Vous pouvez me poser des questions sur :
                - Les failles Web (**Injections SQL, XSS, CSRF**)
                - L'analyse réseau (**Nmap, Wireshark, Modèle OSI**)
                - La cryptographie (**Hachage, MD5, SHA-256, Salage, Chiffrement AES/RSA**)
                - La cyberdéfense (**Pare-feu UFW, Hardening Linux, Mots de passe**)
            """.trimIndent()
        }
    }
}
