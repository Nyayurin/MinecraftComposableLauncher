package cn.yurin.mcl.ui.localization.destination

import cn.yurin.mcl.ui.localization.DestOrBuilderScope
import cn.yurin.mcl.ui.localization.Destination
import cn.yurin.mcl.ui.localization.property

object SettingsDest : Destination.Sign {
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

context(_: DestOrBuilderScope, _: SettingsDest.SideBar)
val launch get() = property("launch")

context(_: DestOrBuilderScope, _: SettingsDest.SideBar)
val personalization get() = property("personalization")

context(_: DestOrBuilderScope, _: SettingsDest.Content.Personalization)
val theme get() = property("theme")

context(_: DestOrBuilderScope, _: SettingsDest.Content.Personalization)
val language get() = property("language")

context(_: DestOrBuilderScope, _: SettingsDest.Content.Personalization.ColorPicker)
val darkMode get() = property("darkMode")

context(_: DestOrBuilderScope, _: SettingsDest.Content.Personalization.Language)
val chineseLang get() = property("chinese")

context(_: DestOrBuilderScope, _: SettingsDest.Content.Personalization.Language)
val englishLang get() = property("english")