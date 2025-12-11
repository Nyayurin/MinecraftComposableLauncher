package cn.yurin.mcl.ui.localization

import androidx.compose.runtime.mutableStateOf

fun initContext() = buildContext {
	language = mutableStateOf(Language.Chinese).value
	initTopbar()
	initLaunchPage()
	initDownloadsPage()
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
		selectVersion {
			chinese = "选择版本"
			english = "Select Version"
		}
		versions {
			chinese = "版本"
			english = "Versions"
		}
		settings {
			chinese = "设置"
			english = "Settings"
		}
	}

	LaunchPageDest.Content {

	}

	LaunchPageDest.VersionSelectPage {
		LaunchPageDest.VersionSelectPage.SideBar {
			importFolder {
				chinese = "导入文件夹"
				english = "Import Folder"
			}
		}

		LaunchPageDest.VersionSelectPage.Content {
			info {
				chinese = "信息"
				english = "Info"
			}
			regularVersion {
				chinese = "常规版本"
				english = "Regular Version"
			}
		}
	}
}

context(_: ContextBuilder)
private fun initDownloadsPage() = DownloadsPageDest {
	DownloadsPageDest.SideBar {
		vanilla {
			chinese = "原版"
			english = "Vanilla"
		}
	}

	DownloadsPageDest.Content {
		DownloadsPageDest.Content.Vanilla {
			latest {
				chinese = "最新版"
				english = "Latest"
			}
			release {
				chinese = "发行版"
				english = "Release"
			}
			snapshot {
				chinese = "快照版"
				english = "Snapshot"
			}
			oldBeta {
				chinese = "测试版"
				english = "Old Beta"
			}
			oldAlpha {
				chinese = "初始版"
				english = "Old Alpha"
			}
			releaseAt {
				chinese = "发行于"
				english = "Release at"
			}
		}
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