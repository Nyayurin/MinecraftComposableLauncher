package cn.yurin.minecraft_composable_launcher.ui.page.launch

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cn.yurin.minecraft_composable_launcher.network.VersionsManifest
import cn.yurin.minecraft_composable_launcher.ui.client
import cn.yurin.minecraft_composable_launcher.ui.localization.*
import cn.yurin.minecraft_composable_launcher.ui.page.manifest
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import cn.yurin.minecraft_composable_launcher.ui.localization.Context
import cn.yurin.minecraft_composable_launcher.ui.localization.DownloadsPageDest
import cn.yurin.minecraft_composable_launcher.ui.localization.vanilla

@Composable
context(context: Context)
fun RowScope.VersionSelectSidebar(
	onBack: () -> Unit,
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
			/*NavigationRailItem(
				selected = currentPage == index,
				onClick = {
					if (currentPage == index) {
						scope.launch {
							val response = client.get("https://piston-meta.mojang.com/mc/game/version_manifest.json")
							manifest = response.body<VersionsManifest>()
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
			)*/
		}
	}
}