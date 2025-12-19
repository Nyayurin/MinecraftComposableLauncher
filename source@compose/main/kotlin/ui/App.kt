package cn.yurin.mcl.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import cn.yurin.mcl.core.Data
import cn.yurin.mcl.core.refreshVersionsManifest
import cn.yurin.mcl.storage.readData
import cn.yurin.mcl.storage.saveContext
import cn.yurin.mcl.storage.saveData
import cn.yurin.mcl.ui.localization.initContext
import cn.yurin.mcl.ui.neo.page.Home
import io.github.iamcalledrob.smoothRoundedCornerShape.SmoothRoundedCornerShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun App(
	windowScale: Float,
	windowAlpha: Float,
	windowDraggableArea: @Composable (@Composable () -> Unit) -> Unit,
	exitApplication: () -> Unit,
	minimizeWindow: () -> Unit,
) = context(remember { initContext() }, remember { readData() }) {
	val data = contextOf<Data>()
	LaunchedEffect(Unit) {
		refreshVersionsManifest()
		data.scope.launch(Dispatchers.IO) {
			while (true) {
				delay(5000L)
				saveContext()
				saveData()
			}
		}
	}
	val scrollbarStyle = if (data.isDarkMode) darkScrollbarStyle() else lightScrollbarStyle()
	CompositionLocalProvider(
		LocalScrollbarStyle provides scrollbarStyle,
	) {
		Theme(
			seedColor = data.seedColor,
			isDark = data.isDarkMode,
			isExpressive = data.isExpressive,
		) {
			Surface(
				color = MaterialTheme.colorScheme.background,
				modifier = Modifier
					.fillMaxSize()
					.scale(windowScale)
					.alpha(windowAlpha)
					.clip(SmoothRoundedCornerShape(radius = 24.dp)),
			) {
				Box {
					Home(
						windowDraggableArea = windowDraggableArea,
						exitApplication = {
							saveContext()
							saveData()
							exitApplication()
						},
						minimizeWindow = {
							saveContext()
							saveData()
							minimizeWindow()
						},
					)
					AnimatedVisibility(
						visible = data.dialogProvider != null,
						enter = fadeIn(),
						exit = fadeOut(),
					) {
						var dialogProvider by remember { mutableStateOf(data.dialogProvider!!) }
						remember(data.dialogProvider) {
							data.dialogProvider?.let { dialogProvider = it }
						}
						val dialog = dialogProvider()
						Surface(
							color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
							modifier = Modifier
								.fillMaxSize()
								.clickable(
									indication = null,
									interactionSource = remember { MutableInteractionSource() },
									onClick = dialog.onDismissRequest
								),
						) {
							Surface(
								shape = SmoothRoundedCornerShape(radius = 32.dp),
								color = MaterialTheme.colorScheme.surfaceContainer,
								modifier = Modifier
									.wrapContentSize()
									.widthIn(min = 280.dp, max = 560.dp)
									.clickable(
										indication = null,
										interactionSource = remember { MutableInteractionSource() }
									) { },
							) {
								Column(
									verticalArrangement = Arrangement.spacedBy(16.dp),
									modifier = Modifier.padding(24.dp),
								) {
									Row {
										dialog.icon?.invoke()
										dialog.title?.invoke()
									}
									dialog.content?.invoke()
									Row(
										horizontalArrangement = Arrangement.spacedBy(8.dp),
										modifier = Modifier.align(Alignment.End),
									) {
										dialog.confirmButton()
										dialog.dismissButton?.invoke()
									}
								}
							}
						}
					}
				}
			}
		}
	}
}