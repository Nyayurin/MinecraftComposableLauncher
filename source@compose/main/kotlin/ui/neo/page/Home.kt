package cn.yurin.mcl.ui.neo.page

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.yurin.mcl.core.Data
import cn.yurin.mcl.core.OperationSystem
import cn.yurin.mcl.core.PreConfiguration
import cn.yurin.mcl.ui.HomePage
import cn.yurin.mcl.ui.localization.Context
import cn.yurin.mcl.ui.localization.dest
import cn.yurin.mcl.ui.localization.destination.NavigationBar
import cn.yurin.mcl.ui.neo.page.home.Launch
import cn.yurin.mcl.ui.page.home.Accounts
import cn.yurin.mcl.ui.page.home.Downloads
import cn.yurin.mcl.ui.page.home.More
import cn.yurin.mcl.ui.page.home.Settings
import cn.yurin.mcl.ui.page.home.launch.Versions
import cn.yurin.minecraftcomposablelauncher.generated.resources.*
import dev.chrisbanes.haze.LocalHazeStyle
import dev.chrisbanes.haze.hazeEffect
import io.github.iamcalledrob.smoothRoundedCornerShape.SmoothRoundedCornerShape
import org.jetbrains.compose.resources.painterResource

@Composable
context(_: Context, data: Data)
fun Home(
	windowDraggableArea: @Composable (@Composable () -> Unit) -> Unit,
	exitApplication: () -> Unit,
	minimizeWindow: () -> Unit,
) {
	var currentPage by remember { mutableStateOf<HomePage>(HomePage.Launch) }
	Column {
		TopBar(
			windowDraggableArea = windowDraggableArea,
			exitApplication = exitApplication,
			minimizeWindow = minimizeWindow,
		)
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.fillMaxSize(),
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxSize()
					.weight(1F)
					.padding(
						animateDpAsState(
							when (data.windowSize.widthSizeClass) {
								WindowWidthSizeClass.Compact -> 0.dp
								WindowWidthSizeClass.Medium -> 12.dp
								else -> 24.dp
							}
						).value
					),
			) {
				AnimatedVisibility(data.windowSize.widthSizeClass != WindowWidthSizeClass.Compact) {
					Row {
						SideBar(
							windowDraggableArea = windowDraggableArea,
							currentPage = currentPage,
							onPageChanges = { currentPage = it },
						)
						Spacer(
							modifier = Modifier.width(
								animateDpAsState(
									when (data.windowSize.widthSizeClass) {
										WindowWidthSizeClass.Medium -> 12.dp
										else -> 24.dp
									}
								).value,
							),
						)
					}
				}
				AnimatedContent(
					targetState = currentPage,
					transitionSpec = {
						slideIn(tween()) {
							IntOffset(0, (targetState.position compareTo initialState.position) * it.height)
						} togetherWith slideOut(tween()) {
							IntOffset(0, (initialState.position compareTo targetState.position) * it.height)
						}
					},
				) {
					when (it) {
						HomePage.Launch -> Launch(
							onChangeToGamesPage = { currentPage = HomePage.Games },
							onChangeToAccountsPage = { currentPage = HomePage.Accounts },
						)

						HomePage.Games -> Versions(onBack = { currentPage = HomePage.Launch })
						HomePage.Accounts -> Accounts()
						HomePage.Downloads -> Downloads()
						HomePage.Settings -> Settings()
						HomePage.Others -> More()
					}
				}
			}
			AnimatedVisibility(data.windowSize.widthSizeClass == WindowWidthSizeClass.Compact) {
				Column {
					Spacer(
						modifier = Modifier.height(12.dp),
					)
					NavigationBar(
						currentPage = currentPage,
						onPageChanges = { currentPage = it },
					)
				}
			}
		}
	}
}

