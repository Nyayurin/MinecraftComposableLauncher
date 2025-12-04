package cn.yurin.minecraft_composable_launcher.ui.localization

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
}

context(_: DestOrBuilderScope, _: LaunchPageDest.SideBar)
val online get() = property("online")

context(_: DestOrBuilderScope, _: LaunchPageDest.SideBar)
val offline get() = property("offline")

context(_: DestOrBuilderScope, _: LaunchPageDest.SideBar)
val launch get() = property("launch")

context(_: DestOrBuilderScope, _: LaunchPageDest.SideBar)
val versions get() = property("versions")

context(_: DestOrBuilderScope, _: LaunchPageDest.SideBar)
val settings get() = property("settings")

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