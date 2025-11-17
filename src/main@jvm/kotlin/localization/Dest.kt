package cn.yurin.minecraft_composable_launcher.localization

object TopbarDest : Destination.Sign
object LaunchSidebarDest : Destination.Sign

context(_: DestOrBuilderScope, _: TopbarDest)
val launch get() = property("launch")

context(_: DestOrBuilderScope, _: TopbarDest)
val downloads get() = property("downloads")

context(_: DestOrBuilderScope, _: TopbarDest)
val settings get() = property("settings")

context(_: DestOrBuilderScope, _: TopbarDest)
val more get() = property("more")

context(_: DestOrBuilderScope, _: LaunchSidebarDest)
val online get() = property("online")

context(_: DestOrBuilderScope, _: LaunchSidebarDest)
val offline get() = property("offline")

context(_: DestOrBuilderScope, _: LaunchSidebarDest)
val launch get() = property("launch")

context(_: DestOrBuilderScope, _: LaunchSidebarDest)
val versions get() = property("versions")

context(_: DestOrBuilderScope, _: LaunchSidebarDest)
val settings get() = property("settings")