@Composable
context(_: Context, data: Data)
private fun TopBar(
	windowDraggableArea: @Composable (@Composable () -> Unit) -> Unit,
	exitApplication: () -> Unit,
	minimizeWindow: () -> Unit,
) = dest(NavigationBar) {
	windowDraggableArea {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.run {
					when (data.imageBackground) {
						true -> hazeEffect(data.hazeState, LocalHazeStyle.current.copy(backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85F)))
						else -> background(MaterialTheme.colorScheme.surfaceContainerHigh)
					}
				}
				.padding(
					animateDpAsState(
						when (data.windowSize.widthSizeClass) {
							WindowWidthSizeClass.Compact -> 8.dp
							WindowWidthSizeClass.Medium -> 12.dp
							else -> 16.dp
						}
					).value,
				),
		) {
			if (PreConfiguration.system == OperationSystem.Macos) {
				Row(
					horizontalArrangement = Arrangement.spacedBy(9.dp, Alignment.Start),
					modifier = Modifier
						.align(Alignment.CenterStart)
						.padding(start = 4.5.dp),
				) {
					Surface(
						color = Color(0xFFF25056),
						modifier = Modifier
							.size(15.dp)
							.clip(CircleShape)
							.clickable(onClick = exitApplication),
					) {}
					Surface(
						color = Color(0xFFFAC536),
						modifier = Modifier
							.size(15.dp)
							.clip(CircleShape)
							.clickable(onClick = minimizeWindow),
					) {}
					Surface(
						color = Color(0xFFD9D9DA),
						modifier = Modifier
							.size(15.dp)
							.clip(CircleShape),
					) {}
				}
			}
			AnimatedContent(
				targetState = data.windowSize.widthSizeClass == WindowWidthSizeClass.Compact,
				modifier = Modifier.align(Alignment.Center),
			) { isCompact ->
				when (isCompact) {
					true -> Text(
						text = "MCL",
						color = MaterialTheme.colorScheme.onSurface,
						style = MaterialTheme.typography.titleMedium,
					)

					else -> Text(
						text = "Minecraft Composable Launcher",
						color = MaterialTheme.colorScheme.onSurface,
						fontSize = 18.sp,
						style = MaterialTheme.typography.titleMedium,
					)
				}
			}
			if (PreConfiguration.system != OperationSystem.Macos) {
				Row(
					horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
					modifier = Modifier.align(Alignment.CenterEnd),
				) {
					Icon(
						painter = painterResource(Res.drawable.minimize),
						contentDescription = "Minimize",
						tint = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier
							.size(24.dp)
							.clip(CircleShape)
							.clickable(onClick = minimizeWindow),
					)
					Icon(
						painter = painterResource(Res.drawable.close),
						contentDescription = "Close",
						tint = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier
							.size(24.dp)
							.clip(CircleShape)
							.clickable(onClick = exitApplication),
					)
				}
			}
		}
	}
}

@Composable
context(_: Context, data: Data)
private fun NavigationBar(
	currentPage: HomePage,
	onPageChanges: (HomePage) -> Unit,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
		modifier = Modifier
			.fillMaxWidth()
			.clip(SmoothRoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp))
			.run {
				when (data.imageBackground) {
					true -> hazeEffect(data.hazeState)
					else -> background(MaterialTheme.colorScheme.surfaceContainer)
				}
			}
			.padding(
				horizontal = 32.dp,
				vertical = 16.dp,
			),
	) {
		BarComponents(
			currentPage = currentPage,
			onPageChanges = onPageChanges,
			size = 36.dp,
		)
	}
}

@Composable
context(_: Context, data: Data)
private fun SideBar(
	windowDraggableArea: @Composable (@Composable () -> Unit) -> Unit,
	currentPage: HomePage,
	onPageChanges: (HomePage) -> Unit,
) {
	windowDraggableArea {
		Column(
			verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
			modifier = Modifier
				.clip(
					SmoothRoundedCornerShape(
						radius = animateDpAsState(
							when (data.windowSize.widthSizeClass) {
								WindowWidthSizeClass.Medium -> 12.dp
								else -> 24.dp
							}
						).value
					)
				)
				.run {
					when (data.imageBackground) {
						true -> hazeEffect(data.hazeState)
						else -> background(MaterialTheme.colorScheme.surfaceContainer)
					}
				}
				.padding(
					horizontal = 16.dp,
					vertical = 32.dp,
				),
		) {
			val iconSize by animateDpAsState(
				when (data.windowSize.widthSizeClass) {
					WindowWidthSizeClass.Medium -> 36.dp
					else -> 48.dp
				}
			)
			BarComponents(
				currentPage = currentPage,
				onPageChanges = onPageChanges,
				size = iconSize,
			)
		}
	}
}

@Composable
context(_: Context, _: Data)
private fun BarComponents(
	currentPage: HomePage,
	onPageChanges: (HomePage) -> Unit,
	size: Dp,
) {
	val pages = listOf(HomePage.Launch, HomePage.Games, HomePage.Accounts, HomePage.Downloads, HomePage.Settings, HomePage.Others)
	pages.forEach { page ->
		SideBarButton(
			selected = currentPage == page,
			onSelect = { onPageChanges(page) },
			icon = when (page) {
				HomePage.Launch -> painterResource(Res.drawable.launch)
				HomePage.Games -> painterResource(Res.drawable.minecraft)
				HomePage.Accounts -> painterResource(Res.drawable.accounts)
				HomePage.Downloads -> painterResource(Res.drawable.downloads)
				HomePage.Settings -> painterResource(Res.drawable.settings)
				HomePage.Others -> painterResource(Res.drawable.others)
			},
			size = size,
		)
	}
}

@Composable
private fun SideBarButton(
	selected: Boolean,
	onSelect: () -> Unit,
	icon: Painter,
	size: Dp,
) {
	Icon(
		painter = icon,
		contentDescription = null,
		tint = animateColorAsState(
			when (selected) {
				true -> MaterialTheme.colorScheme.primary
				else -> MaterialTheme.colorScheme.onSurface
			}
		).value,
		modifier = Modifier
			.size(size)
			.clip(SmoothRoundedCornerShape(radius = 12.dp))
			.clickable(onClick = onSelect),
	)
}