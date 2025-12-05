package cn.yurin.minecraft_composable_launcher.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.yurin.minecraft_composable_launcher.network.VersionsManifest
import cn.yurin.minecraft_composable_launcher.ui.localization.*
import cn.yurin.minecraft_composable_launcher.ui.page.*
import cn.yurin.minecraftcomposablelauncher.generated.resources.Res
import cn.yurin.minecraftcomposablelauncher.generated.resources.close_24px
import cn.yurin.minecraftcomposablelauncher.generated.resources.minimize_24px
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import org.jetbrains.compose.resources.painterResource

var seedColor by mutableStateOf(Color(0xFF9B9D95))
var isDarkMode by mutableStateOf<Boolean?>(null)

val client = HttpClient(CIO) {
	install(ContentNegotiation) {
		json()
	}
}

@Composable
context(_: Context)
fun App(
	windowScale: Float,
	windowAlpha: Float,
	windowDraggableArea: @Composable (@Composable () -> Unit) -> Unit,
	exitApplication: () -> Unit,
	minimizeWindow: () -> Unit,
) {
	LaunchedEffect(Unit) {
		val response = client.get("https://piston-meta.mojang.com/mc/game/version_manifest.json")
		manifest = response.body<VersionsManifest>()
	}
	val scrollbarStyle = if (isDarkMode ?: isSystemInDarkTheme()) darkScrollbarStyle() else lightScrollbarStyle()
	CompositionLocalProvider(
		LocalScrollbarStyle provides scrollbarStyle,
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
								IntOffset(
									(targetState compareTo initialState) * it.width,
									0
								)
							} togetherWith
									slideOut(tween()) { IntOffset((initialState compareTo targetState) * it.width, 0) }
						},
					) {
						when (it) {
							0 -> LaunchPage()
							1 -> DownloadsPage()
							2 -> SettingsPage()
							3 -> MorePage()
						}
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
				modifier = Modifier.weight(0.5F),
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