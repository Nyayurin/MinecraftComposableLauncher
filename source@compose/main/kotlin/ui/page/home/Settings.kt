package cn.yurin.mcl.ui.page.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import cn.yurin.mcl.core.Data
import cn.yurin.mcl.ui.SettingsPage
import cn.yurin.mcl.ui.localization.Context
import cn.yurin.mcl.ui.localization.Language
import cn.yurin.mcl.ui.localization.dest
import cn.yurin.mcl.ui.localization.destination.*
import cn.yurin.mcl.ui.localization.language
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
context(_: Context, _: Data)
fun Settings() = dest(SettingsDest) {
	Row {
		var currentPage by remember { mutableStateOf<SettingsPage>(SettingsPage.Launch) }
		Sidebar(
			currentPage = currentPage,
			onPageChanged = { currentPage = it },
		)
		Content(
			currentPage = currentPage,
		)
	}
}

@Composable
context(context: Context, _: Data)
private fun RowScope.Sidebar(
	currentPage: SettingsPage,
	onPageChanged: (SettingsPage) -> Unit,
) = dest(SettingsDest.SideBar) {
	val pages = listOf(SettingsPage.Launch, SettingsPage.Personalization)
	NavigationRail(
		containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.15F),
	) {
		pages.forEach { page ->
			NavigationRailItem(
				selected = currentPage == page,
				onClick = { onPageChanged(page) },
				icon = {},
				label = {
					AnimatedContent(context.language) {
						Text(
							text = when (page) {
								SettingsPage.Launch -> launch
								SettingsPage.Personalization -> personalization
							}.language(it),
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
	currentPage: SettingsPage,
) = dest(SettingsDest.Content) {
	AnimatedContent(
		targetState = currentPage,
		transitionSpec = {
			slideIn(tween()) {
				IntOffset(0, (targetState.position compareTo initialState.position) * it.height)
			} togetherWith slideOut(tween()) {
				IntOffset(0, (initialState.position compareTo targetState.position) * it.height)
			}
		},
		modifier = Modifier
			.fillMaxHeight()
			.weight(0.85F)
			.padding(horizontal = 32.dp),
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(32.dp),
			modifier = Modifier.verticalScroll(rememberScrollState()),
		) {
			Spacer(modifier = Modifier.height(0.dp))
			when (it) {
				SettingsPage.Launch -> Launch()
				SettingsPage.Personalization -> Personalization()
			}
			Spacer(modifier = Modifier.height(0.dp))
		}
	}
}

@Composable
context(_: Context, _: Data)
private fun Launch() = dest(SettingsDest.Content.Launch) {
}

@Composable
context(context: Context, data: Data)
private fun Personalization() = dest(SettingsDest.Content.Personalization) {
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
				initialColor = data.seedColor,
				onColorChanged = { data.seedColor = it.color },
				initialDark = data.isDarkMode,
				onDarkChanged = { data.isDarkMode = it },
				initialExpressive = data.isExpressive,
				onExpressiveChanged = { data.isExpressive = it },
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
			dest(SettingsDest.Content.Personalization.Language) {
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
									Language.Chinese -> chineseLang.language(it)
									Language.English -> englishLang.language(it)
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
	initialColor: Color,
	onColorChanged: (ColorEnvelope) -> Unit,
	initialDark: Boolean,
	onDarkChanged: (Boolean) -> Unit,
	initialExpressive: Boolean,
	onExpressiveChanged: (Boolean) -> Unit,
) = dest(SettingsDest.Content.Personalization.ColorPicker) {
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
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier = Modifier.fillMaxWidth()
	) {
		AnimatedContent(context.language) {
			Text(
				text = expressive.language(it),
				color = MaterialTheme.colorScheme.onSurface,
				style = MaterialTheme.typography.bodyLarge,
			)
		}
		Switch(
			checked = initialExpressive,
			onCheckedChange = onExpressiveChanged,
		)
	}
}