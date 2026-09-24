package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.viewmodel.ActionType
import com.example.viewmodel.AutomationStatus
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MainViewModelTest {

  private lateinit var viewModel: MainViewModel

  @Before
  fun setup() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    viewModel = MainViewModel(application)
  }

  @Test
  fun testInitialState() {
    val state = viewModel.uiState.value
    assertEquals(AutomationStatus.IDLE, state.status)
    assertEquals(500L, state.clickIntervalMs)
    assertEquals(50, state.repeatCount)
    assertEquals(0, state.completedClicks)
    assertTrue(state.logs.isNotEmpty())
  }

  @Test
  fun testSetInterval() {
    viewModel.setIntervalMs(1200L)
    assertEquals(1200L, viewModel.uiState.value.clickIntervalMs)
  }

  @Test
  fun testSetActionType() {
    viewModel.setActionType(ActionType.DOUBLE_TAP)
    assertEquals(ActionType.DOUBLE_TAP, viewModel.uiState.value.actionType)
  }

  @Test
  fun testToggleVibration() {
    viewModel.toggleVibration(false)
    assertFalse(viewModel.uiState.value.vibrationEnabled)
  }

  @Test
  fun testManualAction() {
    val initialLifetimeClicks = viewModel.uiState.value.stats.totalClicksAllTime
    viewModel.performManualTest()
    assertEquals(initialLifetimeClicks + 1, viewModel.uiState.value.stats.totalClicksAllTime)
  }

  @Test
  fun testTargetCoordinatesUpdate() {
    viewModel.setTargetCoordinates(720, 1280)
    val state = viewModel.uiState.value
    assertEquals(720, state.targetX)
    assertEquals(1280, state.targetY)
  }

  @Test
  fun testPointerStyleAndVisibility() {
    viewModel.setPointerVisible(false)
    assertFalse(viewModel.uiState.value.isMousePointerVisible)

    viewModel.setPointerVisible(true)
    assertTrue(viewModel.uiState.value.isMousePointerVisible)
  }

  @Test
  fun testJitterToggle() {
    viewModel.toggleJitter(false)
    assertFalse(viewModel.uiState.value.jitterEnabled)

    viewModel.toggleJitter(true)
    assertTrue(viewModel.uiState.value.jitterEnabled)
  }

  @Test
  fun testMicrosoftStoreFeatures() {
    // 1000 CPS test
    viewModel.setTargetCps(1000)
    assertEquals(1000, viewModel.uiState.value.targetCps)

    // Profile cycle test
    val initialProfile = viewModel.uiState.value.selectedProfile
    viewModel.cycleNextProfile()
    val nextProfile = viewModel.uiState.value.selectedProfile
    assertTrue(nextProfile.id != initialProfile.id || viewModel.uiState.value.profiles.size <= 1)

    // Multi-Point addition test
    val initialPoints = viewModel.uiState.value.multiPoints.size
    viewModel.addMultiPoint(400, 600)
    assertEquals(initialPoints + 1, viewModel.uiState.value.multiPoints.size)
    assertTrue(viewModel.uiState.value.multiPointModeActive)

    // Remove multi point
    viewModel.removeLastMultiPoint()
    assertEquals(initialPoints, viewModel.uiState.value.multiPoints.size)
  }
}
