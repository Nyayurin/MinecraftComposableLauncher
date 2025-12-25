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
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.*
import cn.yurin.mcl.core.Data
import cn.yurin.mcl.ui.HomePage
import cn.yurin.mcl.ui.localization.Context
import cn.yurin.mcl.ui.localization.dest
import cn.yurin.mcl.ui.localization.destination.TopbarDest
import cn.yurin.mcl.ui.page.home.*
import cn.yurin.minecraftcomposablelauncher.generated.resources.*
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
		AnimatedContent(data.windowSize.widthSizeClass == WindowWidthSizeClass.Compact) { isCompact ->
			when (isCompact) {
				true -> Column {
					AnimatedContent(
						targetState = currentPage,
						transitionSpec = {
							slideIn(tween()) {
								IntOffset(0, (targetState.position compareTo initialState.position) * it.width)
							} togetherWith slideOut(tween()) {
								IntOffset(0, (initialState.position compareTo targetState.position) * it.width)
							}
						},
						modifier = Modifier.weight(1F),
					) {
						when (it) {
							HomePage.Launch -> Launch(
								onChangeToAccountPage = { currentPage = HomePage.Accounts },
							)

							HomePage.Accounts -> Accounts()
							HomePage.Downloads -> Downloads()
							HomePage.Settings -> Settings()
							HomePage.More -> More()
						}
					}
					NavigationBar(
						currentPage = currentPage,
					) { currentPage = it }
				}

				else -> Row {
					SideBar(
						currentPage = currentPage,
						onPageChanges = { currentPage = it },
					)
					AnimatedContent(
						targetState = currentPage,
						transitionSpec = {
							slideIn(tween()) {
								IntOffset(0, (targetState.position compareTo initialState.position) * it.width)
							} togetherWith slideOut(tween()) {
								IntOffset(0, (initialState.position compareTo targetState.position) * it.width)
							}
						},
					) {
						when (it) {
							HomePage.Launch -> Launch(
								onChangeToAccountPage = { currentPage = HomePage.Accounts },
							)

							HomePage.Accounts -> Accounts()
							HomePage.Downloads -> Downloads()
							HomePage.Settings -> Settings()
							HomePage.More -> More()
						}
					}
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
) = dest(TopbarDest) {
	windowDraggableArea {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					animateDpAsState(
						when (data.windowSize.widthSizeClass) {
							WindowWidthSizeClass.Expanded -> 16.dp
							else -> 8.dp
						}
					).value,
				),
		) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(9.dp, Alignment.Start),
				modifier = Modifier
					.align(Alignment.CenterStart)
					.padding(
						start = animateDpAsState(
							when (data.windowSize.widthSizeClass) {
								WindowWidthSizeClass.Compact -> 3.dp
								WindowWidthSizeClass.Medium -> 0.5.dp
								else -> 8.5.dp
							}
						).value
					),
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
			AnimatedContent(
				targetState = data.windowSize.widthSizeClass == WindowWidthSizeClass.Compact,
				modifier = Modifier.align(Alignment.Center),
			) { isCompact ->
				when (isCompact) {
					true -> Text(
						text = "MCL",
						color = MaterialTheme.colorScheme.onBackground,
						style = MaterialTheme.typography.titleMedium,
					)

					else -> Text(
						text = "Minecraft Composable Launcher",
						color = MaterialTheme.colorScheme.onBackground,
						fontSize = TextUnit(18F, TextUnitType.Sp),
						style = MaterialTheme.typography.titleMedium,
					)
				}
			}
			Row(
				horizontalArrangement = Arrangement.spacedBy(
					space = animateDpAsState(
						when (data.windowSize.widthSizeClass) {
							WindowWidthSizeClass.Compact -> 8.dp
							else -> 12.dp
						}
					).value,
					alignment = Alignment.End
				),
				modifier = Modifier.align(Alignment.CenterEnd),
			) {
				val iconSize by animateDpAsState(
					when (data.windowSize.widthSizeClass) {
						WindowWidthSizeClass.Compact -> 20.dp
						else -> 24.dp
					}
				)
				Icon(
					painter = painterResource(Res.drawable.minimize),
					contentDescription = "Minimize",
					tint = MaterialTheme.colorScheme.onBackground,
					modifier = Modifier
						.size(iconSize)
						.clip(CircleShape)
						.clickable(onClick = minimizeWindow),
				)
				Icon(
					painter = painterResource(Res.drawable.close),
					contentDescription = "Close",
					tint = MaterialTheme.colorScheme.onBackground,
					modifier = Modifier
						.size(iconSize)
						.clip(CircleShape)
						.clickable(onClick = exitApplication),
				)
			}
		}
	}
}

