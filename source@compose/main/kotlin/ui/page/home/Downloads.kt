package cn.yurin.mcl.ui.page.home

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.yurin.mcl.core.*
import cn.yurin.mcl.network.VersionsManifest
import cn.yurin.mcl.ui.DownloadsPage
import cn.yurin.mcl.ui.localization.*
import cn.yurin.mcl.ui.localization.destination.DownloadsDest
import cn.yurin.mcl.ui.localization.destination.confirm
import cn.yurin.mcl.ui.localization.destination.latest
import cn.yurin.mcl.ui.localization.destination.oldAlpha
import cn.yurin.mcl.ui.localization.destination.oldBeta
import cn.yurin.mcl.ui.localization.destination.release
import cn.yurin.mcl.ui.localization.destination.releaseAt
import cn.yurin.mcl.ui.localization.destination.snapshot
import cn.yurin.mcl.ui.localization.destination.titleDownloaded
import cn.yurin.mcl.ui.localization.destination.titleDownloading
import cn.yurin.mcl.ui.localization.destination.vanilla
import cn.yurin.minecraftcomposablelauncher.generated.resources.Res
import cn.yurin.minecraftcomposablelauncher.generated.resources.arrow_drop_up_24px
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.name
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.painterResource

@Composable
context(_: Context, _: Data)
fun Downloads() = dest(DownloadsDest) {
	Row {
		var currentPage by remember { mutableStateOf<DownloadsPage>(DownloadsPage.Vanilla) }
		Sidebar(
			currentPage = currentPage,
			onPageChanged = { currentPage = it },
		)
		Content(
			currentPage = currentPage,
		)
	}
}

@Composable
context(context: Context, data: Data)
private fun RowScope.Sidebar(
	currentPage: DownloadsPage,
	onPageChanged: (DownloadsPage) -> Unit,
) = dest(DownloadsDest.SideBar) {
	val pages = listOf<DownloadsPage>(DownloadsPage.Vanilla)
	NavigationRail(
		containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.15F),
	) {
		pages.forEach { page ->
			NavigationRailItem(
				selected = currentPage == page,
				onClick = {
					if (currentPage == page) {
						data.scope.launch(Dispatchers.IO) {
							refreshVersionsManifest()
						}
					}
					onPageChanged(page)
				},
				icon = {},
				label = {
					AnimatedContent(context.language) {
						Text(
							text = when (page) {
								DownloadsPage.Vanilla -> vanilla
							}.language(it),
							color = MaterialTheme.colorScheme.onSurface,
							style = MaterialTheme.typography.titleSmall,
						)
					}
				}
			)
		}
	}
}

