package cn.yurin.mcl.ui

sealed class HomePage(val position: Int) {
	data object Launch : HomePage(0)
	data object Games : HomePage(1)
	data object Accounts : HomePage(2)
	data object Downloads : HomePage(3)
	data object Settings : HomePage(4)
	data object Others : HomePage(5)
}

sealed class LaunchPages(val position: Int) {
	data object Home : LaunchPages(0)
	data object Versions : LaunchPages(1)
	data object Settings : LaunchPages(1)
}

sealed class DownloadsPage(val position: Int) {
	data object Vanilla : DownloadsPage(0)
}

sealed class SettingsPage(val position: Int) {
	data object Launch : SettingsPage(0)
	data object Personalization : SettingsPage(1)
}