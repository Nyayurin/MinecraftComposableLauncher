package cn.yurin.minecraft_composable_launcher.ui.localization

import androidx.compose.runtime.mutableStateOf

fun initContext() = buildContext {
	language = mutableStateOf(Language.Chinese).value
	initTopbar()
	initLaunchPage()
	initSettingsPage()
}

context(_: ContextBuilder)
private fun initTopbar() = TopbarDest {
	launch {
		chinese = "启动"
		english = "Launch"
	}
	downloads {
		chinese = "下载"
		english = "Downloads"
	}
	settings {
		chinese = "设置"
		english = "Settings"
	}
	more {
		chinese = "更多"
		english = "More"
	}
}

context(_: ContextBuilder)
private fun initLaunchPage() = LaunchPageDest {
	LaunchPageDest.SideBar {
		online {
			chinese = "在线"
			english = "Online"
		}
		offline {
			chinese = "离线"
			english = "Offline"
		}
		launch {
			chinese = "启动"
			english = "Launch"
		}
		versions {
			chinese = "版本"
			english = "versions"
		}
		settings {
			chinese = "设置"
			english = "Settings"
		}
	}

	LaunchPageDest.Content {

	}
}

context(_: ContextBuilder)
private fun initSettingsPage() = SettingsPageDest {
	SettingsPageDest.SideBar {
		launch {
			chinese = "启动"
			english = "Launch"
		}
		personalization {
			chinese = "个性化"
			english = "Personalization"
		}
		more {
			chinese = "更多"
			english = "More"
		}
	}

	SettingsPageDest.Content {
		SettingsPageDest.Content.Launch {

		}

		SettingsPageDest.Content.Personalization {
			theme {
				chinese = "主题"
				english = "Theme"
			}
			language {
				chinese = "语言"
				english = "Language"
			}

			SettingsPageDest.Content.Personalization.ColorPicker {
				darkMode {
					chinese = "深色模式"
					english = "Dark mode"
				}
			}

			SettingsPageDest.Content.Personalization.Language {
				chineseLang {
					chinese = "中文"
					english = "Chinese"
				}
				englishLang {
					chinese = "英文"
					english = "English"
				}
			}
		}

		SettingsPageDest.Content.More {

		}
	}
}