@Composable
context(_: Context, data: Data)
private fun RowScope.Content(
	currentPage: DownloadsPage,
) = dest(DownloadsDest.Content) {
	val launcher = rememberDirectoryPickerLauncher { file ->
		if (file != null) {
			if (!data.folders.any { it.path == file.absolutePath() }) {
				data.folders += GameFolder.DotMinecraft(
					name = file.name,
					path = file.absolutePath(),
					versions = emptyList()
				)
				if (data.currentFolder == null) {
					data.currentFolder = data.folders.first()
				}
				refreshFolders()
				if (data.currentVersion == null) {
					data.currentVersion = data.currentFolder!!.versions.firstOrNull()
				}
			}
		}
	}
	Box(
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.85F),
	) {
		var showDownloadPage by remember { mutableStateOf(false) }
		val downloadList = remember { mutableStateListOf<Pair<String, Pair<Int, Int>>>() }
		var downloadFinished by remember { mutableStateOf(false) }
		val scrollState = rememberScrollState()
		AnimatedContent(
			targetState = currentPage,
			transitionSpec = {
				slideIn(tween()) {
					IntOffset(0, (targetState.position compareTo initialState.position) * it.height)
				} togetherWith slideOut(tween()) {
					IntOffset(0, (initialState.position compareTo targetState.position) * it.height)
				}
			},
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 32.dp),
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(32.dp),
				modifier = Modifier
					.verticalScroll(scrollState),
			) {
				Spacer(modifier = Modifier.height(0.dp))
				when (it) {
					DownloadsPage.Vanilla -> Vanilla(
						onDownloadVersion = { version ->
							if (data.currentFolder == null) {
								launcher.launch()
							} else {
								data.scope.launch(Dispatchers.IO) {
									showDownloadPage = true
									downloadList.clear()
									downloadFinished = false
									println("Downloading version ${version.id}")
									downloadManifest(
										version = version,
										onInitDownloadList = { key, sum ->
											downloadList += key to (sum to 0)
										},
										onDownloaded = { key ->
											val index = downloadList.indexOfFirst { item -> item.first == key }
											val item = downloadList[index]
											downloadList[index] = item.first to (item.second.first to item.second.second + 1)
										},
										onDownloadError = { e ->
											println("Failed to download version ${version.id}: $e")
											e.printStackTrace()
										},
									)?.let { manifest ->
										completeVersion(
											version = version,
											manifest = manifest,
											onInitDownloadList = { key, sum ->
												downloadList += key to (sum to 0)
											},
											onDownloaded = { key ->
												val index = downloadList.indexOfFirst { item -> item.first == key }
												val item = downloadList[index]
												downloadList[index] =
													item.first to (item.second.first to item.second.second + 1)
											},
											onDownloadError = { e ->
												println("Failed to complete version ${version.id}: $e")
												e.printStackTrace()
											},
										)
									}
									downloadFinished = true
									refreshFolders()
								}
							}
						}
					)
				}
				Spacer(modifier = Modifier.height(0.dp))
			}
		}
		VerticalScrollbar(
			adapter = rememberScrollbarAdapter(scrollState),
			modifier = Modifier
				.align(Alignment.CenterEnd)
				.fillMaxHeight(),
		)
		if (showDownloadPage) {
			dest(DownloadsDest.DownloadDialog) {
				AlertDialog(
					onDismissRequest = { showDownloadPage = false },
					title = {
						Text(
							text = when (downloadFinished) {
								true -> titleDownloaded.current
								else -> titleDownloading.current
							},
							color = MaterialTheme.colorScheme.onSurface,
							style = MaterialTheme.typography.titleSmall,
						)
					},
					icon = null,
					confirmButton = {
						TextButton(
							onClick = { showDownloadPage = false }
						) {
							Text(
								text = confirm.current,
								color = MaterialTheme.colorScheme.onSurface,
								style = MaterialTheme.typography.titleSmall,
							)
						}
					},
					dismissButton = null,
					text = {
						Column(
							verticalArrangement = Arrangement.spacedBy(16.dp),
						) {
							downloadList.forEach { (key, value) ->
								val (sum, count) = value
								val percentage = count / sum.toFloat()
								Row(
									verticalAlignment = Alignment.CenterVertically,
									modifier = Modifier.fillMaxWidth(),
								) {
									Box(
										contentAlignment = Alignment.Center,
										modifier = Modifier.size(64.dp),
									) {
										CircularProgressIndicator(
											progress = { percentage },
										)
										Text(
											text = "${(percentage * 100).toInt()}%",
											color = MaterialTheme.colorScheme.onSurface,
											style = MaterialTheme.typography.bodySmall,
										)
									}
									Text(
										text = key,
										color = MaterialTheme.colorScheme.onSurface,
										style = MaterialTheme.typography.titleSmall,
									)
									Spacer(
										modifier = Modifier.weight(1F),
									)
									Text(
										text = "$count / $sum",
										color = MaterialTheme.colorScheme.onSurface,
										style = MaterialTheme.typography.titleSmall,
									)
								}
							}
						}
					},
				)
			}
		}
	}
}

