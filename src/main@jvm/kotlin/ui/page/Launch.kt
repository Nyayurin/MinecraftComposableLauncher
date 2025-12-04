package cn.yurin.minecraft_composable_launcher.ui.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.yurin.minecraft_composable_launcher.ui.localization.Context
import cn.yurin.minecraft_composable_launcher.ui.localization.LaunchPageDest
import cn.yurin.minecraft_composable_launcher.ui.localization.current
import cn.yurin.minecraft_composable_launcher.ui.localization.dest
import cn.yurin.minecraft_composable_launcher.ui.localization.launch
import cn.yurin.minecraft_composable_launcher.ui.localization.offline
import cn.yurin.minecraft_composable_launcher.ui.localization.online
import cn.yurin.minecraft_composable_launcher.ui.localization.settings
import cn.yurin.minecraft_composable_launcher.ui.localization.versions

@Composable
context(_: Context)
fun LaunchPage() = dest(LaunchPageDest) {
	Row {
		Sidebar()
		Spacer(
			modifier = Modifier
				.weight(0.7F),
		)
	}
}

@Composable
context(_: Context)
private fun RowScope.Sidebar() = dest(LaunchPageDest.SideBar) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.3F)
			.background(MaterialTheme.colorScheme.surfaceContainerHighest)
			.padding(32.dp)
	) {
		var selection by remember { mutableIntStateOf(0) }
		val pages = listOf(online, offline)
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
					onClick = {},
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
						onClick = {},
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
						onClick = {},
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