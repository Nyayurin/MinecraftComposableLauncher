package cn.yurin.mcl

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import cn.yurin.mcl.ui.App
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import kotlinx.coroutines.launch
import java.awt.Dimension
import java.awt.Frame

fun main() = application {
	val state = rememberWindowState(
		position = WindowPosition.Aligned(Alignment.Center),
		size = DpSize(700.dp, 400.dp),
	)
	val scope = rememberCoroutineScope()
	var windowScale by remember { mutableFloatStateOf(0.75F) }
	var windowAlpha by remember { mutableFloatStateOf(0F) }
	Window(
		onCloseRequest = ::exitApplication,
		state = state,
		title = "Minecraft Composable Launcher",
		undecorated = true,
		transparent = true,
	) {
		suspend fun animateWindow(reverse: Boolean) {
			val anim = Animatable(0F)
			anim.animateTo(
				targetValue = 1F,
				animationSpec = tween(200),
			) {
				val value = when (reverse) {
					true -> 1 - value
					else -> value
				}
				windowScale = value * 0.25F + 0.75F
				windowAlpha = value
			}
		}
		LaunchedEffect(Unit) {
			scope.launch {
				animateWindow(false)
			}
			window.addWindowStateListener {
				if (window.state == Frame.ICONIFIED) {
					windowScale = 0.75F
					windowAlpha = 0F
				}
			}
			window.addWindowStateListener { e ->
				val wasMinimized = e.oldState == Frame.ICONIFIED
				val nowNormal = e.newState == Frame.NORMAL
				if (wasMinimized && nowNormal) {
					scope.launch {
						animateWindow(false)
					}
				}
			}
		}
		setMinimumSize(600.dp, 350.dp)
		window.disableMaximize()
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(8.dp),
		) {
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
						animateWindow(true)
						exitApplication()
					}
				},
				minimizeWindow = {
					scope.launch {
						animateWindow(true)
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

fun ComposeWindow.disableMaximize() {
	if (!System.getProperty("os.name").lowercase().contains("win")) return
	val hwnd = WinDef.HWND(Pointer.createConstant(windowHandle))

	val style = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_STYLE)

	// 去掉最大化按钮 和 调整大小框架（可选）
	val newStyle = style and WinUser.WS_MAXIMIZEBOX.inv() and WinUser.WS_THICKFRAME.inv()

	User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_STYLE, newStyle)

	// 让 Windows 更新菜单和行为
	User32.INSTANCE.SetWindowPos(
		hwnd,
		null,
		0, 0, 0, 0,
		WinUser.SWP_NOMOVE or
				WinUser.SWP_NOSIZE or
				WinUser.SWP_NOZORDER or
				WinUser.SWP_FRAMECHANGED
	)
}