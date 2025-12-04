package cn.yurin.minecraft_composable_launcher

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import cn.yurin.minecraft_composable_launcher.ui.localization.initContext
import cn.yurin.minecraft_composable_launcher.ui.App
import kotlinx.coroutines.launch
import java.awt.Dimension
import java.awt.Frame

fun main() = context(initContext()) {
	application {
		val state = rememberWindowState(
			position = WindowPosition.Aligned(Alignment.Center),
			size = DpSize(1000.dp, 600.dp),
		)
		val scope = rememberCoroutineScope()
		var windowScale by remember { mutableFloatStateOf(0F) }
		var windowAlpha by remember { mutableFloatStateOf(0F) }
		Window(
			onCloseRequest = ::exitApplication,
			state = state,
			title = "Minecraft Composable Launcher",
			undecorated = true,
			transparent = true,
		) {
			suspend fun animateWindow(reverse: Boolean) {
				val anim = Animatable(
					when (reverse) {
						true -> 0F
						else -> 1F
					}
				)
				anim.animateTo(
					targetValue = when (reverse) {
						true -> 1F
						else -> 0F
					},
					animationSpec = tween(200),
				) {
					windowScale = value * 0.25F + 0.75F
					windowAlpha = value
				}
			}
			LaunchedEffect(Unit) {
				window.addWindowStateListener {
					if (window.state == Frame.ICONIFIED) {
						windowScale = .75F
						windowAlpha = 0F
					}
				}
			}
			LaunchedEffect(state.isMinimized) {
				if (!state.isMinimized) {
					scope.launch {
						animateWindow(true)
					}
				}
			}
			setMinimumSize(800.dp, 450.dp)
			App(
				windowScale = animateFloatAsState(windowScale).value,
				windowAlpha = animateFloatAsState(windowAlpha).value,
				windowDraggableArea = {
					WindowDraggableArea {
						it()
					}
				},
				exitApplication = {
					scope.launch {
						animateWindow(false)
						exitApplication()
					}
				},
				minimizeWindow = {
					scope.launch {
						animateWindow(false)
						state.isMinimized = true
					}
				}
			)
		}
	}
}

@Composable
fun FrameWindowScope.setMinimumSize(
	width: Dp = Dp.Unspecified,
	height: Dp = Dp.Unspecified,
) {
	val density = LocalDensity.current
	LaunchedEffect(density) {
		window.minimumSize = Dimension(width.value.toInt(), height.value.toInt())
	}
}