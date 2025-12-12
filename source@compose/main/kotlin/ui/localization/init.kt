package cn.yurin.mcl.ui.localization

import androidx.compose.runtime.mutableStateOf

fun initContext() = buildContext {
	language = mutableStateOf(Language.Chinese).value
	initTopbar()
	initLaunchPage()
	initAccountsPage()
	initDownloadsPage()
	initSettingsPage()
}

context(_: ContextBuilder)
private fun initTopbar() = TopbarDest {
	launch {
		chinese = "启动"
		english = "Launch"
	}
	accounts {
		chinese = "账户"
		english = "Accounts"
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
private fun initAccountsPage() = AccountsPageDest {
	AccountsPageDest.SideBar {
		loginAccount {
			chinese = "登录账户"
			english = "Login Account"
		}
		onlineAccount {
			chinese = "正版账户"
			english = "Online Account"
		}
		offlineAccount {
			chinese = "离线账户"
			english = "Offline Account"
		}
	}

	AccountsPageDest.Content {
		onlineAccount {
			chinese = "正版账户"
			english = "Online Account"
		}
		offlineAccount {
			chinese = "离线账户"
			english = "Offline Account"
		}
	}

	AccountsPageDest.LoginDialog {
		title {
			chinese = "登录微软账户"
			english = "Login Microsoft Account"
		}
		content {
			chinese = """
				请按以下步骤登录账户:
				  1. 点击 "登录" 按钮
				  2. 在弹出的网页中输入登录代码(MCL 会自动复制到剪贴板, 你只需要在输入框内粘贴即可), 并点击 "允许访问"
				  3. 按照网站的提示登录
				  4. 当网站提示 "是否允许此应用访问你的信息?" 时, 请点击 "接受"
				  5. 在网站提示 "大功告成" 后, 关闭网页回到 MCL 等待一段时间即可登录成功
				若网站提示 "出现错误" 或账户添加失败时, 请按照以上步骤重新登录
				若设备网络环境不佳, 可能会导致网页加载缓慢甚至无法加载, 请使用网络代理并重试
			""".trimIndent()
			english = """
				请按以下步骤登录账户:
				  1. 点击 "登录" 按钮
				  2. 在弹出的网页中输入登录代码(MCL 会自动复制到剪贴板, 你只需要在输入框内粘贴即可), 并点击 "允许访问"
				  3. 按照网站的提示登录
				  4. 当网站提示 "是否允许此应用访问你的信息?" 时, 请点击 "接受"
				  5. 在网站提示 "大功告成" 后, 关闭网页回到 MCL 等待一段时间即可登录成功
				若网站提示 "出现错误" 或账户添加失败时, 请按照以上步骤重新登录
				若设备网络环境不佳, 可能会导致网页加载缓慢甚至无法加载, 请使用网络代理并重试
			""".trimIndent()
		}
		login {
			chinese = "登录"
			english = "Login"
		}
		cancel {
			chinese = "取消"
			english = "Cancel"
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

	DownloadsPageDest.DownloadDialog {
		titleDownloading {
			chinese = "下载版本中..."
			english = "Downloading version..."
		}
		titleDownloaded {
			chinese = "下载版本完成"
			english = "Download version finished"
		}
		manifest {
			chinese = "清单"
			english = "Manifest"
		}
		client {
			chinese = "客户端"
			english = "Client"
		}
		libraries {
			chinese = "依赖库"
			english = "Libraries"
		}
		assetIndex {
			chinese = "资源索引"
			english = "Asset Index"
		}
		assets {
			chinese = "资源"
			english = "Assets"
		}
		confirm {
			chinese = "确认"
			english = "Confirm"
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