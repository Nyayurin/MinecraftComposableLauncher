package cn.yurin.mcl.ui.localization.destination

import cn.yurin.mcl.ui.localization.DestOrBuilderScope
import cn.yurin.mcl.ui.localization.Destination
import cn.yurin.mcl.ui.localization.property

object TopbarDest : Destination.Sign

context(_: DestOrBuilderScope, _: TopbarDest)
val launch get() = property("launch")

context(_: DestOrBuilderScope, _: TopbarDest)
val accounts get() = property("accounts")

context(_: DestOrBuilderScope, _: TopbarDest)
val downloads get() = property("downloads")

context(_: DestOrBuilderScope, _: TopbarDest)
val settings get() = property("settings")

context(_: DestOrBuilderScope, _: TopbarDest)
val more get() = property("more")