@Composable
context(_: Context, data: Data)
private fun Vanilla(
	onDownloadVersion: (VersionsManifest.Version) -> Unit,
) = dest(DownloadsDest.Content.Vanilla) {
	data.scope.launch(Dispatchers.IO) {
		refreshVersionsManifest()
	}
	AnimatedVisibility(data.versionsManifest != null) {
		var manifest by remember { mutableStateOf(data.versionsManifest!!) }
		remember(data.versionsManifest) {
			data.versionsManifest?.let { manifest = it }
		}
		val versions = manifest.versions.groupBy { version -> version.type }
		Column(
			verticalArrangement = Arrangement.spacedBy(32.dp),
		) {
			Card(
				title = {
					Text(
						text = latest.current,
						color = MaterialTheme.colorScheme.onSurface,
						style = MaterialTheme.typography.headlineSmall,
					)
				},
			) {
				val latestRelease = manifest.versions.find { it.id == manifest.latest.release }!!
				val latestSnapshot = manifest.versions.find { it.id == manifest.latest.snapshot }!!
				VersionItem(
					version = latestRelease,
					detail = { version ->
						buildString {
							append(release.current)
							append(", ")
							append(releaseAt.current)
							append(" ")
							append(localDateTimeFormater.format(LocalDateTime.parse(version.releaseTime, localDateTimeParser)))
						}
					},
					onClick = onDownloadVersion,
				)
				AnimatedVisibility(latestSnapshot.type == "snapshot") {
					VersionItem(
						version = latestSnapshot,
						detail = { version ->
							buildString {
								append(snapshot.current)
								append(", ")
								append(releaseAt.current)
								append(" ")
								append(localDateTimeFormater.format(LocalDateTime.parse(version.releaseTime, localDateTimeParser)))
							}
						},
						onClick = onDownloadVersion,
					)
				}
			}
			listOf(
				release to "release",
				snapshot to "snapshot",
				oldBeta to "old_beta",
				oldAlpha to "old_alpha",
			).forEach { (text, type) ->
				FoldableCard(
					title = {
						Text(
							text = text.current,
							color = MaterialTheme.colorScheme.onSurface,
							style = MaterialTheme.typography.headlineSmall,
						)
					},
				) {
					versions[type]?.forEach { version ->
						VersionItem(
							version = version,
							detail = { version ->
								localDateTimeFormater.format(LocalDateTime.parse(version.releaseTime, localDateTimeParser))
							},
							onClick = onDownloadVersion,
						)
					}
				}
			}
		}
	}
}

@Composable
context(_: Context, _: Data)
private fun Card(
	title: @Composable () -> Unit,
	content: @Composable context(Context) ColumnScope.() -> Unit,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier = Modifier
			.clip(RoundedCornerShape(16.dp))
			.background(MaterialTheme.colorScheme.surfaceContainerHighest)
			.padding(16.dp),
	) {
		title()
		Column(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
		) {
			content()
		}
	}
}

@Composable
context(_: Context, _: Data)
private fun FoldableCard(
	title: @Composable () -> Unit,
	content: @Composable context(Context) ColumnScope.() -> Unit,
) {
	var fold by remember { mutableStateOf(true) }
	Column(
		modifier = Modifier
			.clip(RoundedCornerShape(16.dp))
			.background(MaterialTheme.colorScheme.surfaceContainerHighest)
			.padding(16.dp),
	) {
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth(),
		) {
			title()
			IconButton(
				onClick = { fold = !fold },
			) {
				Icon(
					painter = painterResource(Res.drawable.arrow_drop_up_24px),
					tint = MaterialTheme.colorScheme.onSurface,
					contentDescription = null,
					modifier = Modifier
						.size(64.dp)
						.rotate(
							animateFloatAsState(
								when (fold) {
									true -> 0F
									else -> 180F
								}
							).value
						),
				)
			}
		}
		AnimatedVisibility(!fold) {
			Spacer(modifier = Modifier.height(16.dp))
			Column(
				verticalArrangement = Arrangement.spacedBy(16.dp),
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
			) {
				content()
			}
		}
	}
}

val localDateTimeParser = LocalDateTime.Format {
	year()
	char('-')
	monthNumber()
	char('-')
	day()
	char('T')
	hour()
	char(':')
	minute()
	char(':')
	second()
	chars("+00:00")
}

val localDateTimeFormater = LocalDateTime.Format {
	year()
	char('/')
	monthNumber()
	char('/')
	day()
	char(' ')
	hour()
	char(':')
	minute()
}

@Composable
context(_: Context, _: Data)
private fun VersionItem(
	version: VersionsManifest.Version,
	detail: (VersionsManifest.Version) -> String,
	onClick: (VersionsManifest.Version) -> Unit,
) = dest(DownloadsDest.Content.Vanilla) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(16.dp))
			.background(MaterialTheme.colorScheme.surfaceContainer)
			.clickable { onClick(version) }
			.padding(12.dp),
	) {
		Text(
			text = version.id,
			color = MaterialTheme.colorScheme.onSurface,
			style = MaterialTheme.typography.titleLarge,
		)
		Text(
			text = detail(version),
			color = MaterialTheme.colorScheme.onSurface,
			style = MaterialTheme.typography.bodyLarge,
		)
	}
}