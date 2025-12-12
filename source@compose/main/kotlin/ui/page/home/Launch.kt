package cn.yurin.mcl.ui.page.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.IntOffset
import cn.yurin.mcl.core.Data
import cn.yurin.mcl.ui.LaunchPages
import cn.yurin.mcl.ui.localization.*
import cn.yurin.mcl.ui.page.home.launch.Home
import cn.yurin.mcl.ui.page.home.launch.Versions

@Composable
context(_: Context, data: Data)
fun Launch(onChangeToAccountPage: () -> Unit) {
	var currentPage by remember { mutableStateOf<LaunchPages>(LaunchPages.Home) }
	AnimatedContent(
		targetState = currentPage,
		transitionSpec = {
			slideIn(tween()) {
				IntOffset(0, (targetState.position compareTo initialState.position) * it.height)
			} togetherWith slideOut(tween()) {
				IntOffset(0, (initialState.position compareTo targetState.position) * it.height)
			}
		},
	) { page ->
		when (page) {
			LaunchPages.Home -> Home(
				onChangePage = { currentPage = it },
				onChangeToAccountPage = onChangeToAccountPage,
			)

			LaunchPages.Versions -> Versions(
				onBack = { currentPage = LaunchPages.Home }
			)

			LaunchPages.Settings -> Text("Coming soon...")
		}
	}
}