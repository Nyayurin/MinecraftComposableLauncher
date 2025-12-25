package cn.yurin.mcl.ui.localization.destination

import cn.yurin.mcl.ui.localization.DestOrBuilderScope
import cn.yurin.mcl.ui.localization.Destination
import cn.yurin.mcl.ui.localization.property

object LaunchDest : Destination.Sign {
	object Content : Destination.Sign

	object Versions : Destination.Sign {
		object SideBar : Destination.Sign
		object Content : Destination.Sign
	}
}

context(_: DestOrBuilderScope, _: LaunchDest)
val loginAccount get() = property("loginAccount")

context(_: DestOrBuilderScope, _: LaunchDest)
val launch get() = property("launch")

context(_: DestOrBuilderScope, _: LaunchDest)
val selectVersion get() = property("selectVersion")

context(_: DestOrBuilderScope, _: LaunchDest)
val versions get() = property("versions")

context(_: DestOrBuilderScope, _: LaunchDest)
val settings get() = property("settings")

context(_: DestOrBuilderScope, _: LaunchDest)
val onlineAccount get() = property("onlineAccount")

context(_: DestOrBuilderScope, _: LaunchDest)
val offlineAccount get() = property("offlineAccount")

context(_: DestOrBuilderScope, _: LaunchDest.Versions.SideBar)
val importFolder get() = property("importFolder")

context(_: DestOrBuilderScope, _: LaunchDest.Versions.Content)
val regularVersion get() = property("regularVersion")

context(_: DestOrBuilderScope, _: LaunchDest.Versions.Content)
val info get() = property("info")