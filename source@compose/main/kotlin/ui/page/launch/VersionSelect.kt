package cn.yurin.minecraft_composable_launcher.ui.page.launch

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
import cn.yurin.minecraft_composable_launcher.core.Data
import cn.yurin.minecraft_composable_launcher.core.Version
import cn.yurin.minecraft_composable_launcher.core.currentVersion
import cn.yurin.minecraft_composable_launcher.core.versionManifests
import cn.yurin.minecraft_composable_launcher.ui.localization.*
import cn.yurin.minecraftcomposablelauncher.generated.resources.Res
import cn.yurin.minecraftcomposablelauncher.generated.resources.arrow_back_24px
import cn.yurin.minecraftcomposablelauncher.generated.resources.arrow_drop_up_24px
import org.jetbrains.compose.resources.painterResource

@Composable
context(_: Context, _: Data)
fun VersionSelectPage(onBack: () -> Unit) = dest(LaunchPageDest.VersionSelectPage) {
	AnimatedVisibility(versionManifests != null) {
		val versionManifests = remember { versionManifests!! }
		var currentPage by remember { mutableStateOf(versionManifests.keys.first()) }
		Row {
			VersionSelectSidebar(
				versionManifests = versionManifests,
				onBack = onBack,
				onChange = { currentPage = it },
			)
			VersionSelectContent(
				path = currentPage,
				versionManifests = versionManifests[currentPage]!!,
				onBack = onBack,
			)
		}
	}
}

@Composable
context(_: Context, _: Data)
private fun RowScope.VersionSelectSidebar(
	versionManifests: Map<String, List<Version>>,
	onBack: () -> Unit,
	onChange: (String) -> Unit,
) = dest(LaunchPageDest.VersionSelectPage.SideBar) {
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
		NavigationRailItem(
			selected = true,
			onClick = {},
			icon = {},
			label = {
				Text(
					text = "Minecraft",
					color = MaterialTheme.colorScheme.onSurface,
					style = MaterialTheme.typography.titleLarge,
				)
			},
		)
	}
}

@Composable
context(_: Context, _: Data)
private fun RowScope.VersionSelectContent(
	path: String,
	versionManifests: List<Version>,
	onBack: () -> Unit,
) = dest(LaunchPageDest.VersionSelectPage.Content) {
	val scope = rememberCoroutineScope()
	Box(
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.8F),
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
				Text(
					text = path,
					color = MaterialTheme.colorScheme.onSurface,
					style = MaterialTheme.typography.bodyLarge,
				)
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
				versionManifests.forEach { version ->
					VersionItem(
						title = {
							Text(
								text = version.name,
								color = MaterialTheme.colorScheme.onSurface,
								style = MaterialTheme.typography.bodyLarge,
							)
						},
						onClick = {
							currentVersion = version
							onBack()
						},
					)
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