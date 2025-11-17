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
import cn.yurin.minecraft_composable_launcher.localization.initContext
import kotlinx.coroutines.launch
import java.awt.Dimension

fun main() = context(initContext()) {
	application {
		val state = rememberWindowState(
			position = WindowPosition.Aligned(Alignment.Center),
			size = DpSize(1000.dp, 600.dp),
		)
		val scope = rememberCoroutineScope()
		var windowScale by remember { mutableFloatStateOf(0F) }
		Window(
			onCloseRequest = ::exitApplication,
			state = state,
			title = "Minecraft Composable Launcher",
			undecorated = true,
			transparent = true,
		) {
			LaunchedEffect(state.isMinimized) {
				if (!state.isMinimized) {
					scope.launch {
						val anim = Animatable(0F)
						anim.animateTo(
							targetValue = 1F,
							animationSpec = tween(),
						) {
							windowScale = value
						}
					}
				}
			}
			setMinimumSize(800.dp, 400.dp)
			App(
				windowScale = animateFloatAsState(windowScale).value,
				windowDraggableArea = {
					WindowDraggableArea {
						it()
					}
				},
				exitApplication = {
					scope.launch {
						val anim = Animatable(1F)
						anim.animateTo(
							targetValue = 0F,
							animationSpec = tween(),
						) {
							windowScale = value
						}
						exitApplication()
					}
				},
				minimizeWindow = {
					scope.launch {
						val anim = Animatable(1F)
						anim.animateTo(
							targetValue = 0F,
							animationSpec = tween(),
						) {
							windowScale = value
						}
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
		window.minimumSize = with(density) {
			Dimension(width.toPx().toInt(), height.toPx().toInt())
		}
	}
}