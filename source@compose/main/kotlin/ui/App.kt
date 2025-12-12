package cn.yurin.mcl.ui

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import cn.yurin.mcl.core.Data
import cn.yurin.mcl.core.refreshVersionsManifest
import cn.yurin.mcl.ui.localization.initContext
import cn.yurin.mcl.ui.page.Home
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun App(
	windowScale: Float,
	windowAlpha: Float,
	windowDraggableArea: @Composable (@Composable () -> Unit) -> Unit,
	exitApplication: () -> Unit,
	minimizeWindow: () -> Unit,
) = context(remember { initContext() }, remember { Data() }) {
	val data = contextOf<Data>()
	LaunchedEffect(Unit) {
		refreshVersionsManifest()
	}
	val scrollbarStyle = if (data.isDarkMode ?: isSystemInDarkTheme()) darkScrollbarStyle() else lightScrollbarStyle()
	CompositionLocalProvider(
		LocalScrollbarStyle provides scrollbarStyle,
	) {
		Theme(
			seedColor = data.seedColor,
			isDark = data.isDarkMode ?: isSystemInDarkTheme(),
		) {
			Surface(
				color = MaterialTheme.colorScheme.background,
				modifier = Modifier
					.fillMaxSize()
					.scale(windowScale)
					.alpha(windowAlpha)
					.clip(RoundedCornerShape(8.dp)),
			) {
				Column {
					Home(
						windowDraggableArea = windowDraggableArea,
						exitApplication = exitApplication,
						minimizeWindow = minimizeWindow,
					)
				}
			}
		}
	}
}