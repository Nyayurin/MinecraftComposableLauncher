package cn.yurin.mcl.ui.neo.page

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
context(_: Context, _: Data)
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
		Row {
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

@Composable
context(_: Context, _: Data)
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
					horizontal = 20.dp,
					vertical = 6.dp,
				),
		) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
				modifier = Modifier.align(Alignment.CenterStart),
			) {
				Surface(
					color = Color(0xFFF25056),
					modifier = Modifier
						.size(20.dp)
						.clip(CircleShape)
						.clickable(onClick = exitApplication),
				) {}
				Surface(
					color = Color(0xFFFAC536),
					modifier = Modifier
						.size(20.dp)
						.clip(CircleShape)
						.clickable(onClick = minimizeWindow),
				) {}
				Surface(
					color = Color(0xFFD9D9DA),
					modifier = Modifier
						.size(20.dp)
						.clip(CircleShape),
				) {}
			}
			Text(
				text = "Minecraft Composable Launcher",
				color = MaterialTheme.colorScheme.onBackground,
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier.align(Alignment.Center),
			)
			Row(
				horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
				modifier = Modifier.align(Alignment.CenterEnd),
			) {
				IconButton(
					onClick = minimizeWindow,
				) {
					Icon(
						painter = painterResource(Res.drawable.minimize),
						contentDescription = "Minimize",
						tint = MaterialTheme.colorScheme.onBackground,
						modifier = Modifier.size(20.dp),
					)
				}
				IconButton(
					onClick = exitApplication,
				) {
					Icon(
						painter = painterResource(Res.drawable.close),
						contentDescription = "Close",
						tint = MaterialTheme.colorScheme.onBackground,
						modifier = Modifier.size(20.dp),
					)
				}
			}
		}
	}
}

@Composable
context(_: Context, _: Data)
private fun SideBar(
	currentPage: HomePage,
	onPageChanges: (HomePage) -> Unit,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
		modifier = Modifier
			.fillMaxHeight()
			.padding(horizontal = 10.dp),
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
			)
		}
	}
}

@Composable
private fun SideBarButton(
	selected: Boolean,
	onSelect: () -> Unit,
	icon: Painter,
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.width(50.dp)
			.clip(SmoothRoundedCornerShape(radius = 16.dp))
			.background(
				animateColorAsState(
					when (selected) {
						true -> MaterialTheme.colorScheme.primary
						else -> MaterialTheme.colorScheme.background
					}
				).value
			)
			.clickable(onClick = onSelect)
			.padding(vertical = 6.dp),
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
			modifier = Modifier.size(24.dp),
		)
	}
}