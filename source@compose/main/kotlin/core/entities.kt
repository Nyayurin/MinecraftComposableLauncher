package cn.yurin.minecraft_composable_launcher.core

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