package cn.yurin.minecraft_composable_launcher.ui.page

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.yurin.minecraft_composable_launcher.ui.localization.Context
import cn.yurin.minecraft_composable_launcher.ui.localization.SettingsPageDest
import cn.yurin.minecraft_composable_launcher.ui.localization.dest

@Composable
context(_: Context)
fun MorePage() = dest(SettingsPageDest) {
	Row(modifier = Modifier.fillMaxSize()) {
		Spacer(
			modifier = Modifier
				.weight(0.9F),
		)
	}
}