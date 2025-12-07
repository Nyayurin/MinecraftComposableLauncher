package cn.yurin.minecraft_composable_launcher.ui.page.launch

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.yurin.minecraft_composable_launcher.ui.localization.Context
import cn.yurin.minecraft_composable_launcher.ui.localization.DownloadsPageDest
import cn.yurin.minecraft_composable_launcher.ui.localization.dest
import cn.yurin.minecraftcomposablelauncher.generated.resources.Res
import cn.yurin.minecraftcomposablelauncher.generated.resources.arrow_back_24px
import org.jetbrains.compose.resources.painterResource

@Composable
context(context: Context)
fun RowScope.VersionSelectSidebar(
	onBack: () -> Unit,
) = dest(DownloadsPageDest.SideBar) {
	NavigationRail(
		containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.3F),
	) {
		IconButton(
			onClick = onBack,
		) {
			Icon(
				painter = painterResource(Res.drawable.arrow_back_24px),
				tint = MaterialTheme.colorScheme.onSurface,
				contentDescription = null,
				modifier = Modifier.size(64.dp),
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
					style = MaterialTheme.typography.titleSmall,
				)
			},
		)
	}
}