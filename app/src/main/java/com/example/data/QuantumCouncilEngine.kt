package com.example.data

import com.example.model.AutonomousStaffMember
import com.example.model.CouncilDialogue
import com.example.model.QuantumMeetingRoomState
import com.example.model.SpecialistRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Autonomous Quantum Meeting Room Engine.
 * Simulates real-time synchronous neural discourse between autonomous specialists
 * (Mathematicians, Currency Arbitrageurs, Real-Time Chart Readers, and Ecosystem Scanners).
 * As they calculate and debate, they dynamically auto-recruit new Doctors and Experts
 * to expand the Collective Brain.
 */
class QuantumCouncilEngine {

    private val timeFormatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    private val initialStaff = listOf(
        AutonomousStaffMember(
            id = "staff_math_1",
            name = "Dr. Gauss Quântico",
            role = SpecialistRole.QUANTUM_MATH_SCIENTIST,
            avatarEmoji = "⚛️",
            level = 8,
            totalCalculations = 1420500L,
            specialFormula = "d(Spread)/dt + ∑(ΔCentavos) > 0.12",
            currentThought = "Calculando derivadas estocásticas de centavos em tempo real..."
        ),
        AutonomousStaffMember(
            id = "staff_forex_1",
            name = "Dra. Valuta Arbitrage",
            role = SpecialistRole.FOREX_EXCHANGE_OPERATOR,
            avatarEmoji = "💱",
            level = 9,
            totalCalculations = 987400L,
            specialFormula = "Route(USD ➔ EUR ➔ BRL) - SpreadTaxa > 0.05",
            currentThought = "Comparando cotações Wise vs Mercado Interbancário..."
        ),
        AutonomousStaffMember(
            id = "staff_chart_1",
            name = "Prof. Candlestick Realtime",
            role = SpecialistRole.REALTIME_GRAPH_ANALYST,
            avatarEmoji = "📈",
            level = 7,
            totalCalculations = 654200L,
            specialFormula = "RSI(14) < 30 ∧ EMA(9) > EMA(21) [Ponto de Compra]",
            currentThought = "Mapeando rompimento de suporte em USD/BRL..."
        ),
        AutonomousStaffMember(
            id = "staff_store_1",
            name = "Cientista Omni-Store",
            role = SpecialistRole.ECOSYSTEM_APP_DISCOVERER,
            avatarEmoji = "🌐",
            level = 6,
            totalCalculations = 432100L,
            specialFormula = "ScanTree(AccessibilityNodes) ➔ Map(ConvertButtons)",
            currentThought = "Varrendo interfaces de bancos e lojas no ecossistema..."
        ),
        AutonomousStaffMember(
            id = "staff_brain_core",
            name = "Cérebro Central da Sala",
            role = SpecialistRole.AUTONOMOUS_DECISION_ENGINE,
            avatarEmoji = "🧠",
            level = 10,
            totalCalculations = 2500000L,
            specialFormula = "Consensus(Council) ➔ AutoRecruit(NewDoctors)",
            currentThought = "Sincronizando mentes quânticas na frequência de 432.8 Hz..."
        )
    )

    private val _meetingState = MutableStateFlow(
        QuantumMeetingRoomState(
            isMeetingActive = true,
            quantumCyclesCount = 50000L,
            syncedBrainFrequencyHz = 432.8,
            totalSpecialistsRecruited = initialStaff.size,
            staff = initialStaff,
            dialogues = listOf(
                CouncilDialogue(
                    id = System.currentTimeMillis(),
                    timestamp = timeFormatter.format(Date()),
                    speakerName = "Cérebro Central da Sala",
                    speakerEmoji = "🧠",
                    speakerRole = "Coordenador",
                    message = "Sala de reunião quântica iniciada. Conectando cérebros autônomos em malha unificada.",
                    insightTag = "INICIALIZAÇÃO"
                )
            ),
            collectiveWisdomSynthesized = 120
        )
    )
    val meetingState: StateFlow<QuantumMeetingRoomState> = _meetingState.asStateFlow()

