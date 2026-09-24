package com.example.data

import com.example.model.AutoClickerProfile
import com.example.model.ClickRateMode
import com.example.model.ExtendedClickType
import com.example.model.MacroActionType
import com.example.model.MacroSequence
import com.example.model.MacroStep
import com.example.model.MacroStepType
import com.example.model.MouseButtonType
import com.example.model.ProfileCategory
import com.example.model.SmartVisualTriggerRule
import com.example.model.TargetPoint
import com.example.model.VisualTriggerAction
import com.example.model.VisualTriggerCondition

/**
 * Built-in presets cloned directly from the Microsoft Store auto clicker apps:
 * - 9mszwlljjvb1 (Automatic Clicker - High-speed 1000 CPS, Visual Triggers, True Background Mode)
 * - 9n31lzkvgqvp (Auto Clicker App - Keyboard/Mouse Macro Recorder, Unlimited Profiles)
 */
object MicrosoftProfilePresets {

    fun getDefaultProfiles(): List<AutoClickerProfile> {
        return listOf(
            AutoClickerProfile(
                id = "preset_ultra_1000_cps",
                title = "⚡ Ultra CPS 1000 Gamer (Minecraft & Roblox)",
                description = "Velocidade máxima quântica de 1000 cliques por segundo para PVP, mineração e clickers idle rápidos.",
                category = ProfileCategory.GAMING,
                iconEmoji = "⚡",
                rateMode = ClickRateMode.CPS,
                targetCps = 1000,
                intervalMillis = 1L,
                extendedClickType = ExtendedClickType.SINGLE,
                mouseButton = MouseButtonType.LEFT,
                jitterEnabled = false,
                jitterMs = 0L,
                coordinateJitterRadiusPx = 1,
                isBuiltIn = true
            ),
            AutoClickerProfile(
                id = "preset_smart_visual_trigger",
                title = "🎯 Smart Visual Trigger (Reconhecimento de Cor/Imagem)",
                description = "Escaneia a tela em busca de cores ou imagens alvo. Quando o padrão surge, dispara rajada instantânea.",
                category = ProfileCategory.GAMING,
                iconEmoji = "🎯",
                rateMode = ClickRateMode.CPS,
                targetCps = 500,
                intervalMillis = 2L,
                extendedClickType = ExtendedClickType.DOUBLE,
                mouseButton = MouseButtonType.LEFT,
                jitterEnabled = true,
                jitterMs = 8L,
                coordinateJitterRadiusPx = 3,
                visualTriggerId = "trigger_emerald_loot",
                isBuiltIn = true
            ),
            AutoClickerProfile(
                id = "preset_multi_point_farm",
                title = "🔄 Multi-Ponto Sequencial (Farming em 3 Etapas)",
                description = "Ciclo ordenado entre 3 alvos na tela (Ponto 1 -> Ponto 2 -> Ponto 3) com atrasos independentes.",
                category = ProfileCategory.GAMING,
                iconEmoji = "🔄",
                rateMode = ClickRateMode.EXACT_TIME,
                targetCps = 20,
                intervalMillis = 250L,
                extendedClickType = ExtendedClickType.SINGLE,
                mouseButton = MouseButtonType.LEFT,
                jitterEnabled = true,
                jitterMs = 15L,
                coordinateJitterRadiusPx = 4,
                multiPoints = listOf(
                    TargetPoint(id = 1, x = 320, y = 780, delayAfterMs = 200L, holdDurationMs = 35L, clickCount = 1, label = "Coletar Recurso", colorHex = "#00E5FF"),
                    TargetPoint(id = 2, x = 540, y = 960, delayAfterMs = 250L, holdDurationMs = 35L, clickCount = 2, label = "Atacar Monstro", colorHex = "#A855F7"),
                    TargetPoint(id = 3, x = 760, y = 1140, delayAfterMs = 300L, holdDurationMs = 45L, clickCount = 1, label = "Usar Poção", colorHex = "#10B981")
                ),
                macroId = "macro_farming_cycle",
                isBuiltIn = true
            ),
            AutoClickerProfile(
                id = "preset_forex_sniper",
                title = "📈 Sniper de Câmbio & Arbitragem (Wise / Binance)",
                description = "Disparo cirúrgico em centavos favoráveis de compra e venda de moedas com verificação em tempo real.",
                category = ProfileCategory.TRADING,
                iconEmoji = "📈",
                rateMode = ClickRateMode.CPS,
                targetCps = 60,
                intervalMillis = 16L,
                extendedClickType = ExtendedClickType.SINGLE,
                mouseButton = MouseButtonType.LEFT,
                jitterEnabled = true,
                jitterMs = 10L,
                coordinateJitterRadiusPx = 2,
                multiPoints = listOf(
                    TargetPoint(id = 1, x = 540, y = 620, delayAfterMs = 100L, holdDurationMs = 30L, clickCount = 1, label = "Selecionar Par", colorHex = "#38BDF8"),
                    TargetPoint(id = 2, x = 540, y = 1180, delayAfterMs = 150L, holdDurationMs = 40L, clickCount = 1, label = "Confirmar Troca", colorHex = "#22C55E")
                ),
                isBuiltIn = true
            ),
            AutoClickerProfile(
                id = "preset_stealth_anti_cheat",
                title = "🛡️ Modo Furtivo Humano (Anti-Detecção)",
                description = "Cadência orgânica humana (10 a 16 CPS) com desvio gaussiano de milissegundos e raio aleatório de coordenadas.",
                category = ProfileCategory.STEALTH,
                iconEmoji = "🛡️",
                rateMode = ClickRateMode.CPS,
                targetCps = 14,
                intervalMillis = 71L,
                extendedClickType = ExtendedClickType.SINGLE,
                mouseButton = MouseButtonType.LEFT,
                jitterEnabled = true,
                jitterMs = 35L,
                coordinateJitterRadiusPx = 8,
                isBuiltIn = true
            ),
            AutoClickerProfile(
                id = "preset_data_entry_form",
                title = "📝 Preenchimento Rápido & Automação de Formulários",
                description = "Sequência de toques para campos de texto, avanço de páginas e confirmação automática em cadastros.",
                category = ProfileCategory.DATA_ENTRY,
                iconEmoji = "📝",
                rateMode = ClickRateMode.EXACT_TIME,
                targetCps = 5,
                intervalMillis = 500L,
                extendedClickType = ExtendedClickType.SINGLE,
                mouseButton = MouseButtonType.LEFT,
                jitterEnabled = false,
                jitterMs = 0L,
                coordinateJitterRadiusPx = 0,
                multiPoints = listOf(
                    TargetPoint(id = 1, x = 540, y = 650, delayAfterMs = 300L, holdDurationMs = 50L, clickCount = 1, label = "Campo Código", colorHex = "#F59E0B"),
                    TargetPoint(id = 2, x = 540, y = 920, delayAfterMs = 400L, holdDurationMs = 50L, clickCount = 1, label = "Botão Enviar", colorHex = "#10B981")
                ),
                isBuiltIn = true
            )
        )
    }

