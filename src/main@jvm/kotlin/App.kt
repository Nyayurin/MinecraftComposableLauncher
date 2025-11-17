package cn.yurin.minecraft_composable_launcher

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cn.yurin.minecraft_composable_launcher.localization.*
import cn.yurin.minecraft_composable_launcher.page.LaunchPage
import cn.yurin.minecraftcomposablelauncher.generated.resources.Res
import cn.yurin.minecraftcomposablelauncher.generated.resources.close_24px
import cn.yurin.minecraftcomposablelauncher.generated.resources.colors_24px
import cn.yurin.minecraftcomposablelauncher.generated.resources.minimize_24px
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import org.jetbrains.compose.resources.painterResource

var seedColor by mutableStateOf(Color(0xFF9B9D95))
var isDarkMode by mutableStateOf<Boolean?>(null)

@Composable
context(context: Context)
fun App(
	windowScale: Float,
	windowDraggableArea: @Composable (@Composable () -> Unit) -> Unit,
	exitApplication: () -> Unit,
	minimizeWindow: () -> Unit,
) {
	Theme(
		seedColor = seedColor,
		isDark = isDarkMode ?: isSystemInDarkTheme(),
	) {
		Surface(
			color = MaterialTheme.colorScheme.background,
			modifier = Modifier
				.fillMaxSize()
				.scale(windowScale)
				.clip(RoundedCornerShape(8.dp)),
		) {
			Column {
				var page by remember { mutableStateOf(0) }
				TopBar(
					currentPage = page,
					onPageChanges = { page = it },
					windowDraggableArea = windowDraggableArea,
					exitApplication = exitApplication,
					minimizeWindow = minimizeWindow,
				)
				AnimatedContent(
					targetState = page,
					transitionSpec = {
						slideIn(tween()) { IntOffset((targetState.compareTo(initialState)) * it.width, 0) } togetherWith
								slideOut(tween()) { IntOffset((initialState.compareTo(targetState)) * it.width, 0) }
					},
				) {
					when (it) {
						0 -> LaunchPage()
					}
				}
			}
		}
	}
}

@Composable
context(context: Context)
fun TopBar(
	currentPage: Int,
	onPageChanges: (Int) -> Unit,
	windowDraggableArea: @Composable (@Composable () -> Unit) -> Unit,
	exitApplication: () -> Unit,
	minimizeWindow: () -> Unit,
) = dest(TopbarDest) {
	windowDraggableArea {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.primaryContainer)
				.padding(16.dp),
		) {
			Text(
				text = "MCL",
				color = MaterialTheme.colorScheme.onPrimaryContainer,
				style = MaterialTheme.typography.titleLarge,
				modifier = Modifier.weight(0.5F),
			)
			val pages = listOf(launch, downloads, settings, more)
			SingleChoiceSegmentedButtonRow(
				space = 8.dp,
				modifier = Modifier.weight(1F),
			) {
				pages.forEachIndexed { index, page ->
					SegmentedButton(
						selected = currentPage == index,
						onClick = { onPageChanges(index) },
						shape = SegmentedButtonDefaults.itemShape(
							index = index,
							count = pages.size
						),
						colors = SegmentedButtonDefaults.colors(
							activeContainerColor = MaterialTheme.colorScheme.primary,
							activeContentColor = MaterialTheme.colorScheme.onPrimary,
							activeBorderColor = MaterialTheme.colorScheme.primary,
							inactiveContainerColor = MaterialTheme.colorScheme.primaryContainer,
							inactiveContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
							inactiveBorderColor = MaterialTheme.colorScheme.primary,
						),
						label = {
							Text(
								text = page.current,
								color = animateColorAsState(
									when (currentPage == index) {
										true -> MaterialTheme.colorScheme.onPrimary
										else -> MaterialTheme.colorScheme.onPrimaryContainer
									}
								).value,
								style = MaterialTheme.typography.titleSmall,
							)
						}
					)
				}
			}
			var showColorsDialog by remember { mutableStateOf(false) }
			Row(
				horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
				modifier = Modifier.weight(0.5F),
			) {
				IconButton(
					onClick = { showColorsDialog = true },
				) {
					Icon(
						painter = painterResource(Res.drawable.colors_24px),
						contentDescription = "Colors",
						tint = MaterialTheme.colorScheme.onPrimaryContainer,
					)
				}
				IconButton(
					onClick = minimizeWindow,
				) {
					Icon(
						painter = painterResource(Res.drawable.minimize_24px),
						contentDescription = "Minimize",
						tint = MaterialTheme.colorScheme.onPrimaryContainer,
					)
				}
				IconButton(
					onClick = exitApplication,
				) {
					Icon(
						painter = painterResource(Res.drawable.close_24px),
						contentDescription = "Close",
						tint = MaterialTheme.colorScheme.onPrimaryContainer,
					)
				}
			}
			if (showColorsDialog) {
				Dialog(
					onDismissRequest = { showColorsDialog = false }
				) {
					ColorPicker(
						onColorChanged = { seedColor = it.color },
						initialColor = seedColor,
						onDarkChanged = { isDarkMode = it },
						initialDark = isDarkMode ?: isSystemInDarkTheme(),
					)
				}
			}
		}
	}
}

@Composable
context(_: Context)
fun ColorPicker(
	onColorChanged: (ColorEnvelope) -> Unit,
	initialColor: Color,
	onDarkChanged: (Boolean) -> Unit,
	initialDark: Boolean,
) {
	Surface(
		color = MaterialTheme.colorScheme.surfaceBright,
		shape = RoundedCornerShape(16.dp),
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier.padding(16.dp),
		) {
			val controller = rememberColorPickerController()
			HsvColorPicker(
				controller = controller,
				onColorChanged = onColorChanged,
				initialColor = initialColor,
				modifier = Modifier.size(200.dp),
			)
			BrightnessSlider(
				controller = controller,
				modifier = Modifier
					.width(200.dp)
					.height(20.dp),
			)
			Button(
				onClick = { onDarkChanged(!initialDark) },
				colors = ButtonDefaults.buttonColors(
					containerColor = animateColorAsState(
						when (initialDark) {
							true -> MaterialTheme.colorScheme.primary
							else -> MaterialTheme.colorScheme.onSurface
						}
					).value
				),
			) {
				Text(
					text = "Dark Mode",
					color = animateColorAsState(
						when (initialDark) {
							true -> MaterialTheme.colorScheme.onPrimary
							else -> MaterialTheme.colorScheme.surface
						}
					).value,
					style = MaterialTheme.typography.bodyLarge,
				)
			}
		}
	}
}