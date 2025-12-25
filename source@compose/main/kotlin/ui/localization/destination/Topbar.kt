package cn.yurin.mcl.ui.localization.destination

import cn.yurin.mcl.ui.localization.DestOrBuilderScope
import cn.yurin.mcl.ui.localization.Destination
import cn.yurin.mcl.ui.localization.property

object NavigationBar : Destination.Sign

context(_: DestOrBuilderScope, _: NavigationBar)
val launch get() = property("launch")

context(_: DestOrBuilderScope, _: NavigationBar)
val accounts get() = property("accounts")

context(_: DestOrBuilderScope, _: NavigationBar)
val downloads get() = property("downloads")

context(_: DestOrBuilderScope, _: NavigationBar)
val settings get() = property("settings")

context(_: DestOrBuilderScope, _: NavigationBar)
val others get() = property("others")