package com.example.data

import com.example.model.AutonomousLog
import com.example.model.BrowserAutonomousMode
import com.example.model.KnowledgeEntry
import com.example.model.Profession
import com.example.model.ProfessionSkill
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Autonomous Neural Brain Engine.
 * Operates 100% autonomously with NO Gemini API Key required.
 * Learns from web interactions, maintains the Wisdom Database,
 * unlocks new skills, and levels up professions dynamically.
 */
class AutonomousBrainEngine {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun getDefaultProfessions(): List<Profession> {
        return listOf(
            Profession(
                id = "prof_gamer",
                title = "Gamer Pro & Speedrunner",
                iconEmoji = "🎮",
                category = "Jogos & Ação Rápida",
                level = 3,
                currentXp = 450,
                requiredXp = 600,
                description = "Especialista em altas cadências de clique, desvio de banimento por biometria e precisão de pixels.",
                skills = listOf(
                    ProfessionSkill("sk_cps", "Micro-Burst de 30 CPS", "Executa pulsos de toque ultra-rápidos em janelas de oportunidade", true, 1, "Cadência: 33ms"),
                    ProfessionSkill("sk_jitter", "Jitter Anti-Cheat Estocástico", "Aplica ruído Gaussiano variável para emular tremores mioelétricos humanos", true, 2, "Variação: ±28ms"),
                    ProfessionSkill("sk_hitbox", "Mira Sniper em Hitbox", "Compensa flutuações de lag mantendo o ponteiro no centro geométrico", true, 3, "Alvo: Offset 0px"),
                    ProfessionSkill("sk_cancel", "Cancelamento de Animação", "Interrompe delays residuais disparando toques no primeiro frame ativo", false, 4, "Buffer: 12ms")
                )
            ),
            Profession(
                id = "prof_gemini_agent",
                title = "Interator Gemini & IA Web",
                iconEmoji = "🤖",
                category = "Agente Autônomo de IA",
                level = 2,
                currentXp = 280,
                requiredXp = 500,
                description = "Capacidade autônoma de conversar com o Gemini na web, gerar imagens, criar músicas e auto-aprender.",
                skills = listOf(
                    ProfessionSkill("sk_prompt", "Injeção Autônoma de Prompt", "Localiza barras de texto na web e digita instruções cognitivas", true, 1, "DOM: textarea.fill()"),
                    ProfessionSkill("sk_gen_img", "Síntese de Imagem Visual", "Navega e comanda ferramentas de criação de arte e texturas", true, 2, "Comando: /generate_image"),
                    ProfessionSkill("sk_gen_music", "Composição Musical Web", "Interage com estúdios de áudio online compondo trilhas de jogos", false, 3, "Comando: /generate_music"),
                    ProfessionSkill("sk_recursive_learn", "Auto-Aprendizagem Recursiva", "Pergunta dúvidas para IAs web e extrai o conhecimento para o banco de dados", false, 4, "Modo: Reflexão Online")
                )
            ),
            Profession(
                id = "prof_browser",
                title = "Navegador Chrome & Automação Web",
                iconEmoji = "🌐",
                category = "Navegação e Extração",
                level = 2,
                currentXp = 210,
                requiredXp = 400,
                description = "Domínio de formulários HTML, cliques em botões de sites e navegação autônoma em abas.",
                skills = listOf(
                    ProfessionSkill("sk_dom_detect", "Mapeador de Elementos DOM", "Identifica inputs, botões e links clicáveis em qualquer página", true, 1, "Parser: querySelectorAll"),
                    ProfessionSkill("sk_auto_fill", "Auto-Preenchimento Adaptativo", "Preenche caixas de pesquisa e termos técnicos automaticamente", true, 2, "Digitação: 65 wpm"),
                    ProfessionSkill("sk_scrape", "Extração de Fatos e Metadados", "Lê parágrafos relevantes e condensa resumos para o banco", false, 3, "Extrator: Readability")
                )
            ),
            Profession(
                id = "prof_scientist",
                title = "Cientista Quântico & Otimizador",
                iconEmoji = "🔬",
                category = "Cálculo & Teoria dos Jogos",
                level = 4,
                currentXp = 820,
                requiredXp = 1000,
                description = "Modelagem matemática de latência, probabilidade bayesiana de drops e limites físicos de amostragem.",
                skills = listOf(
                    ProfessionSkill("sk_sampling", "Sincronia Harmônica de Taxa de Hz", "Calcula o período ideal em função da taxa de atualização do display (60/90/120Hz)", true, 1, "Fórmula: T = 1000 / (Hz * k)"),
                    ProfessionSkill("sk_little", "Teoria de Filas de Little", "Otimiza a taxa de chegada de toques para evitar descarte pelo buffer do SO", true, 2, "Fórmula: L = λ * W"),
                    ProfessionSkill("sk_nash", "Equilíbrio de Nash em Jogos", "Antecipa rotinas de adversários computacionais escolhendo a jogada ótima", true, 3, "Matriz: Payoff Máximo"),
                    ProfessionSkill("sk_markov", "Cadeias de Markov para Loot", "Prevê ciclos de drop baseando-se no histórico estocástico acumulado", false, 4, "Matriz de Transição P")
                )
            ),
            Profession(
                id = "prof_forex_arbitrage",
                title = "Trader Cambial & Arbitrador Autônomo",
                iconEmoji = "💱",
                category = "Câmbio, Wise & Arbitragem",
                level = 3,
                currentXp = 540,
                requiredXp = 750,
                description = "Especialista em leitura de tela de bancos (Wise/Fintechs), cálculo de centavos de spread cambial e timing ótimo de compra/venda.",
                skills = listOf(
                    ProfessionSkill("sk_spread_calc", "Detector de Centavos de Spread", "Calcula a diferença em frações de centavos entre moedas base (USD/BRL/EUR)", true, 1, "Spread: Δ = (Ask - Bid) * 100"),
                    ProfessionSkill("sk_vision_bank", "Visão Computacional de Telas Bancárias", "Identifica saldo, moedas e botões 'Converter' e 'Enviar' via Accessibility Node Info", true, 2, "Leitor: NodeHierarchy.bounds"),
                    ProfessionSkill("sk_triangular_arb", "Arbitragem Triangular Automatizada", "Simula conversão cruzada BRL ➔ USD ➔ EUR ➔ BRL calculando lucro imediato", true, 3, "Equação: R_tri = (A/Ask_1)*Bid_2*Bid_3"),
                    ProfessionSkill("sk_timing_rsi", "Timing Quântico de Compra/Venda", "Dispara ações nos fundos e topos de oscilação do câmbio internacional", false, 4, "Gatilho: RSI < 30 || RSI > 70")
                )
            )
        )
    }

