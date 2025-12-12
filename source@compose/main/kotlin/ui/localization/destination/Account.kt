package cn.yurin.mcl.ui.localization.destination

import cn.yurin.mcl.ui.localization.DestOrBuilderScope
import cn.yurin.mcl.ui.localization.Destination
import cn.yurin.mcl.ui.localization.property

object AccountsDest : Destination.Sign {
	object SideBar : Destination.Sign
	object Content : Destination.Sign
	object LoginDialog : Destination.Sign
}

context(_: DestOrBuilderScope, _: AccountsDest.SideBar)
val loginAccount get() = property("loginAccount")

context(_: DestOrBuilderScope, _: AccountsDest.SideBar)
val onlineAccount get() = property("onlineAccount")

context(_: DestOrBuilderScope, _: AccountsDest.SideBar)
val offlineAccount get() = property("offlineAccount")

context(_: DestOrBuilderScope, _: AccountsDest.Content)
val onlineAccount get() = property("onlineAccount")

context(_: DestOrBuilderScope, _: AccountsDest.Content)
val offlineAccount get() = property("offlineAccount")

context(_: DestOrBuilderScope, _: AccountsDest.LoginDialog)
val title get() = property("title")

context(_: DestOrBuilderScope, _: AccountsDest.LoginDialog)
val content get() = property("content")

context(_: DestOrBuilderScope, _: AccountsDest.LoginDialog)
val login get() = property("login")

context(_: DestOrBuilderScope, _: AccountsDest.LoginDialog)
val cancel get() = property("cancel")