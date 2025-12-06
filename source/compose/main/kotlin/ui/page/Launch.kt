package cn.yurin.minecraft_composable_launcher.ui.page

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.yurin.minecraft_composable_launcher.ui.localization.*
import cn.yurin.minecraft_composable_launcher.ui.page.launch.VersionSelectSidebar

@Composable
context(_: Context)
fun LaunchPage() {
	var currentPage by remember { mutableIntStateOf(0) }
	AnimatedContent(currentPage) { page ->
		when (page) {
			0 -> dest(LaunchPageDest) {
				Row {
					Sidebar(
						onLaunchClick = {

						},
						onVersionSelectClick = { currentPage = 1 },
						onSettingClick = { currentPage = 2 },
					)
					Spacer(
						modifier = Modifier
							.weight(0.7F),
					)
				}
			}

			1 -> dest(LaunchPageDest.VersionSelectPage) {
				Row {
					VersionSelectSidebar(
						onBack = { currentPage = 0 },
					)
					Spacer(
						modifier = Modifier
							.weight(0.7F),
					)
				}
			}
		}
	}
}

@Composable
context(_: Context)
private fun RowScope.Sidebar(
	onLaunchClick: () -> Unit,
	onVersionSelectClick: () -> Unit,
	onSettingClick: () -> Unit,
) = dest(LaunchPageDest.SideBar) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.3F)
			.background(MaterialTheme.colorScheme.surfaceContainerHighest)
			.padding(32.dp)
	) {
		var selection by remember { mutableIntStateOf(0) }
		val pages = listOf(
			online,
			offline
		)
		Box(
			contentAlignment = Alignment.TopCenter,
			modifier = Modifier.weight(1F),
		) {
			SingleChoiceSegmentedButtonRow(
				space = 8.dp,
			) {
				pages.forEachIndexed { index, page ->
					SegmentedButton(
						selected = selection == index,
						onClick = { selection = index },
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
									when (selection == index) {
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
		}
		AnimatedContent(
			targetState = selection,
			transitionSpec = {
				slideIn(tween()) { IntOffset((targetState compareTo initialState) * it.width, 0) } togetherWith
						slideOut(tween()) { IntOffset((initialState compareTo targetState) * it.width, 0) }
			},
			modifier = Modifier.fillMaxWidth()
		) {
			when (it) {
				0 -> Box(
					contentAlignment = Alignment.Center,
					modifier = Modifier.weight(1F),
				) {
					Text(
						text = "Online User",
						color = MaterialTheme.colorScheme.onSurface,
						style = MaterialTheme.typography.bodyLarge,
					)
				}

				else -> Box(
					contentAlignment = Alignment.Center,
					modifier = Modifier.weight(1F),
				) {
					Text(
						text = "Offline User",
						color = MaterialTheme.colorScheme.onSurface,
						style = MaterialTheme.typography.bodyLarge,
					)
				}
			}
		}
		Box(
			contentAlignment = Alignment.BottomCenter,
			modifier = Modifier.weight(1F),
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp),
			) {
				FilledTonalButton(
					onClick = onLaunchClick,
					colors = ButtonDefaults.filledTonalButtonColors(
						containerColor = MaterialTheme.colorScheme.primary,
						contentColor = MaterialTheme.colorScheme.onPrimary,
					),
					modifier = Modifier.fillMaxWidth(),
				) {
					Text(
						text = launch.current,
						color = MaterialTheme.colorScheme.onPrimary,
						style = MaterialTheme.typography.bodyLarge,
					)
				}
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					modifier = Modifier.fillMaxWidth(),
				) {
					FilledTonalButton(
						onClick = onVersionSelectClick,
						colors = ButtonDefaults.filledTonalButtonColors(
							containerColor = MaterialTheme.colorScheme.secondary,
							contentColor = MaterialTheme.colorScheme.onSecondary,
						),
						modifier = Modifier.weight(1F),
					) {
						Text(
							text = versions.current,
							color = MaterialTheme.colorScheme.onSecondary,
							style = MaterialTheme.typography.bodyLarge,
						)
					}
					FilledTonalButton(
						onClick = onSettingClick,
						colors = ButtonDefaults.filledTonalButtonColors(
							containerColor = MaterialTheme.colorScheme.secondary,
							contentColor = MaterialTheme.colorScheme.onSecondary,
						),
						modifier = Modifier.weight(1F),
					) {
						Text(
							text = settings.current,
							color = MaterialTheme.colorScheme.onSecondary,
							style = MaterialTheme.typography.bodyLarge,
						)
					}
				}
			}
		}
	}
}