package cn.yurin.mcl.ui.page

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.yurin.mcl.core.Data
import cn.yurin.mcl.ui.localization.Context
import cn.yurin.mcl.ui.localization.SettingsPageDest
import cn.yurin.mcl.ui.localization.dest

@Composable
context(_: Context, _: Data)
fun MorePage() = dest(SettingsPageDest) {
	Row(modifier = Modifier.fillMaxSize()) {
		Spacer(
			modifier = Modifier
				.weight(0.9F),
		)
	}
}