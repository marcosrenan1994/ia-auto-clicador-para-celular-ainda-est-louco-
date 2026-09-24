package com.example.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base AndroidViewModel providing lifecycle-aware Application context,
 * robust StateFlow UI state management, single-event effects channel,
 * and coroutine execution utilities separated from Compose UI components.
 *
 * @param application The Application instance for context-dependent operations.
 * @param initialState The initial UI state for the ViewModel.
 */
abstract class BaseAndroidViewModel<UiState, UiEffect>(
    application: Application,
    initialState: UiState
) : AndroidViewModel(application) {

    /**
     * Backing mutable state flow for UI state updates.
     */
    private val _uiState = MutableStateFlow(initialState)

    /**
     * Public read-only StateFlow exposed to Jetpack Compose UI.
     */
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Internal channel for single-time UI events/effects (e.g. snackbars, navigation, alerts).
     */
    private val _uiEffect = Channel<UiEffect>(Channel.BUFFERED)

    /**
     * Public Flow for collecting one-off UI effects in Compose.
     */
    val uiEffect = _uiEffect.receiveAsFlow()

    /**
     * Current snapshot value of UI state.
     */
    protected val currentState: UiState
        get() = _uiState.value

    /**
     * Atomically update the current UI state using a reducer lambda.
     */
    protected fun updateState(reducer: UiState.() -> UiState) {
        _uiState.update(reducer)
    }

    /**
     * Emit a single-time UI side effect to the UI layer.
     */
    protected fun sendEffect(effect: UiEffect) {
        viewModelScope.launch {
            _uiEffect.send(effect)
        }
    }

    /**
     * Convenience wrapper to launch a coroutine with automatic loading and error state handlers.
     */
    protected fun launchWithLoading(
        onLoading: (Boolean) -> Unit = {},
        onError: (Throwable) -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch {
            try {
                onLoading(true)
                block()
            } catch (t: Throwable) {
                onError(t)
            } finally {
                onLoading(false)
            }
        }
    }
}
