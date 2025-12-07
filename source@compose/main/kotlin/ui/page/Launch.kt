package cn.yurin.minecraft_composable_launcher.ui.page

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.yurin.minecraft_composable_launcher.core.Data
import cn.yurin.minecraft_composable_launcher.core.currentFolder
import cn.yurin.minecraft_composable_launcher.core.currentVersion
import cn.yurin.minecraft_composable_launcher.ui.localization.*
import cn.yurin.minecraft_composable_launcher.ui.page.launch.VersionSelectPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.File
import java.lang.System

@Composable
context(_: Context, _: Data)
fun LaunchPage() {
	val scope = remember { CoroutineScope(Dispatchers.IO) }
	var currentPage by remember { mutableIntStateOf(0) }
	AnimatedContent(
		targetState = currentPage,
		transitionSpec = {
			slideIn(tween()) {
				IntOffset(0, (targetState compareTo initialState) * it.height)
			} togetherWith slideOut(tween()) {
				IntOffset(0, (initialState compareTo targetState) * it.height)
			}
		},
	) { page ->
		when (page) {
			0 -> dest(LaunchPageDest) {
				Row {
					Sidebar(
						onLaunchClick = {
							when (currentVersion) {
								null -> currentPage = 1
								else -> scope.launch(Dispatchers.IO) {
									val currentVersion = currentVersion!!
									val libraries = listOf(
										*currentVersion.manifest.libraries.filter { library ->
											when (library.rule?.os?.name) {
												null -> true
												in System.getProperty("os.name").lowercase() -> true
												else -> false
											}
										}.mapNotNull { library ->
											library.downloads?.artifact?.path?.let { path ->
												File("${currentFolder!!.path}/libraries", path).absolutePath
											}
										}.toTypedArray(),
										File(currentVersion.path, "${currentVersion.name}.jar").absolutePath
									)
									val process = ProcessBuilder(
										"java",
										"-Dminecraft.launcher.brand=MinecraftComposableLauncher",
										"-Dminecraft.launcher.version=1.0.0",
										"-cp",
										libraries.joinToString(";"),
										currentVersion.manifest.mainClass,
										"--gameDir",
										currentFolder!!.path,
										"--assetsDir",
										"${currentFolder!!.path}/assets",
										"--assetIndex",
										currentVersion.manifest.assetIndex.id,
										"--uuid",
										"83e0c8867af43b2a8eedcede1fd64ce0",
										"--accessToken",
										"8a8bf5456a914d1bb73ec634b260a385",
										"--version",
										currentVersion.manifest.id,
									).start()
									val error = scope.async(Dispatchers.IO) {
										while (process.isAlive) {
											process.errorReader().readLine()?.let(System.err::println)
										}
									}
									val input = scope.async(Dispatchers.IO) {
										while (process.isAlive) {
											process.inputReader().readLine()?.let(::println)
										}
									}
									awaitAll(input, error)
									println("Process done")
								}
							}
						},
						onVersionSelectClick = {
							currentPage = 1
						},
						onSettingClick = { currentPage = 2 },
					)
					Spacer(
						modifier = Modifier
							.weight(0.7F),
					)
				}
			}

			1 -> VersionSelectPage(
				onBack = { currentPage = 0 }
			)
		}
	}
}

@Composable
context(_: Context, _: Data)
private fun RowScope.Sidebar(
	onLaunchClick: () -> Unit,
	onVersionSelectClick: () -> Unit,
	onSettingClick: () -> Unit,
) = dest(LaunchPageDest.SideBar) {
	Box(
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
			modifier = Modifier.align(Alignment.TopCenter),
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
			modifier = Modifier
				.fillMaxWidth()
				.align(Alignment.Center),
		) {
			when (it) {
				0 -> Box(
					contentAlignment = Alignment.Center,
				) {
					Text(
						text = "Online User",
						color = MaterialTheme.colorScheme.onSurface,
						style = MaterialTheme.typography.bodyLarge,
					)
				}

				else -> Box(
					contentAlignment = Alignment.Center,
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
			modifier = Modifier.align(Alignment.BottomCenter),
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp),
			) {
				FilledTonalButton(
					onClick = onLaunchClick,
					shape = RoundedCornerShape(12.dp),
					colors = ButtonDefaults.filledTonalButtonColors(
						containerColor = MaterialTheme.colorScheme.primary,
						contentColor = MaterialTheme.colorScheme.onPrimary,
					),
					modifier = Modifier.fillMaxWidth(),
				) {
					Column(
						horizontalAlignment = Alignment.CenterHorizontally,
					) {
						Text(
							text = launch.current,
							color = MaterialTheme.colorScheme.onPrimary,
							style = MaterialTheme.typography.bodyLarge,
						)
						Text(
							text = currentVersion?.manifest?.id ?: selectVersion.current,
							color = MaterialTheme.colorScheme.onPrimary,
							style = MaterialTheme.typography.bodySmall,
						)
					}
				}
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					modifier = Modifier.fillMaxWidth(),
				) {
					FilledTonalButton(
						onClick = onVersionSelectClick,
						shape = RoundedCornerShape(12.dp),
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
						shape = RoundedCornerShape(12.dp),
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