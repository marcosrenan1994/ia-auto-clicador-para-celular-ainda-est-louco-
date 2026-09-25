package com.example.data.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Commands recognized from the user's voice input.
 */
enum class VoiceCommandAction(val commandTag: String, val spokenFeedback: String) {
    STOP_ALL("ia parar", "Automação interrompida imediatamente! Estou completamente parado, mestre."),
    START_GAME("ia jogar esse jogo", "Iniciando modo de jogo! Alvos e cadência gamer ativados."),
    START_CLICKER("ia iniciar", "Iniciando autoclicker agora."),
    PAUSE_CLICKER("ia pausar", "Autoclicker pausado com segurança."),
    MAX_SPEED_1000("ia velocidade mil", "Potência máxima! Definido para 1000 cliques por segundo."),
    SWITCH_CAMERA("ia trocar camera", "Alternando câmera ao vivo."),
    LIVE_STATUS("ia status", "Sistemas operando normalmente. Escutando seus comandos 24 horas por dia.")
}

/**
 * Live 24/7 Voice Recognition and Audible Text-To-Speech (TTS) Engine.
 * Enables true voice control ("ia parar", "ia jogar esse jogo") with audible vocal response.
 */
class VoiceLiveAssistantEngine(private val context: Context) {

    companion object {
        private const val TAG = "VoiceLiveAssistant"
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isListening = MutableStateFlow(false)
    val isListening = _isListening.asStateFlow()

    private val _lastHeardText = MutableStateFlow("")
    val lastHeardText = _lastHeardText.asStateFlow()

    private val _lastVoiceResponse = MutableStateFlow("Assistente de Voz Pronto.")
    val lastVoiceResponse = _lastVoiceResponse.asStateFlow()

    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady = _isTtsReady.asStateFlow()

    private val _audioEnergyLevel = MutableStateFlow(0f)
    val audioEnergyLevel = _audioEnergyLevel.asStateFlow()

    var onCommandTriggered: ((VoiceCommandAction) -> Unit)? = null
    var isAudibleVoiceEnabled: Boolean = true

    init {
        initTts()
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("pt", "BR"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.setLanguage(Locale.getDefault())
                }
                textToSpeech?.setSpeechRate(1.05f)
                textToSpeech?.setPitch(1.0f)
                _isTtsReady.value = true
                Log.i(TAG, "Audible Text-to-Speech initialized with Portuguese voice.")
            } else {
                Log.w(TAG, "Failed to initialize Text-to-Speech.")
            }
        }
    }

    /**
     * Speaks the given message aloud with a loud, clear, audible voice.
     */
    fun speakAloud(message: String) {
        _lastVoiceResponse.value = message
        if (!isAudibleVoiceEnabled || !_isTtsReady.value) return

        mainHandler.post {
            try {
                textToSpeech?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "tts_response_${System.currentTimeMillis()}")
            } catch (e: Exception) {
                Log.e(TAG, "Error speaking text", e)
            }
        }
    }

    /**
     * Starts continuous 24/7 speech recognition listening loop.
     */
    fun startListening247() {
        mainHandler.post {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                Log.w(TAG, "Speech recognition is not available on this device.")
                _lastHeardText.value = "Reconhecimento de voz indisponível"
                return@post
            }

            stopListening()

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createRecognitionListener())
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "pt-BR")
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }

            try {
                speechRecognizer?.startListening(intent)
                _isListening.value = true
                Log.i(TAG, "Continuous 24/7 Voice Recognition started.")
            } catch (e: Exception) {
                Log.e(TAG, "Error starting speech recognition", e)
                _isListening.value = false
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                _isListening.value = false
                speechRecognizer?.stopListening()
                speechRecognizer?.cancel()
                speechRecognizer?.destroy()
                speechRecognizer = null
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping speech recognizer", e)
            }
        }
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {
                _audioEnergyLevel.value = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                Log.d(TAG, "SpeechRecognizer error: $error")
                // Re-arm listener automatically if 24/7 mode is on
                if (_isListening.value) {
                    mainHandler.postDelayed({
                        if (_isListening.value) startListening247()
                    }, 500L)
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val heardText = matches?.firstOrNull()?.lowercase()?.trim() ?: ""
                if (heardText.isNotBlank()) {
                    _lastHeardText.value = heardText
                    evaluateVoiceCommand(heardText)
                }

                // Re-arm listener for continuous 24/7 listening
                if (_isListening.value) {
                    mainHandler.postDelayed({
                        if (_isListening.value) startListening247()
                    }, 350L)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partial = matches?.firstOrNull()?.lowercase()?.trim() ?: ""
                if (partial.isNotBlank()) {
                    _lastHeardText.value = partial
                    // Fast kill-switch: if user shouts "parar", stop IMMEDIATELY on partial result!
                    if (partial.contains("parar") || partial.contains("pare") || partial.contains("stop")) {
                        evaluateVoiceCommand(partial)
                    }
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    /**
     * Evaluates heard voice text and triggers matching actions with audible feedback.
     */
    fun evaluateVoiceCommand(heardText: String) {
        val normalized = heardText.lowercase().trim()

        val action = when {
            normalized.contains("parar") || normalized.contains("pare") || normalized.contains("stop") || normalized.contains("cancelar") -> {
                VoiceCommandAction.STOP_ALL
            }
            normalized.contains("jogar") || normalized.contains("jogo") || normalized.contains("game") -> {
                VoiceCommandAction.START_GAME
            }
            normalized.contains("iniciar") || normalized.contains("começar") || normalized.contains("continuar") -> {
                VoiceCommandAction.START_CLICKER
            }
            normalized.contains("pausar") || normalized.contains("espera") -> {
                VoiceCommandAction.PAUSE_CLICKER
            }
            normalized.contains("velocidade mil") || normalized.contains("turbo") || normalized.contains("mil cps") -> {
                VoiceCommandAction.MAX_SPEED_1000
            }
            normalized.contains("camera") || normalized.contains("trocar camera") || normalized.contains("virar camera") -> {
                VoiceCommandAction.SWITCH_CAMERA
            }
            normalized.contains("status") || normalized.contains("como você está") -> {
                VoiceCommandAction.LIVE_STATUS
            }
            else -> null
        }

        if (action != null) {
            speakAloud(action.spokenFeedback)
            onCommandTriggered?.invoke(action)
        }
    }

    fun destroy() {
        stopListening()
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying TTS", e)
        }
    }
}
