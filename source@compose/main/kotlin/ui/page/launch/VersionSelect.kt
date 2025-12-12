package cn.yurin.mcl.ui.page.launch

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import cn.yurin.mcl.core.*
import cn.yurin.mcl.ui.localization.*
import cn.yurin.minecraftcomposablelauncher.generated.resources.Res
import cn.yurin.minecraftcomposablelauncher.generated.resources.arrow_back_24px
import cn.yurin.minecraftcomposablelauncher.generated.resources.arrow_drop_up_24px
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.name
import org.jetbrains.compose.resources.painterResource
import java.io.File

@Composable
context(_: Context, _: Data)
fun VersionSelectPage(onBack: () -> Unit) = dest(LaunchPageDest.VersionSelectPage) {
	Row {
		Sidebar(
			onBack = onBack,
		)
		Content(
			onBack = onBack,
		)
	}
}

@Composable
context(_: Context, data: Data)
private fun RowScope.Sidebar(
	onBack: () -> Unit,
) = dest(LaunchPageDest.VersionSelectPage.SideBar) {
	val launcher = rememberDirectoryPickerLauncher { file ->
		if (file != null) {
			if (!data.folders.any { it.path == file.absolutePath() }) {
				data.folders += GameFolder.DotMinecraft(
					name = file.name,
					path = file.absolutePath(),
					versions = (File(file.file, "versions").listFiles() ?: emptyArray()).filter { file ->
						file.isDirectory
					}.filter { version ->
						version.listFiles { file ->
							file.isFile && file.name == "${version.name}.json"
						}.isNotEmpty()
					}.map { version ->
						Version(
							name = version.name,
							path = version.absolutePath,
							manifest = data.json.decodeFromString(File(version, "${version.name}.json").readText())
						)
					}.sortedByDescending {
						it.manifest.releaseTime
					}
				)
				if (data.currentFolder == null) {
					data.currentFolder = data.folders.first()
				}
				if (data.currentVersion == null) {
					data.currentVersion = data.currentFolder!!.versions.firstOrNull()
				}
			}
		}
	}
	NavigationRail(
		containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.2F),
	) {
		Spacer(
			modifier = Modifier.height(16.dp),
		)
		IconButton(
			onClick = onBack,
		) {
			Icon(
				painter = painterResource(Res.drawable.arrow_back_24px),
				tint = MaterialTheme.colorScheme.onSurface,
				contentDescription = null,
				modifier = Modifier.size(32.dp),
			)
		}
		Spacer(
			modifier = Modifier.height(16.dp),
		)
		AnimatedContent(data.folders) { folders ->
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(16.dp),
			) {
				folders.forEach { folder ->
					NavigationRailItem(
						selected = true,
						onClick = { data.currentFolder = folder },
						icon = {},
						label = {
							Text(
								text = folder.name,
								color = MaterialTheme.colorScheme.onSurface,
								style = MaterialTheme.typography.titleLarge,
							)
						},
					)
				}
			}
		}
		Spacer(
			modifier = Modifier.weight(1F),
		)
		FilledTonalButton(
			onClick = {
				launcher.launch()
			},
			shape = RoundedCornerShape(12.dp),
			colors = ButtonDefaults.filledTonalButtonColors(
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = MaterialTheme.colorScheme.onPrimary,
			),
		) {
			Text(
				text = importFolder.current,
				color = MaterialTheme.colorScheme.onPrimary,
				style = MaterialTheme.typography.bodyLarge,
			)
		}
		Spacer(
			modifier = Modifier.height(16.dp),
		)
	}
}

@Composable
context(_: Context, data: Data)
private fun RowScope.Content(
	onBack: () -> Unit,
) = dest(LaunchPageDest.VersionSelectPage.Content) {
	if (data.currentFolder == null) {
		Spacer(
			modifier = Modifier.weight(0.8F),
		)
	}
	AnimatedVisibility(
		visible = data.currentFolder != null,
		modifier = Modifier.weight(0.8F),
	) {
		var folder by remember { mutableStateOf(data.currentFolder!!) }
		remember(data.currentFolder) {
			data.currentFolder?.let { folder = it }
		}
		Box(
			modifier = Modifier.fillMaxHeight(),
		) {
			val scrollState = rememberScrollState()
			Column(
				verticalArrangement = Arrangement.spacedBy(32.dp),
				modifier = Modifier
					.verticalScroll(scrollState)
					.padding(horizontal = 32.dp),
			) {
				Spacer(modifier = Modifier.height(0.dp))
				Card(
					title = {
						Text(
							text = info.current,
							color = MaterialTheme.colorScheme.onSurface,
							style = MaterialTheme.typography.headlineSmall,
						)
					}
				) {
					AnimatedContent(folder) { folder ->
						Text(
							text = folder.path,
							color = MaterialTheme.colorScheme.onSurface,
							style = MaterialTheme.typography.bodyLarge,
						)
					}
				}
				FoldableCard(
					title = {
						Text(
							text = regularVersion.current,
							color = MaterialTheme.colorScheme.onSurface,
							style = MaterialTheme.typography.headlineSmall,
						)
					},
					defaultFold = false,
				) {
					AnimatedContent(folder) { folder ->
						Column(
							verticalArrangement = Arrangement.spacedBy(16.dp),
							modifier = Modifier.fillMaxWidth(),
						) {
							folder.versions.forEach { version ->
								VersionItem(
									title = {
										Text(
											text = version.name,
											color = MaterialTheme.colorScheme.onSurface,
											style = MaterialTheme.typography.bodyLarge,
										)
									},
									onClick = {
										data.currentVersion = version
										onBack()
									},
								)
							}
						}
					}
				}
				Spacer(modifier = Modifier.height(0.dp))
			}
			VerticalScrollbar(
				adapter = rememberScrollbarAdapter(scrollState),
				modifier = Modifier
					.align(Alignment.CenterEnd)
					.fillMaxHeight(),
			)
		}
	}
}

@Composable
context(_: Context, _: Data)
private fun Card(
	title: @Composable () -> Unit,
	modifier: Modifier = Modifier.fillMaxWidth(),
	content: @Composable context(Context) ColumnScope.() -> Unit,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
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
	defaultFold: Boolean = true,
	modifier: Modifier = Modifier.fillMaxWidth(),
	content: @Composable context(Context) ColumnScope.() -> Unit,
) {
	var fold by remember { mutableStateOf(defaultFold) }
	Column(
		modifier = modifier
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
				onClick = { fold = !fold }
			) {
				Icon(
					painter = painterResource(Res.drawable.arrow_drop_up_24px),
					tint = MaterialTheme.colorScheme.onSurface,
					contentDescription = null,
					modifier = modifier
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
			Spacer(modifier = modifier.height(16.dp))
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

@Composable
context(_: Context, _: Data)
private fun VersionItem(
	title: @Composable () -> Unit,
	onClick: () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(16.dp))
			.background(MaterialTheme.colorScheme.surfaceContainer)
			.clickable { onClick() }
			.padding(12.dp),
	) {
		title()
	}
}