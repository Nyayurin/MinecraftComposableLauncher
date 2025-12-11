package cn.yurin.mcl.network

import kotlinx.serialization.Serializable

@Serializable
data class VersionsManifest(
	val latest: Latest,
	val versions: List<Version>,
) {
	@Serializable
	data class Latest(
		val release: String,
		val snapshot: String,
	)

	@Serializable
	data class Version(
		val id: String,
		val type: String,
		val url: String,
		val time: String,
		val releaseTime: String,
	)
}