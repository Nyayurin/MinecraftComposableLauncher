package cn.yurin.mcl.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.yurin.minecraftcomposablelauncher.generated.resources.MapleMono_NF_CN_Regular
import cn.yurin.minecraftcomposablelauncher.generated.resources.Res
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.dynamiccolor.ColorSpec
import io.github.iamcalledrob.smoothRoundedCornerShape.SmoothRoundedCornerShape
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Theme(
	seedColor: Color,
	isDark: Boolean,
	isExpressive: Boolean,
	content: @Composable () -> Unit,
) {
	val defaultTypography = MaterialTheme.typography
	var typography by remember { mutableStateOf<Typography?>(null) }

	run {
		val fontFamily = FontFamily(
			Font(
				resource = Res.font.MapleMono_NF_CN_Regular,
				weight = FontWeight.Normal,
				style = FontStyle.Normal,
			)
		)
		typography = Typography(
			defaultTypography.displayLarge.copy(fontFamily = fontFamily),
			defaultTypography.displayMedium.copy(fontFamily = fontFamily),
			defaultTypography.displaySmall.copy(fontFamily = fontFamily),
			defaultTypography.headlineLarge.copy(fontFamily = fontFamily),
			defaultTypography.headlineMedium.copy(fontFamily = fontFamily),
			defaultTypography.headlineSmall.copy(fontFamily = fontFamily),
			defaultTypography.titleLarge.copy(fontFamily = fontFamily),
			defaultTypography.titleMedium.copy(fontFamily = fontFamily),
			defaultTypography.titleSmall.copy(fontFamily = fontFamily),
			defaultTypography.bodyLarge.copy(fontFamily = fontFamily),
			defaultTypography.bodyMedium.copy(fontFamily = fontFamily),
			defaultTypography.bodySmall.copy(fontFamily = fontFamily),
			defaultTypography.labelLarge.copy(fontFamily = fontFamily),
			defaultTypography.labelMedium.copy(fontFamily = fontFamily),
			defaultTypography.labelSmall.copy(fontFamily = fontFamily),
		)
	}

	AnimatedContent(isExpressive) {
		when (it) {
			true -> DynamicMaterialExpressiveTheme(
				seedColor = seedColor,
				motionScheme = MotionScheme.expressive(),
				isDark = isDark,
				specVersion = ColorSpec.SpecVersion.SPEC_2025,
				typography = typography ?: defaultTypography,
				animate = true,
				content = content,
			)

			else -> DynamicMaterialTheme(
				seedColor = seedColor,
				isDark = isDark,
				specVersion = ColorSpec.SpecVersion.SPEC_2025,
				typography = typography ?: defaultTypography,
				animate = true,
				content = content,
			)
		}
	}
}

fun lightScrollbarStyle() = ScrollbarStyle(
	minimalHeight = 16.dp,
	thickness = 8.dp,
	shape = SmoothRoundedCornerShape(radius = 4.dp),
	hoverDurationMillis = 300,
	unhoverColor = Color.Black.copy(alpha = 0.125f),
	hoverColor = Color.Black.copy(alpha = 0.50f),
)

fun darkScrollbarStyle() = ScrollbarStyle(
	minimalHeight = 16.dp,
	thickness = 8.dp,
	shape = SmoothRoundedCornerShape(radius = 4.dp),
	hoverDurationMillis = 300,
	unhoverColor = Color.White.copy(alpha = 0.25f),
	hoverColor = Color.White.copy(alpha = 0.75f),
)