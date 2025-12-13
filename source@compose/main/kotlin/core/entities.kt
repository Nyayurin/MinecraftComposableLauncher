package cn.yurin.mcl.core

import kotlinx.serialization.Serializable

@Serializable
data class Version(
	val name: String,
	val path: String,
	val manifest: VersionManifest,
)

@Serializable
sealed class GameFolder {
	abstract val name: String
	abstract val path: String
	abstract val versions: List<Version>

	@Serializable
	data class DotMinecraft(
		override val name: String,
		override val path: String,
		override val versions: List<Version>,
	) : GameFolder()

	@Serializable
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

@Serializable
sealed class Account {
	abstract val name: String
	abstract val token: String
	abstract val uuid: String

	@Serializable
	data class Online(
		override val name: String,
		override val token: String,
		override val uuid: String,
		val accessToken: String,
		val refreshToken: String?,
	) : Account()

	@Serializable
	data class Offline(
		override val name: String,
		override val token: String,
		override val uuid: String,
	) : Account()
}