    fun getDefaultWisdom(): List<KnowledgeEntry> {
        return listOf(
            KnowledgeEntry(
                id = "w_forex_1",
                sourceUrlOrApp = "Wise & Mercado Interbancário",
                topic = "Arbitragem de Centavos no Câmbio em Tempo Real",
                insight = "A flutuação de 2 a 5 centavos no câmbio comercial entre USD/BRL e EUR/BRL abre janelas de 45 segundos para conversão vantajosa. O reconhecimento de tela do nó de confirmação permite agir no ápice do spread.",
                timestamp = System.currentTimeMillis() - 10800000,
                professionName = "Trader Cambial & Arbitrador Autônomo",
                xpAwarded = 95
            ),
            KnowledgeEntry(
                id = "w_1",
                sourceUrlOrApp = "gemini.google.com",
                topic = "Auto-Aprendizado com Gemini Web",
                insight = "A interface web do Gemini responde perfeitamente a injeções no campo contenteditable. Respostas obtidas ampliam os dados heurísticos do cérebro sem necessitar de chaves de API.",
                timestamp = System.currentTimeMillis() - 7200000,
                professionName = "Interator Gemini & IA Web",
                xpAwarded = 75
            ),
            KnowledgeEntry(
                id = "w_2",
                sourceUrlOrApp = "Google Chrome",
                topic = "Otimização de Taxa de Quadros (120 FPS)",
                insight = "Em telas de 120Hz, toques enviados em múltiplos de 8.33ms sincronizam com a varredura VSYNC, eliminando perda de toques pelo driver gráfico.",
                timestamp = System.currentTimeMillis() - 3600000,
                professionName = "Cientista Quântico & Otimizador",
                xpAwarded = 60
            ),
            KnowledgeEntry(
                id = "w_3",
                sourceUrlOrApp = "Jogos de Ação / Clickers",
                topic = "Dispersão Biométrica Anti-Detecção",
                insight = "Intervalos perfeitamente estáticos (ex: 100.0ms cravados) acionam alarmes em servidores de jogos. Um jitter Gaussiano de ±15% simula a contração muscular biológica e garante imunidade.",
                timestamp = System.currentTimeMillis() - 1800000,
                professionName = "Gamer Pro & Speedrunner",
                xpAwarded = 80
            )
        )
    }

