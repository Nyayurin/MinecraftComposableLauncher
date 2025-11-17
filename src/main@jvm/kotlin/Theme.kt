package cn.yurin.minecraft_composable_launcher

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.dynamiccolor.ColorSpec

@Composable
fun Theme(
	seedColor: Color,
	isDark: Boolean,
	content: @Composable () -> Unit,
) {
	DynamicMaterialTheme(
		seedColor = seedColor,
		isDark = isDark,
		specVersion = ColorSpec.SpecVersion.SPEC_2025,
		animate = true,
		content = content,
	)
}