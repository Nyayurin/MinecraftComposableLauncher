package cn.yurin.mcl.ui.localization.destination

import cn.yurin.mcl.ui.localization.DestOrBuilderScope
import cn.yurin.mcl.ui.localization.Destination
import cn.yurin.mcl.ui.localization.property

object DownloadsDest : Destination.Sign {
	object SideBar : Destination.Sign
	object Content : Destination.Sign {
		object Vanilla : Destination.Sign
	}
	object DownloadDialog : Destination.Sign
}

context(_: DestOrBuilderScope, _: DownloadsDest.SideBar)
val vanilla get() = property("vanilla")

context(_: DestOrBuilderScope, _: DownloadsDest.Content.Vanilla)
val latest get() = property("latest")

context(_: DestOrBuilderScope, _: DownloadsDest.Content.Vanilla)
val release get() = property("release")

context(_: DestOrBuilderScope, _: DownloadsDest.Content.Vanilla)
val snapshot get() = property("snapshot")

context(_: DestOrBuilderScope, _: DownloadsDest.Content.Vanilla)
val oldBeta get() = property("oldBeta")

context(_: DestOrBuilderScope, _: DownloadsDest.Content.Vanilla)
val oldAlpha get() = property("oldAlpha")

context(_: DestOrBuilderScope, _: DownloadsDest.Content.Vanilla)
val releaseAt get() = property("releaseAt")

context(_: DestOrBuilderScope, _: DownloadsDest.DownloadDialog)
val titleDownloading get() = property("titleDownloading")

context(_: DestOrBuilderScope, _: DownloadsDest.DownloadDialog)
val titleDownloaded get() = property("titleDownloaded")

context(_: DestOrBuilderScope, _: DownloadsDest.DownloadDialog)
val manifest get() = property("manifest")

context(_: DestOrBuilderScope, _: DownloadsDest.DownloadDialog)
val client get() = property("client")

context(_: DestOrBuilderScope, _: DownloadsDest.DownloadDialog)
val libraries get() = property("libraries")

context(_: DestOrBuilderScope, _: DownloadsDest.DownloadDialog)
val assetIndex get() = property("assetIndex")

context(_: DestOrBuilderScope, _: DownloadsDest.DownloadDialog)
val assets get() = property("assets")

context(_: DestOrBuilderScope, _: DownloadsDest.DownloadDialog)
val confirm get() = property("confirm")