    private val speechTemplates = mapOf(
        SpecialistRole.QUANTUM_MATH_SCIENTIST to listOf(
            "Detectei variação infinitesimal de +0.038 centavos no par USD/BRL. O gradiente matemático favorece conversão!",
            "Convergência estocástica confirmada: a probabilidade de lucro no micro-spread é de 94.7%.",
            "Resolvendo equações de Navier-Stokes aplicadas ao fluxo de liquidez cambial em bancos.",
            "A matriz de covariância entre moedas aponta momento ótimo para a Dra. Valuta agir."
        ),
        SpecialistRole.FOREX_EXCHANGE_OPERATOR to listOf(
            "Analisando spread da Wise: Compra em R$ 5,6620 e Venda em R$ 5,6840. Margem de centavos favorável!",
            "A taxa de conversão direta superou o custo de swap. Rota triangular EUR/BRL pronta para execução.",
            "Mapeei o botão de conversão no aplicativo bancário em primeiro plano via coordenadas de acessibilidade.",
            "Alinhamento perfeito com as previsões matemáticas. Lucro em centavos capturado e registrado no banco Room."
        ),
        SpecialistRole.REALTIME_GRAPH_ANALYST to listOf(
            "Vela Candlestick martelo formada no gráfico de 1 minuto! O RSI tocou 28.4 (sobrevenda).",
            "Média móvel rápida (EMA 9) cruzou para cima da EMA 21. Tendência de valorização confirmada em tempo real!",
            "Bandas de Bollinger estreitaram no gráfico online. Explosão de volatilidade iminente.",
            "Padrão gráfico 'Fundo Duplo' validado no par cambial selecionado."
        ),
        SpecialistRole.ECOSYSTEM_APP_DISCOVERER to listOf(
            "Identifiquei nós interativos e telas de câmbio em apps ativos e lojas do ecossistema.",
            "Mapeamento da hierarquia visual concluído: botão 'Confirmar Troca' identificado em (540, 1680).",
            "Padrões de UI catalogados para operação em segundo e primeiro plano com precisão de cursor.",
            "Explorando novos repositórios e endpoints de dados para enriquecer o banco de sabedoria."
        ),
        SpecialistRole.AUTONOMOUS_DECISION_ENGINE to listOf(
            "Consenso quântico alcançado entre Matemática, Câmbio e Gráficos. Disparando ordens de clique!",
            "Sintetizando aprendizados do diálogo no Cérebro Central. Experiência coletiva aumentada.",
            "Recrutando novo cientista especialista em cálculos e leitura gráfica para expandir a bancada!",
            "Frequência de sincronia neural estabilizada. O cérebro da sala opera em alta cadência autônoma."
        )
    )

    private val doctorCandidates = listOf(
        Triple("Dr. Laplace Probabilístico", SpecialistRole.QUANTUM_MATH_SCIENTIST, "Modelagem Bayesiana de Centavos"),
        Triple("Dra. Forex Arbitragem Global", SpecialistRole.FOREX_EXCHANGE_OPERATOR, "Execução Rápida em Múltiplos Bancos"),
        Triple("Prof. Ichimoku Kinko Hyo", SpecialistRole.REALTIME_GRAPH_ANALYST, "Nuvens de Tendência e Fibonacci Online"),
        Triple("Dr. Turing Ecossistema & Web", SpecialistRole.ECOSYSTEM_APP_DISCOVERER, "Varredura Multithread de Stores e Apps"),
        Triple("Dra. Bernoulli de Micro-Spreads", SpecialistRole.QUANTUM_MATH_SCIENTIST, "Distribuição Binomial de Lucros Cambiais"),
        Triple("Dr. Fibonacci de Alta Frequência", SpecialistRole.REALTIME_GRAPH_ANALYST, "Retrações Gráficas em Sub-Segundos")
    )

