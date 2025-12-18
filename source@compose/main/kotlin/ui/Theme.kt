package cn.yurin.mcl.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.dynamiccolor.ColorSpec

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Theme(
	seedColor: Color,
	isDark: Boolean,
	isExpressive: Boolean,
	content: @Composable () -> Unit,
) {
	AnimatedContent(isExpressive) {
		when (it) {
			true -> DynamicMaterialExpressiveTheme(
				seedColor = seedColor,
				motionScheme = MotionScheme.expressive(),
				isDark = isDark,
				specVersion = ColorSpec.SpecVersion.SPEC_2025,
				animate = true,
				content = content,
			)

			else -> DynamicMaterialTheme(
				seedColor = seedColor,
				isDark = isDark,
				specVersion = ColorSpec.SpecVersion.SPEC_2025,
				animate = true,
				content = content,
			)
		}
	}
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