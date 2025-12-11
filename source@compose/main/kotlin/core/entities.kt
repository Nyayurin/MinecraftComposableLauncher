package cn.yurin.mcl.core

import kotlinx.serialization.Serializable

data class Version(
	val name: String,
	val path: String,
	val manifest: VersionManifest,
)

sealed class GameFolder {
	abstract val name: String
	abstract val path: String
	abstract val versions: List<Version>

	data class DotMinecraft(
		override val name: String,
		override val path: String,
		override val versions: List<Version>,
	) : GameFolder()

	data class MCL(
		override val name: String,
		override val path: String,
		override val versions: List<Version>,
	) : GameFolder()
}

@Serializable
data class AssetIndex(
	val objects: Map<String, Item>,
) {
	@Serializable
	data class Item(
		val hash: String,
		val size: Int,
	)
}