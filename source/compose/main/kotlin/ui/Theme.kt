package cn.yurin.minecraft_composable_launcher.ui

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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

fun lightScrollbarStyle() = ScrollbarStyle(
	minimalHeight = 16.dp,
	thickness = 8.dp,
	shape = RoundedCornerShape(4.dp),
	hoverDurationMillis = 300,
	unhoverColor = Color.Black.copy(alpha = 0.125f),
	hoverColor = Color.Black.copy(alpha = 0.50f),
)

fun darkScrollbarStyle() = ScrollbarStyle(
	minimalHeight = 16.dp,
	thickness = 8.dp,
	shape = RoundedCornerShape(4.dp),
	hoverDurationMillis = 300,
	unhoverColor = Color.White.copy(alpha = 0.25f),
	hoverColor = Color.White.copy(alpha = 0.75f),
)