    /**
     * Executes a single quantum cycle in the meeting room:
     * - One specialist speaks and shares real-time thought
     * - Calculations increment by thousands
     * - Auto-recruits new doctors when wisdom reaches milestones
     */
    fun tickQuantumMeetingCycle(): String? {
        val current = _meetingState.value
        if (!current.isMeetingActive) return null

        val currentStaff = current.staff.toMutableList()
        val speakerIndex = Random.nextInt(currentStaff.size)
        val speaker = currentStaff[speakerIndex]

        val speeches = speechTemplates[speaker.role] ?: listOf("Processando operações em velocidade quântica...")
        val chosenSpeech = speeches.random()

        // Update speaker's calculations and status
        val updatedStaff = currentStaff.mapIndexed { idx, member ->
            if (idx == speakerIndex) {
                member.copy(
                    totalCalculations = member.totalCalculations + Random.nextLong(5000L, 25000L),
                    currentThought = chosenSpeech,
                    isSpeaking = true
                )
            } else {
                member.copy(
                    totalCalculations = member.totalCalculations + Random.nextLong(1000L, 8000L),
                    isSpeaking = false
                )
            }
        }.toMutableList()

        val newDialogues = current.dialogues.takeLast(25).toMutableList()
        newDialogues.add(
            CouncilDialogue(
                id = System.currentTimeMillis(),
                timestamp = timeFormatter.format(Date()),
                speakerName = speaker.name,
                speakerEmoji = speaker.avatarEmoji,
                speakerRole = speaker.role.title,
                message = chosenSpeech,
                insightTag = speaker.role.name.take(12)
            )
        )

        var newRecruitMessage: String? = null
        var totalRecruited = current.totalSpecialistsRecruited

        // Auto-recruit new doctor if criteria met (every ~6-8 cycles or chance)
        if (Random.nextInt(8) == 0 && updatedStaff.size < 12) {
            val candidate = doctorCandidates.filter { cand -> updatedStaff.none { it.name == cand.first } }.randomOrNull()
            if (candidate != null) {
                val newDoctor = AutonomousStaffMember(
                    id = "staff_auto_${System.currentTimeMillis()}",
                    name = candidate.first,
                    role = candidate.second,
                    avatarEmoji = candidate.second.iconEmoji,
                    level = Random.nextInt(5, 10),
                    totalCalculations = Random.nextLong(100000L, 500000L),
                    specialFormula = candidate.third,
                    currentThought = "Auto-recrutado pelo cérebro da sala de reunião para maximizar operações!",
                    autoRecruited = true
                )
                updatedStaff.add(newDoctor)
                totalRecruited++
                newRecruitMessage = "🎓 NOVO CIENTISTA RECRUTADO: ${newDoctor.name} (${newDoctor.role.title}) juntou-se à mesa redonda!"

                newDialogues.add(
                    CouncilDialogue(
                        id = System.currentTimeMillis() + 1,
                        timestamp = timeFormatter.format(Date()),
                        speakerName = "Cérebro Central da Sala",
                        speakerEmoji = "🧠",
                        speakerRole = "Coordenador",
                        message = "Conexão neural estabelecida com ${newDoctor.name}. Fórmulas matemáticas e sabedoria integradas à sala de reunião!",
                        insightTag = "AUTO-RECRUTAMENTO"
                    )
                )
            }
        }

        _meetingState.value = current.copy(
            quantumCyclesCount = current.quantumCyclesCount + Random.nextLong(100L, 500L),
            syncedBrainFrequencyHz = (432.0 + Random.nextDouble(-1.5, 1.5)),
            totalSpecialistsRecruited = totalRecruited,
            staff = updatedStaff,
            dialogues = newDialogues,
            collectiveWisdomSynthesized = current.collectiveWisdomSynthesized + Random.nextInt(1, 4)
        )

        return newRecruitMessage
    }

    fun toggleMeetingActive(): Boolean {
        val willActive = !_meetingState.value.isMeetingActive
        _meetingState.value = _meetingState.value.copy(isMeetingActive = willActive)
        return willActive
    }

    fun manuallyRecruitSpecialist(name: String, role: SpecialistRole, formula: String): AutonomousStaffMember {
        val current = _meetingState.value
        val newMember = AutonomousStaffMember(
            id = "staff_custom_${System.currentTimeMillis()}",
            name = name.ifBlank { "Dr. Especialista Quântico" },
            role = role,
            avatarEmoji = role.iconEmoji,
            level = 1,
            totalCalculations = 50000L,
            specialFormula = formula.ifBlank { "Análise de Dados & Operações" },
            currentThought = "Conectado à sala de reunião pelo usuário.",
            autoRecruited = false
        )
        val updatedList = current.staff + newMember
        _meetingState.value = current.copy(
            staff = updatedList,
            totalSpecialistsRecruited = updatedList.size
        )
        return newMember
    }
}
