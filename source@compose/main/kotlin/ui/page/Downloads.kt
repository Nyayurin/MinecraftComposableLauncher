package cn.yurin.minecraft_composable_launcher.ui.page

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.yurin.minecraft_composable_launcher.core.Data
import cn.yurin.minecraft_composable_launcher.core.client
import cn.yurin.minecraft_composable_launcher.core.versionsManifest
import cn.yurin.minecraft_composable_launcher.ui.localization.*
import cn.yurin.minecraftcomposablelauncher.generated.resources.Res
import cn.yurin.minecraftcomposablelauncher.generated.resources.arrow_drop_up_24px
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char
import cn.yurin.minecraft_composable_launcher.network.VersionsManifest
import cn.yurin.minecraft_composable_launcher.ui.localization.Context
import cn.yurin.minecraft_composable_launcher.ui.localization.DownloadsPageDest
import cn.yurin.minecraft_composable_launcher.ui.localization.SettingsPageDest
import cn.yurin.minecraft_composable_launcher.ui.localization.current
import cn.yurin.minecraft_composable_launcher.ui.localization.language
import cn.yurin.minecraft_composable_launcher.ui.localization.latest
import cn.yurin.minecraft_composable_launcher.ui.localization.oldAlpha
import cn.yurin.minecraft_composable_launcher.ui.localization.oldBeta
import cn.yurin.minecraft_composable_launcher.ui.localization.release
import cn.yurin.minecraft_composable_launcher.ui.localization.releaseAt
import cn.yurin.minecraft_composable_launcher.ui.localization.snapshot
import cn.yurin.minecraft_composable_launcher.ui.localization.vanilla
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.painterResource

@Composable
context(_: Context, _: Data)
fun DownloadsPage() = dest(SettingsPageDest) {
	Row {
		var selection by remember { mutableIntStateOf(0) }
		Sidebar(
			currentPage = selection,
			onPageChanged = { selection = it },
		)
		Content(
			currentPage = selection,
		)
	}
}

@Composable
context(context: Context, _: Data)
private fun RowScope.Sidebar(
	currentPage: Int,
	onPageChanged: (Int) -> Unit,
) = dest(DownloadsPageDest.SideBar) {
	val scope = rememberCoroutineScope()
	val pages = listOf(vanilla)
	NavigationRail(
		containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.15F),
	) {
		pages.forEachIndexed { index, page ->
			NavigationRailItem(
				selected = currentPage == index,
				onClick = {
					if (currentPage == index) {
						scope.launch {
							val response =
								client.get("https://piston-meta.mojang.com/mc/game/version_manifest.json")
							versionsManifest = response.body<VersionsManifest>()
						}
					}
					onPageChanged(index)
				},
				icon = {},
				label = {
					AnimatedContent(context.language) {
						Text(
							text = page.language(it),
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
context(_: Context, _: Data)
private fun RowScope.Content(
	currentPage: Int,
) = dest(SettingsPageDest.Content) {
	Box(
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.85F),
	) {
		val scrollState = rememberScrollState()
		AnimatedContent(
			targetState = currentPage,
			transitionSpec = {
				slideIn(tween()) { IntOffset(0, (targetState compareTo initialState) * it.height) } togetherWith
						slideOut(tween()) { IntOffset(0, (initialState compareTo targetState) * it.height) }
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
					0 -> Vanilla()
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
	}
}

@Composable
context(_: Context, _: Data)
private fun Vanilla() = dest(DownloadsPageDest.Content.Vanilla) {
	val scope = rememberCoroutineScope()
	scope.launch(Dispatchers.IO) {
		val response = client.get("https://piston-meta.mojang.com/mc/game/version_manifest.json")
		versionsManifest = response.body<VersionsManifest>()
	}
	AnimatedVisibility(versionsManifest != null) {
		val manifest = remember { versionsManifest!! }
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
				VersionItem(
					version = manifest.versions.find { it.id == manifest.latest.release }!!,
					detail = { version ->
						buildString {
							append(release.current)
							append(", ")
							append(releaseAt.current)
							append(" ")
							append(
								localDateTimeFormater.format(
									LocalDateTime.parse(
										version.releaseTime,
										localDateTimeParser
									)
								)
							)
						}
					},
					onClick = { version ->

					},
				)
				VersionItem(
					version = manifest.versions.find { it.id == manifest.latest.snapshot }!!,
					detail = { version ->
						buildString {
							append(snapshot.current)
							append(", ")
							append(releaseAt.current)
							append(" ")
							append(
								localDateTimeFormater.format(
									LocalDateTime.parse(
										version.releaseTime,
										localDateTimeParser
									)
								)
							)
						}
					},
					onClick = { version ->

					},
				)
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
								localDateTimeFormater.format(
									LocalDateTime.parse(
										version.releaseTime,
										localDateTimeParser
									)
								)
							},
							onClick = { version ->

							},
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
	modifier: Modifier = Modifier.fillMaxWidth(),
	content: @Composable context(Context) ColumnScope.() -> Unit,
) {
	var fold by remember { mutableStateOf(true) }
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
) = dest(DownloadsPageDest.Content.Vanilla) {
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