    fun getDefaultVisualTriggers(): List<SmartVisualTriggerRule> {
        return listOf(
            SmartVisualTriggerRule(
                id = "trigger_emerald_loot",
                name = "Esmeralda / Alvo Verde (Loot & Recompensa)",
                sampleX = 540,
                sampleY = 960,
                targetColorHex = "#10B981",
                tolerancePercent = 85,
                condition = VisualTriggerCondition.COLOR_MATCHES,
                action = VisualTriggerAction.BURST_1000_CPS,
                checkIntervalMs = 120L,
                cooldownMs = 1200L,
                isEnabled = true
            ),
            SmartVisualTriggerRule(
                id = "trigger_gold_coin",
                name = "Moeda Dourada / Botão Comprar",
                sampleX = 540,
                sampleY = 820,
                targetColorHex = "#F59E0B",
                tolerancePercent = 80,
                condition = VisualTriggerCondition.COLOR_MATCHES,
                action = VisualTriggerAction.CLICK_AT_TRIGGER_POINT,
                checkIntervalMs = 150L,
                cooldownMs = 800L,
                isEnabled = true
            ),
            SmartVisualTriggerRule(
                id = "trigger_screen_flash",
                name = "Flash de Alerta / Respawn",
                sampleX = 540,
                sampleY = 500,
                targetColorHex = "#EF4444",
                tolerancePercent = 75,
                condition = VisualTriggerCondition.COLOR_CHANGES,
                action = VisualTriggerAction.NOTIFY_QUANTUM_COUNCIL,
                checkIntervalMs = 200L,
                cooldownMs = 2000L,
                isEnabled = false
            )
        )
    }

    fun getDefaultMacros(): List<MacroSequence> {
        return listOf(
            MacroSequence(
                id = "macro_farming_cycle",
                name = "Ciclo Rápido de Farming",
                description = "Macro com toques em sequência e gesto de deslizar para reciclagem de mapa.",
                steps = listOf(
                    MacroStep(1, MacroStepType.CLICK, x = 320, y = 780, delayBeforeMs = 100L, durationMs = 35L, note = "Toque no Alvo 1"),
                    MacroStep(2, MacroStepType.WAIT_DELAY, delayBeforeMs = 200L, note = "Aguardar animação"),
                    MacroStep(3, MacroStepType.DOUBLE_CLICK, x = 540, y = 960, delayBeforeMs = 150L, durationMs = 30L, note = "Ataque duplo"),
                    MacroStep(4, MacroStepType.SWIPE, x = 540, y = 1200, endX = 540, endY = 600, delayBeforeMs = 250L, durationMs = 200L, note = "Arrasto para rolagem"),
                    MacroStep(5, MacroStepType.CLICK, x = 760, y = 1140, delayBeforeMs = 200L, durationMs = 40L, note = "Coleta final")
                ),
                repeatCount = 0,
                playbackSpeed = 1.0f,
                loopUntilStopped = true
            )
        )
    }
}
