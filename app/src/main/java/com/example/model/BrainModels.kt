package com.example.model

/**
 * Data structures modeling the Autonomous Neural Brain,
 * dynamic professions, evolutive skill trees, and online wisdom database.
 */

data class Profession(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val category: String,
    val level: Int,
    val currentXp: Int,
    val requiredXp: Int,
    val description: String,
    val skills: List<ProfessionSkill>,
    val autoCreated: Boolean = false
) {
    val progressFraction: Float
        get() = (currentXp.toFloat() / requiredXp.toFloat()).coerceIn(0f, 1f)
}

data class ProfessionSkill(
    val id: String,
    val name: String,
    val description: String,
    val unlocked: Boolean,
    val tier: Int,
    val executionFormula: String
)

data class KnowledgeEntry(
    val id: String,
    val sourceUrlOrApp: String,
    val topic: String,
    val insight: String,
    val timestamp: Long,
    val professionName: String,
    val xpAwarded: Int
)

enum class BrowserAutonomousMode(val label: String, val description: String) {
    TALK_TO_GEMINI("Diálogo com Gemini", "Conversa autônoma para auto-aprendizagem de dados"),
    GENERATE_IMAGE("Gerar Imagens IA", "Navega e digita prompts de criação visual"),
    GENERATE_MUSIC("Gerar Músicas IA", "Comanda geradores musicais da web"),
    RESEARCH_KNOWLEDGE("Pesquisar na Web", "Lê e sintetiza conhecimento para o Banco de Sabedoria"),
    AUTO_CLICK_GAME("Automação de Jogo Web", "Localiza botões e alvos para clicar em alta cadência")
}

data class AutonomousLog(
    val id: Long,
    val timestampFormatted: String,
    val profession: String,
    val action: String,
    val outcome: String
)
