package cn.yurin.mcl.ui.localization

object TopbarDest : Destination.Sign

context(_: DestOrBuilderScope, _: TopbarDest)
val launch get() = property("launch")

context(_: DestOrBuilderScope, _: TopbarDest)
val downloads get() = property("downloads")

context(_: DestOrBuilderScope, _: TopbarDest)
val settings get() = property("settings")

context(_: DestOrBuilderScope, _: TopbarDest)
val more get() = property("more")

object LaunchPageDest : Destination.Sign {
	object SideBar : Destination.Sign
	object Content : Destination.Sign

	object VersionSelectPage : Destination.Sign {
		object SideBar : Destination.Sign
		object Content : Destination.Sign
	}
}

context(_: DestOrBuilderScope, _: LaunchPageDest.SideBar)
val online get() = property("online")

context(_: DestOrBuilderScope, _: LaunchPageDest.SideBar)
val offline get() = property("offline")

context(_: DestOrBuilderScope, _: LaunchPageDest.SideBar)
val launch get() = property("launch")

context(_: DestOrBuilderScope, _: LaunchPageDest.SideBar)
val selectVersion get() = property("selectVersion")

context(_: DestOrBuilderScope, _: LaunchPageDest.SideBar)
val versions get() = property("versions")

context(_: DestOrBuilderScope, _: LaunchPageDest.SideBar)
val settings get() = property("settings")

context(_: DestOrBuilderScope, _: LaunchPageDest.VersionSelectPage.SideBar)
val importFolder get() = property("importFolder")

context(_: DestOrBuilderScope, _: LaunchPageDest.VersionSelectPage.Content)
val regularVersion get() = property("regularVersion")

context(_: DestOrBuilderScope, _: LaunchPageDest.VersionSelectPage.Content)
val info get() = property("info")

object DownloadsPageDest : Destination.Sign {
	object SideBar : Destination.Sign
	object Content : Destination.Sign {
		object Vanilla : Destination.Sign
	}
	object DownloadAlert : Destination.Sign
}

context(_: DestOrBuilderScope, _: DownloadsPageDest.SideBar)
val vanilla get() = property("vanilla")

context(_: DestOrBuilderScope, _: DownloadsPageDest.Content.Vanilla)
val latest get() = property("latest")

context(_: DestOrBuilderScope, _: DownloadsPageDest.Content.Vanilla)
val release get() = property("release")

context(_: DestOrBuilderScope, _: DownloadsPageDest.Content.Vanilla)
val snapshot get() = property("snapshot")

context(_: DestOrBuilderScope, _: DownloadsPageDest.Content.Vanilla)
val oldBeta get() = property("oldBeta")

context(_: DestOrBuilderScope, _: DownloadsPageDest.Content.Vanilla)
val oldAlpha get() = property("oldAlpha")

context(_: DestOrBuilderScope, _: DownloadsPageDest.Content.Vanilla)
val releaseAt get() = property("releaseAt")

context(_: DestOrBuilderScope, _: DownloadsPageDest.DownloadAlert)
val titleDownloading get() = property("titleDownloading")

context(_: DestOrBuilderScope, _: DownloadsPageDest.DownloadAlert)
val titleDownloaded get() = property("titleDownloaded")

context(_: DestOrBuilderScope, _: DownloadsPageDest.DownloadAlert)
val manifest get() = property("manifest")

context(_: DestOrBuilderScope, _: DownloadsPageDest.DownloadAlert)
val client get() = property("client")

context(_: DestOrBuilderScope, _: DownloadsPageDest.DownloadAlert)
val libraries get() = property("libraries")

context(_: DestOrBuilderScope, _: DownloadsPageDest.DownloadAlert)
val assetIndex get() = property("assetIndex")

context(_: DestOrBuilderScope, _: DownloadsPageDest.DownloadAlert)
val assets get() = property("assets")

context(_: DestOrBuilderScope, _: DownloadsPageDest.DownloadAlert)
val confirm get() = property("confirm")

object SettingsPageDest : Destination.Sign {
	object SideBar : Destination.Sign
	object Content : Destination.Sign {
		object Launch : Destination.Sign
		object Personalization : Destination.Sign {
			object ColorPicker : Destination.Sign
			object Language : Destination.Sign
		}

		object More : Destination.Sign
	}
}

context(_: DestOrBuilderScope, _: SettingsPageDest.SideBar)
val launch get() = property("launch")

context(_: DestOrBuilderScope, _: SettingsPageDest.SideBar)
val personalization get() = property("personalization")

context(_: DestOrBuilderScope, _: SettingsPageDest.SideBar)
val more get() = property("more")

context(_: DestOrBuilderScope, _: SettingsPageDest.Content.Personalization)
val theme get() = property("theme")

context(_: DestOrBuilderScope, _: SettingsPageDest.Content.Personalization)
val language get() = property("language")

context(_: DestOrBuilderScope, _: SettingsPageDest.Content.Personalization.ColorPicker)
val darkMode get() = property("darkMode")

context(_: DestOrBuilderScope, _: SettingsPageDest.Content.Personalization.Language)
val chineseLang get() = property("chinese")

context(_: DestOrBuilderScope, _: SettingsPageDest.Content.Personalization.Language)
val englishLang get() = property("english")