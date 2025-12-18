package cn.yurin.mcl.ui.page

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
import cn.yurin.mcl.core.Data
import cn.yurin.mcl.ui.HomePage
import cn.yurin.mcl.ui.localization.Context
import cn.yurin.mcl.ui.localization.dest
import cn.yurin.mcl.ui.localization.destination.*
import cn.yurin.mcl.ui.localization.language
import cn.yurin.mcl.ui.page.home.*
import cn.yurin.minecraftcomposablelauncher.generated.resources.Res
import cn.yurin.minecraftcomposablelauncher.generated.resources.close_24px
import cn.yurin.minecraftcomposablelauncher.generated.resources.minimize_24px
import org.jetbrains.compose.resources.painterResource

@Composable
context(context: Context, _: Data)
fun Home(
	windowDraggableArea: @Composable (@Composable () -> Unit) -> Unit,
	exitApplication: () -> Unit,
	minimizeWindow: () -> Unit,
) {
	var currentPage by remember { mutableStateOf<HomePage>(HomePage.Launch) }
	Column {
		TopBar(
			currentPage = currentPage,
			onPageChanges = { currentPage = it },
			windowDraggableArea = windowDraggableArea,
			exitApplication = exitApplication,
			minimizeWindow = minimizeWindow,
		)
		AnimatedContent(
			targetState = currentPage,
			transitionSpec = {
				slideIn(tween()) {
					IntOffset((targetState.position compareTo initialState.position) * it.width, 0)
				} togetherWith slideOut(tween()) {
					IntOffset((initialState.position compareTo targetState.position) * it.width, 0)
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

@Composable
context(context: Context, _: Data)
private fun TopBar(
	currentPage: HomePage,
	onPageChanges: (HomePage) -> Unit,
	windowDraggableArea: @Composable (@Composable () -> Unit) -> Unit,
	exitApplication: () -> Unit,
	minimizeWindow: () -> Unit,
) = dest(TopbarDest) {
	windowDraggableArea {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.primary)
				.padding(8.dp),
		) {
			Text(
				text = "MCL",
				color = MaterialTheme.colorScheme.onPrimary,
				style = MaterialTheme.typography.titleLarge,
				modifier = Modifier
					.padding(start = 8.dp)
					.align(Alignment.CenterStart),
			)
			val pages = listOf(HomePage.Launch, HomePage.Accounts, HomePage.Downloads, HomePage.Settings, HomePage.More)
			AnimatedContent(
				targetState = context.language,
				modifier = Modifier.align(Alignment.Center),
			) { language ->
				SingleChoiceSegmentedButtonRow(
					space = 8.dp,
				) {
					pages.forEachIndexed { index, page ->
						SegmentedButton(
							selected = currentPage == page,
							onClick = { onPageChanges(page) },
							shape = SegmentedButtonDefaults.itemShape(
								index = index,
								count = pages.size
							),
							colors = SegmentedButtonDefaults.colors(
								activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
								activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
								activeBorderColor = MaterialTheme.colorScheme.primaryContainer,
								inactiveContainerColor = MaterialTheme.colorScheme.primary,
								inactiveContentColor = MaterialTheme.colorScheme.onPrimary,
								inactiveBorderColor = MaterialTheme.colorScheme.primary,
							),
							label = {
								Text(
									text = when (page) {
										HomePage.Launch -> launch
										HomePage.Accounts -> accounts
										HomePage.Downloads -> downloads
										HomePage.Settings -> settings
										HomePage.More -> more
									}.language(language),
									color = animateColorAsState(
										when (currentPage == page) {
											true -> MaterialTheme.colorScheme.onPrimaryContainer
											else -> MaterialTheme.colorScheme.onPrimary
										}
									).value,
									style = MaterialTheme.typography.titleSmall,
								)
							}
						)
					}
				}
			}
			Row(
				horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
				modifier = Modifier.align(Alignment.CenterEnd),
			) {
				IconButton(
					onClick = minimizeWindow,
				) {
					Icon(
						painter = painterResource(Res.drawable.minimize_24px),
						contentDescription = "Minimize",
						tint = MaterialTheme.colorScheme.onPrimary,
					)
				}
				IconButton(
					onClick = exitApplication,
				) {
					Icon(
						painter = painterResource(Res.drawable.close_24px),
						contentDescription = "Close",
						tint = MaterialTheme.colorScheme.onPrimary,
					)
				}
			}
		}
	}
}