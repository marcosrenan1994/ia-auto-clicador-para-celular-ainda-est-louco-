package com.example.data.vision

import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.example.service.AutoClickAccessibilityService
import java.util.regex.Pattern

data class DetectedUiElement(
    val id: String,
    val text: String,
    val contentDescription: String,
    val className: String,
    val viewId: String,
    val bounds: Rect,
    val centerX: Int,
    val centerY: Int,
    val isClickable: Boolean,
    val isEditable: Boolean,
    val isCurrencyRelated: Boolean,
    val isActionTarget: Boolean,
    val confidenceScore: Float
)

data class ScreenAnalysisResult(
    val packageName: String,
    val windowTitle: String,
    val totalElementsCount: Int,
    val elements: List<DetectedUiElement>,
    val detectedCurrencies: List<String>,
    val detectedRatesAndValues: List<String>,
    val actionableTargets: List<DetectedUiElement>,
    val bestActionTarget: DetectedUiElement?,
    val fullTextDump: String,
    val timestamp: Long = System.currentTimeMillis()
)

object ScreenVisionScanner {

    private const val TAG = "ScreenVisionScanner"

    private val CURRENCY_REGEX = Pattern.compile("(?i)(USD|BRL|EUR|GBP|JPY|USDT|CAD|CHF|AUD|NZD|R\\$|\\$|€|£)")
    private val VALUE_REGEX = Pattern.compile("(?:R\\$|\\$|€|£)?\\s*\\d+([.,]\\d{2,6})?")
    private val ACTION_KEYWORDS = listOf(
        "converter", "comprar", "vender", "enviar", "transferir", "continuar",
        "confirmar", "wise", "saldo", "câmbio", "cotação", "taxa", "adicionar",
        "trade", "swap", "exchange", "buy", "sell", "revisar", "pagar", "converter agora"
    )

    /**
     * Inspects the currently active window on the device via AccessibilityService.
     * Returns structured list of nodes, coordinates, currencies, and actionable targets.
     */
    fun scanActiveWindow(): ScreenAnalysisResult? {
        val service = AutoClickAccessibilityService.instance ?: run {
            Log.w(TAG, "Accessibility service instance not active.")
            return null
        }

        val rootNode: AccessibilityNodeInfo? = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val windows = service.windows
                val activeWindow = windows.firstOrNull { it.isActive } ?: windows.firstOrNull()
                activeWindow?.root ?: service.rootInActiveWindow
            } else {
                service.rootInActiveWindow
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching root node", e)
            null
        }

        if (rootNode == null) {
            Log.w(TAG, "Root node is null")
            return null
        }

        val detectedElements = mutableListOf<DetectedUiElement>()
        val textSnippets = mutableListOf<String>()

        try {
            traverseNode(rootNode, detectedElements, textSnippets)
        } catch (e: Exception) {
            Log.e(TAG, "Error traversing node tree", e)
        }

        val pkgName = rootNode.packageName?.toString() ?: "Desconhecido"
        val currencies = mutableSetOf<String>()
        val ratesAndValues = mutableListOf<String>()

        for (el in detectedElements) {
            val fullText = "${el.text} ${el.contentDescription}".trim()
            val matcher = CURRENCY_REGEX.matcher(fullText)
            while (matcher.find()) {
                currencies.add(matcher.group().uppercase())
            }

            val valMatcher = VALUE_REGEX.matcher(fullText)
            while (valMatcher.find()) {
                val match = valMatcher.group().trim()
                if (match.length >= 2 && !ratesAndValues.contains(match)) {
                    ratesAndValues.add(match)
                }
            }
        }

        val actionable = detectedElements.filter { it.isActionTarget && it.isClickable }
        val best = actionable.maxByOrNull { it.confidenceScore } ?: detectedElements.firstOrNull { it.isClickable }

        return ScreenAnalysisResult(
            packageName = pkgName,
            windowTitle = rootNode.className?.toString() ?: "App Window",
            totalElementsCount = detectedElements.size,
            elements = detectedElements,
            detectedCurrencies = currencies.toList(),
            detectedRatesAndValues = ratesAndValues.take(15),
            actionableTargets = actionable,
            bestActionTarget = best,
            fullTextDump = textSnippets.joinToString("\n").take(2000)
        )
    }

    private fun traverseNode(
        node: AccessibilityNodeInfo,
        outList: MutableList<DetectedUiElement>,
        textDump: MutableList<String>
    ) {
        val bounds = Rect()
        node.getBoundsInScreen(bounds)

        val text = node.text?.toString() ?: ""
        val contentDesc = node.contentDescription?.toString() ?: ""
        val combined = "$text $contentDesc".trim()
        val className = node.className?.toString() ?: ""
        val viewId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            node.viewIdResourceName ?: ""
        } else ""

        if (combined.isNotBlank()) {
            textDump.add(combined)
        }

        val isCurrency = CURRENCY_REGEX.matcher(combined).find()
        val lower = combined.lowercase()
        val hasActionKeyword = ACTION_KEYWORDS.any { lower.contains(it) }

        var score = 0f
        if (node.isClickable) score += 2f
        if (hasActionKeyword) score += 5f
        if (isCurrency) score += 3f
        if (className.contains("Button") || className.contains("ImageButton")) score += 2f

        // Only add elements that have visible size and some meaning
        if (bounds.width() > 0 && bounds.height() > 0 && (combined.isNotBlank() || node.isClickable)) {
            val element = DetectedUiElement(
                id = "node_${outList.size + 1}",
                text = text,
                contentDescription = contentDesc,
                className = className.substringAfterLast('.'),
                viewId = viewId.substringAfterLast('/'),
                bounds = bounds,
                centerX = bounds.centerX(),
                centerY = bounds.centerY(),
                isClickable = node.isClickable,
                isEditable = node.isEditable,
                isCurrencyRelated = isCurrency,
                isActionTarget = hasActionKeyword || (node.isClickable && (isCurrency || score >= 4f)),
                confidenceScore = score
            )
            outList.add(element)
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            traverseNode(child, outList, textDump)
            child.recycle()
        }
    }
}
