package com.example.model

/**
 * Data structures for the Quantum Autonomous Council & Meeting Room.
 * Autonomous worker agents (Mathematicians, Forex Arbitrageurs, Graph Analysts,
 * Store & App Ecosystem Scanners) that interconnect, debate in real-time,
 * auto-recruit new specialists, and synthesize operational knowledge.
 */

enum class SpecialistRole(
    val title: String,
    val iconEmoji: String,
    val description: String,
    val quantumPace: String
) {
    QUANTUM_MATH_SCIENTIST(
        "Dr. Matemático Quântico",
        "⚛️",
        "Calcula diferenciais de centavos, micro-probabilidades de spreads e curvas estocásticas",
        "10.000 ops/ms"
    ),
    FOREX_EXCHANGE_OPERATOR(
        "Especialista em Câmbio & Arbitragem",
        "💱",
        "Monitora taxas de compra/venda em Wise, bancos e corretoras com timing ideal",
        "Instantâneo"
    ),
    REALTIME_GRAPH_ANALYST(
        "Doutor em Análise Gráfica Online",
        "📈",
        "Interpreta velas Candlestick, RSI, Médias Exponenciais (EMA) e rompimentos de tendência",
        "Sub-segundo"
    ),
    ECOSYSTEM_APP_DISCOVERER(
        "Varredor de Lojas & Ecossistema",
        "🌐",
        "Explora integrações e padrões de interfaces em lojas, web e apps em segundo/primeiro plano",
        "Multi-thread"
    ),
    AUTONOMOUS_DECISION_ENGINE(
        "Coordenador do Cérebro da Reunião",
        "🧠",
        "Unifica debates da mesa redonda, gera novos cientistas e comanda ações automáticas",
        "Contínuo"
    )
}

data class AutonomousStaffMember(
    val id: String,
    val name: String,
    val role: SpecialistRole,
    val avatarEmoji: String,
    val level: Int,
    val totalCalculations: Long,
    val specialFormula: String,
    val currentThought: String,
    val isSpeaking: Boolean = false,
    val autoRecruited: Boolean = false
)

data class CouncilDialogue(
    val id: Long,
    val timestamp: String,
    val speakerName: String,
    val speakerEmoji: String,
    val speakerRole: String,
    val message: String,
    val insightTag: String
)

data class QuantumMeetingRoomState(
    val isMeetingActive: Boolean = true,
    val quantumCyclesCount: Long = 0L,
    val syncedBrainFrequencyHz: Double = 432.8,
    val totalSpecialistsRecruited: Int = 5,
    val staff: List<AutonomousStaffMember> = emptyList(),
    val dialogues: List<CouncilDialogue> = emptyList(),
    val collectiveWisdomSynthesized: Int = 0
)
