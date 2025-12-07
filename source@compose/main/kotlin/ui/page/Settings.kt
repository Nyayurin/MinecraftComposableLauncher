package cn.yurin.minecraft_composable_launcher.ui.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cn.yurin.minecraft_composable_launcher.core.Data
import cn.yurin.minecraft_composable_launcher.core.isDarkMode
import cn.yurin.minecraft_composable_launcher.core.seedColor
import cn.yurin.minecraft_composable_launcher.ui.localization.language
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import cn.yurin.minecraft_composable_launcher.ui.localization.Context
import cn.yurin.minecraft_composable_launcher.ui.localization.Language
import cn.yurin.minecraft_composable_launcher.ui.localization.SettingsPageDest
import cn.yurin.minecraft_composable_launcher.ui.localization.chineseLang
import cn.yurin.minecraft_composable_launcher.ui.localization.darkMode
import cn.yurin.minecraft_composable_launcher.ui.localization.dest
import cn.yurin.minecraft_composable_launcher.ui.localization.englishLang
import cn.yurin.minecraft_composable_launcher.ui.localization.launch
import cn.yurin.minecraft_composable_launcher.ui.localization.more
import cn.yurin.minecraft_composable_launcher.ui.localization.personalization
import cn.yurin.minecraft_composable_launcher.ui.localization.theme

@Composable
context(_: Context, _: Data)
fun SettingsPage() = dest(SettingsPageDest) {
	Row {
		var selection by remember { mutableIntStateOf(0) }
		Sidebar(
			currentPage = selection,
			onPageChanged = { selection = it },
		)
		Content(
			currentPage = selection,
		)
	}
}

@Composable
context(context: Context, _: Data)
private fun RowScope.Sidebar(
	currentPage: Int,
	onPageChanged: (Int) -> Unit,
) = dest(SettingsPageDest.SideBar) {
	val pages = listOf(launch, personalization, more)
	NavigationRail(
		containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.15F),
	) {
		pages.forEachIndexed { index, page ->
			NavigationRailItem(
				selected = currentPage == index,
				onClick = { onPageChanged(index) },
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
			)
		}
	}
}

@Composable
context(_: Context, _: Data)
private fun RowScope.Content(
	currentPage: Int,
) = dest(SettingsPageDest.Content) {
	AnimatedContent(
		targetState = currentPage,
		transitionSpec = {
			slideIn(tween()) { IntOffset(0, (targetState compareTo initialState) * it.height) } togetherWith
					slideOut(tween()) { IntOffset(0, (initialState compareTo targetState) * it.height) }
		},
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.85F)
			.padding(horizontal = 32.dp),
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(32.dp),
			modifier = Modifier
				.verticalScroll(rememberScrollState()),
		) {
			Spacer(modifier = Modifier.height(0.dp))
			when (it) {
				0 -> Launch()
				1 -> Personalization()
				2 -> More()
			}
			Spacer(modifier = Modifier.height(0.dp))
		}
	}
}

@Composable
context(_: Context, _: Data)
private fun Launch() = dest(SettingsPageDest.Content.Launch) {
}

@Composable
context(context: Context, _: Data)
private fun Personalization() = dest(SettingsPageDest.Content.Personalization) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(32.dp),
	) {
		Card(
			title = {
				AnimatedContent(context.language) {
					Text(
						text = theme.language(it),
						color = MaterialTheme.colorScheme.onSurface,
						style = MaterialTheme.typography.headlineSmall,
					)
				}
			},
			modifier = Modifier.weight(0.5F),
		) {
			ColorPicker(
				onColorChanged = { seedColor = it.color },
				initialColor = seedColor,
				onDarkChanged = { isDarkMode = it },
				initialDark = isDarkMode ?: isSystemInDarkTheme(),
			)
		}
		Card(
			title = {
				AnimatedContent(context.language) {
					Text(
						text = language.language(it),
						color = MaterialTheme.colorScheme.onSurface,
						style = MaterialTheme.typography.headlineSmall,
					)
				}
			},
			modifier = Modifier.weight(0.5F),
		) {
			dest(SettingsPageDest.Content.Personalization.Language) {
				Language.entries.forEach { language ->
					Row(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(16.dp),
					) {
						RadioButton(
							selected = context.language == language,
							onClick = { context.language = language }
						)
						AnimatedContent(context.language) {
							Text(
								text = when (language) {
									Language.Chinese -> chineseLang.language(
										it
									)

									Language.English -> englishLang.language(
										it
									)
								},
								color = MaterialTheme.colorScheme.onSurface,
								style = MaterialTheme.typography.bodyLarge,
							)
						}
					}
				}
			}
		}
	}
}

@Composable
context(_: Context, _: Data)
private fun More() = dest(SettingsPageDest.Content.More) {

}

@Composable
context(_: Context, _: Data)
private fun Card(
	title: @Composable () -> Unit,
	modifier: Modifier = Modifier.fillMaxWidth(),
	content: @Composable context(Context) ColumnScope.() -> Unit,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.clip(RoundedCornerShape(16.dp))
			.background(MaterialTheme.colorScheme.surfaceContainerHighest)
			.padding(16.dp),
	) {
		title()
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
		) {
			content()
		}
	}
}

@Composable
context(context: Context, _: Data)
fun ColorPicker(
	onColorChanged: (ColorEnvelope) -> Unit,
	initialColor: Color,
	onDarkChanged: (Boolean) -> Unit,
	initialDark: Boolean,
) = dest(SettingsPageDest.Content.Personalization.ColorPicker) {
	val controller = rememberColorPickerController()
	HsvColorPicker(
		controller = controller,
		onColorChanged = onColorChanged,
		initialColor = initialColor,
		modifier = Modifier.size(200.dp),
	)
	BrightnessSlider(
		controller = controller,
		modifier = Modifier
			.fillMaxWidth()
			.height(20.dp),
	)
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier = Modifier.fillMaxWidth()
	) {
		AnimatedContent(context.language) {
			Text(
				text = darkMode.language(it),
				color = MaterialTheme.colorScheme.onSurface,
				style = MaterialTheme.typography.bodyLarge,
			)
		}
		Switch(
			checked = initialDark,
			onCheckedChange = onDarkChanged,
		)
	}
}