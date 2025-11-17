package cn.yurin.minecraft_composable_launcher.localization

fun initContext() = buildContext {
	language = Language.Chinese
	initTopbar()
	initLaunchSidebar()
}

context(_: ContextBuilder)
fun initTopbar() = TopbarDest {
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
fun initLaunchSidebar() = LaunchSidebarDest {
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