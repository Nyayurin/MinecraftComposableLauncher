package cn.yurin.mcl.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.yurin.mcl.core.Data
import cn.yurin.mcl.core.refreshVersionsManifest
import cn.yurin.mcl.ui.localization.*
import cn.yurin.mcl.ui.page.*
import cn.yurin.minecraftcomposablelauncher.generated.resources.Res
import cn.yurin.minecraftcomposablelauncher.generated.resources.close_24px
import cn.yurin.minecraftcomposablelauncher.generated.resources.minimize_24px
import org.jetbrains.compose.resources.painterResource
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
							slideIn(tween()) {
								IntOffset((targetState compareTo initialState) * it.width, 0)
							} togetherWith slideOut(tween()) {
								IntOffset((initialState compareTo targetState) * it.width, 0)
							}
						},
					) {
						when (it) {
							0 -> LaunchPage(
								onChangeToAccountPage = { page = 1 }
							)
							1 -> AccountsPage()
							2 -> DownloadsPage()
							3 -> SettingsPage()
							4 -> MorePage()
						}
					}
				}
			}
		}
	}
}

@Composable
context(context: Context, _: Data)
fun TopBar(
	currentPage: Int,
	onPageChanges: (Int) -> Unit,
	windowDraggableArea: @Composable (@Composable () -> Unit) -> Unit,
	exitApplication: () -> Unit,
	minimizeWindow: () -> Unit,
) = dest(TopbarDest) {
	windowDraggableArea {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.primaryContainer)
				.padding(8.dp),
		) {
			Text(
				text = "MCL",
				color = MaterialTheme.colorScheme.onPrimaryContainer,
				style = MaterialTheme.typography.titleLarge,
				modifier = Modifier
					.padding(start = 8.dp)
					.align(Alignment.CenterStart),
			)
			val pages = listOf(launch, accounts, downloads, settings, more)
			SingleChoiceSegmentedButtonRow(
				space = 8.dp,
				modifier = Modifier.align(Alignment.Center),
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
							AnimatedContent(context.language) {
								Text(
									text = page.language(it),
									color = animateColorAsState(
										when (currentPage == index) {
											true -> MaterialTheme.colorScheme.onPrimary
											else -> MaterialTheme.colorScheme.onPrimaryContainer
										}
									).value,
									style = MaterialTheme.typography.titleSmall,
								)
							}
						}
					)
				}
			}
			Row(
				horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
				modifier = Modifier.align(Alignment.CenterEnd),
			) {
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
		}
	}
}