@Composable
context(_: Context, _: Data)
private fun NavigationBar(
	currentPage: HomePage,
	onPageChanges: (HomePage) -> Unit,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 16.dp),
	) {
		val pages = listOf(HomePage.Launch, HomePage.Accounts, HomePage.Downloads, HomePage.Settings, HomePage.More)
		pages.forEach { page ->
			SideBarButton(
				selected = currentPage == page,
				onSelect = { onPageChanges(page) },
				icon = when (page) {
					HomePage.Launch -> painterResource(Res.drawable.launch)
					HomePage.Accounts -> painterResource(Res.drawable.accounts)
					HomePage.Downloads -> painterResource(Res.drawable.downloads)
					HomePage.Settings -> painterResource(Res.drawable.settings)
					HomePage.More -> painterResource(Res.drawable.others)
				},
				buttonSize = DpSize(48.dp, 48.dp),
				iconSize = DpSize(24.dp, 24.dp),
			)
		}
	}
}

@Composable
context(_: Context, data: Data)
private fun SideBar(
	currentPage: HomePage,
	onPageChanges: (HomePage) -> Unit,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(
			space = animateDpAsState(
				when (data.windowSize.heightSizeClass) {
					WindowHeightSizeClass.Compact -> 12.dp
					WindowHeightSizeClass.Medium -> 24.dp
					else -> 60.dp
				}
			).value,
			alignment = Alignment.CenterVertically
		),
		modifier = Modifier
			.fillMaxHeight()
			.padding(horizontal = 16.dp),
	) {
		val pages = listOf(HomePage.Launch, HomePage.Accounts, HomePage.Downloads, HomePage.Settings, HomePage.More)
		val buttonWidth by animateDpAsState(
			when {
				data.windowSize.widthSizeClass == WindowWidthSizeClass.Medium -> 48.dp
				else -> 80.dp
			}
		)
		val buttonHeight by animateDpAsState(
			when (data.windowSize.widthSizeClass) {
				WindowWidthSizeClass.Expanded -> 56.dp
				else -> 48.dp
			}
		)
		val iconSize by animateDpAsState(
			when (data.windowSize.widthSizeClass) {
				WindowWidthSizeClass.Expanded -> 36.dp
				else -> 24.dp
			}
		)
		pages.forEach { page ->
			SideBarButton(
				selected = currentPage == page,
				onSelect = { onPageChanges(page) },
				icon = when (page) {
					HomePage.Launch -> painterResource(Res.drawable.launch)
					HomePage.Accounts -> painterResource(Res.drawable.accounts)
					HomePage.Downloads -> painterResource(Res.drawable.downloads)
					HomePage.Settings -> painterResource(Res.drawable.settings)
					HomePage.More -> painterResource(Res.drawable.others)
				},
				buttonSize = DpSize(
					width = buttonWidth,
					height = buttonHeight,
				),
				iconSize = DpSize(iconSize, iconSize),
			)
		}
	}
}

@Composable
private fun SideBarButton(
	selected: Boolean,
	onSelect: () -> Unit,
	icon: Painter,
	buttonSize: DpSize,
	iconSize: DpSize,
) {
	Box(
		contentAlignment = Alignment.Center,
		modifier = Modifier
			.size(buttonSize)
			.clip(SmoothRoundedCornerShape(radius = 12.dp))
			.background(
				animateColorAsState(
					when (selected) {
						true -> MaterialTheme.colorScheme.primary
						else -> MaterialTheme.colorScheme.background
					}
				).value
			)
			.clickable(onClick = onSelect),
	) {
		Icon(
			painter = icon,
			contentDescription = null,
			tint = animateColorAsState(
				when (selected) {
					true -> MaterialTheme.colorScheme.onPrimary
					else -> MaterialTheme.colorScheme.onBackground
				}
			).value,
			modifier = Modifier.size(iconSize),
		)
	}
}