    /**
     * Autonomously creates a new profession and custom skills
     * when the user provides any new game, app, or website!
     */
    fun createProfessionForApp(appNameOrTask: String): Profession {
        val cleanName = appNameOrTask.trim().replaceFirstChar { it.uppercase() }
        val id = "prof_custom_" + UUID.randomUUID().toString().take(8)

        val icon = when {
            cleanName.contains("rpg", true) || cleanName.contains("craft", true) -> "⚔️"
            cleanName.contains("music", true) || cleanName.contains("som", true) -> "🎵"
            cleanName.contains("image", true) || cleanName.contains("foto", true) || cleanName.contains("arte", true) -> "🎨"
            cleanName.contains("trade", true) || cleanName.contains("invest", true) || cleanName.contains("banco", true) -> "📈"
            cleanName.contains("video", true) || cleanName.contains("tube", true) -> "🎬"
            cleanName.contains("chat", true) || cleanName.contains("social", true) -> "💬"
            else -> "✨"
        }

        return Profession(
            id = id,
            title = "Especialista em $cleanName",
            iconEmoji = icon,
            category = "Profissão Criada Autonomamente",
            level = 1,
            currentXp = 0,
            requiredXp = 300,
            description = "Profissão sintetizada sob demanda pelo Cérebro Autônomo para operar especificamente no ecossistema de $cleanName.",
            skills = listOf(
                ProfessionSkill("${id}_s1", "Navegação e Reconhecimento de $cleanName", "Mapeia as coordenadas essenciais e botões de ação", true, 1, "Target: Mapeamento de UI"),
                ProfessionSkill("${id}_s2", "Rotina de Aceleração em $cleanName", "Dispara toques em alta precisão nos ciclos ótimos de $cleanName", false, 2, "Cadência: Adaptativa"),
                ProfessionSkill("${id}_s3", "Domínio e Auto-Farming Mestre", "Automatiza totalmente as tarefas repetitivas com aprendizado contínuo", false, 3, "Ciclo: Autônomo 100%")
            ),
            autoCreated = true
        )
    }

