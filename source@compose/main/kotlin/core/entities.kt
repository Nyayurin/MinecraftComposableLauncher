package cn.yurin.minecraft_composable_launcher.core

data class Version(
	val name: String,
	val path: String,
	val manifest: VersionManifest,
)

data class Folder(
	val name: String,
	val path: String,
	val versions: List<Version>,
)