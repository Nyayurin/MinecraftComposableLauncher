package cn.yurin.mcl.ui

sealed class HomePage(val position: Int) {
	data object Launch : HomePage(0)
	data object Accounts : HomePage(1)
	data object Downloads : HomePage(2)
	data object Settings : HomePage(3)
	data object More : HomePage(4)
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