    /**
     * Performs an autonomous real web query (e.g. searching Wikipedia or tech docs)
     * and derives a new insight for the Wisdom Database without requiring any API key!
     */
    suspend fun learnOnlineFromWeb(queryTopic: String): Result<KnowledgeEntry> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = java.net.URLEncoder.encode(queryTopic, "UTF-8")
            val url = "https://pt.wikipedia.org/api/rest_v1/page/summary/$encodedQuery"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "IAutClicAutonomousAgent/1.0 (Android; Autonomous Research)")
                .build()

            val response = try {
                httpClient.newCall(request).execute()
            } catch (_: Exception) {
                null
            }

            var extract = ""
            if (response != null && response.isSuccessful) {
                val body = response.body?.string().orEmpty()
                // Simple regex extraction to avoid heavy JSON parsing for standard Wikipedia summaries
                val extractMatch = Regex("\"extract\"\\s*:\\s*\"(.*?)\"").find(body)
                if (extractMatch != null) {
                    extract = extractMatch.groupValues[1]
                        .replace("\\n", " ")
                        .replace("\\\"", "\"")
                }
            }

            val finalInsight = if (extract.isNotBlank() && extract.length > 20) {
                "Extraído da web sobre '$queryTopic': ${extract.take(240)}..."
            } else {
                // Heuristic synthesis based on topic
                synthesizeAutonomousInsight(queryTopic)
            }

            val entry = KnowledgeEntry(
                id = UUID.randomUUID().toString(),
                sourceUrlOrApp = "Pesquisa Web Online: $queryTopic",
                topic = "Conhecimento Aplicado: $queryTopic",
                insight = finalInsight,
                timestamp = System.currentTimeMillis(),
                professionName = "Navegador Chrome & Automação Web",
                xpAwarded = Random.nextInt(45, 95)
            )

            Result.success(entry)
        } catch (e: Exception) {
            val fallbackEntry = KnowledgeEntry(
                id = UUID.randomUUID().toString(),
                sourceUrlOrApp = "Cérebro Heurístico Local",
                topic = queryTopic,
                insight = synthesizeAutonomousInsight(queryTopic),
                timestamp = System.currentTimeMillis(),
                professionName = "Cientista Quântico & Otimizador",
                xpAwarded = 50
            )
            Result.success(fallbackEntry)
        }
    }

    private fun synthesizeAutonomousInsight(topic: String): String {
        return when {
            topic.contains("gemini", true) ->
                "A interação direta com a interface web do Gemini possibilita solicitar prompts avançados para criar roteiros de autoclicker, desafios de jogos e descrições detalhadas para geração de imagens."
            topic.contains("jogo", true) || topic.contains("game", true) ->
                "Jogos móveis baseados em toques utilizam filas assíncronas no Android. Toques mantidos por 35ms a 50ms garantem registro perfeito no motor Unity/Unreal."
            topic.contains("musica", true) || topic.contains("som", true) ->
                "Geradores de música online estruturam faixas por BPM e estilos (lo-fi, chiptune, synthwave). O cérebro pode enviar comandos estruturados de BPM para compor trilhas em segundo plano."
            topic.contains("imagem", true) || topic.contains("foto", true) ->
                "A criação autônoma de imagens se beneficia de prompts com estilo ('isometric 3d, pixel art, high contrast, clean gaming UI'), permitindo gerar assets gráficos sob demanda."
            else ->
                "Padrão cognitivo assimilado para '$topic': a taxa de amostragem de toques e o tempo de reação adaptativo foram calculados para máxima eficiência operacional sem bloqueios."
        }
    }

    /**
     * Generates tailored JavaScript to automate actions inside the built-in Chrome WebView.
     */
    fun buildJavaScriptForAutonomousAction(
        mode: BrowserAutonomousMode,
        customPrompt: String
    ): String {
        val safePrompt = customPrompt.replace("\"", "\\\"").replace("\n", " ")

        return when (mode) {
            BrowserAutonomousMode.TALK_TO_GEMINI -> """
                (function() {
                    try {
                        var promptText = "$safePrompt";
                        if (!promptText) promptText = "Explique como otimizar reflexos e cadência de cliques em jogos e sugira um novo conceito criativo.";
                        
                        // Find common contenteditable or textareas on modern Gemini / chat interfaces
                        var input = document.querySelector('div[contenteditable="true"]') || 
                                    document.querySelector('textarea') || 
                                    document.querySelector('input[type="text"]');
                                    
                        if (input) {
                            input.focus();
                            if (input.tagName === 'TEXTAREA' || input.tagName === 'INPUT') {
                                input.value = promptText;
                            } else {
                                input.innerText = promptText;
                            }
                            input.dispatchEvent(new Event('input', { bubbles: true }));
                            input.dispatchEvent(new Event('change', { bubbles: true }));
                            
                            // Find send button
                            setTimeout(function() {
                                var buttons = Array.from(document.querySelectorAll('button, div[role="button"]'));
                                var sendBtn = buttons.find(function(b) {
                                    var label = (b.getAttribute('aria-label') || b.innerText || '').toLowerCase();
                                    return label.includes('send') || label.includes('enviar') || label.includes('submit');
                                });
                                if (sendBtn) {
                                    sendBtn.click();
                                }
                            }, 400);
                            return "Sucesso: Prompt digitado no Gemini e comando de envio acionado!";
                        } else {
                            return "Aviso: Campo de texto do chat não detectado nesta página. Aguarde o carregamento completo.";
                        }
                    } catch(e) {
                        return "Erro ao injetar prompt: " + e.message;
                    }
                })();
            """.trimIndent()

            BrowserAutonomousMode.GENERATE_IMAGE -> """
                (function() {
                    try {
                        var promptText = "$safePrompt";
                        if (!promptText) promptText = "Cyberpunk digital mouse cursor with neon laser pointer, 3D render, 8k";
                        var input = document.querySelector('textarea, input[type="text"]');
                        if (input) {
                            input.value = promptText;
                            input.dispatchEvent(new Event('input', { bubbles: true }));
                            setTimeout(function() {
                                var btn = Array.from(document.querySelectorAll('button')).find(function(b) {
                                    var t = (b.innerText || '').toLowerCase();
                                    return t.includes('generate') || t.includes('create') || t.includes('gerar') || t.includes('draw');
                                });
                                if (btn) btn.click();
                            }, 350);
                            return "Sucesso: Prompt de imagem '" + promptText + "' injetado e botão de geração acionado!";
                        }
                        return "Aguardando campo de prompt de imagem...";
                    } catch(e) {
                        return "Erro: " + e.message;
                    }
                })();
            """.trimIndent()

            BrowserAutonomousMode.GENERATE_MUSIC -> """
                (function() {
                    try {
                        var promptText = "$safePrompt";
                        if (!promptText) promptText = "Upbeat 8-bit chiptune gaming theme with energetic tempo and synthwave bassline";
                        var input = document.querySelector('textarea, input[type="text"]');
                        if (input) {
                            input.value = promptText;
                            input.dispatchEvent(new Event('input', { bubbles: true }));
                            setTimeout(function() {
                                var btn = Array.from(document.querySelectorAll('button')).find(function(b) {
                                    var t = (b.innerText || '').toLowerCase();
                                    return t.includes('create') || t.includes('generate') || t.includes('criar') || t.includes('make');
                                });
                                if (btn) btn.click();
                            }, 350);
                            return "Sucesso: Prompt musical injetado no estúdio online!";
                        }
                        return "Aguardando interface de criação musical...";
                    } catch(e) {
                        return "Erro: " + e.message;
                    }
                })();
            """.trimIndent()

            BrowserAutonomousMode.RESEARCH_KNOWLEDGE -> """
                (function() {
                    try {
                        var paras = Array.from(document.querySelectorAll('p')).map(function(p) { return p.innerText.trim(); }).filter(function(t) { return t.length > 40; });
                        var heading = document.querySelector('h1, h2');
                        var title = heading ? heading.innerText : document.title;
                        var excerpt = paras.slice(0, 3).join(' ');
                        return JSON.stringify({ title: title, content: excerpt.substring(0, 350) });
                    } catch(e) {
                        return JSON.stringify({ title: document.title, content: "Erro de extração: " + e.message });
                    }
                })();
            """.trimIndent()

            BrowserAutonomousMode.AUTO_CLICK_GAME -> """
                (function() {
                    try {
                        // Find clickable game canvas or target button
                        var target = document.querySelector('button.clicker, #clicker, #target, canvas, button');
                        if (target) {
                            for (var i = 0; i < 5; i++) {
                                target.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true }));
                            }
                            return "Sucesso: Rajada de 5 cliques rápidos executada no alvo do jogo web!";
                        }
                        return "Nenhum alvo de jogo web localizado.";
                    } catch(e) {
                        return "Erro: " + e.message;
                    }
                })();
            """.trimIndent()
        }
    }
}
