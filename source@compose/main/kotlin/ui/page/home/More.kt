package cn.yurin.mcl.ui.page.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.yurin.mcl.core.Data
import cn.yurin.mcl.ui.localization.Context
import cn.yurin.mcl.ui.localization.dest
import cn.yurin.mcl.ui.localization.destination.SettingsDest

@Composable
context(_: Context, _: Data)
fun More() = dest(SettingsDest) {
	Row(modifier = Modifier.fillMaxSize()) {
		Spacer(
			modifier = Modifier.weight(1F),
		)
	}
}