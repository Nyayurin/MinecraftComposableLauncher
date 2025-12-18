package cn.yurin.mcl.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VersionManifest(
	val arguments: Arguments,
	val assetIndex: AssetIndex,
	val assets: String,
	val complianceLevel: Int,
	val downloads: Downloads,
	val id: String,
	val javaVersion: JavaVersion,
	val libraries: List<Library>,
	val logging: Logging,
	val mainClass: String,
	val minimumLauncherVersion: Int,
	val releaseTime: String,
	val time: String,
	val type: String,
) {
	@Serializable
	data class Arguments(
		val game: List<Either<String, Argument>>,
		val jvm: List<Either<String, Argument>>,
	) {
		@Serializable
		data class Argument(
			val rules: List<Rule>,
			val value: Either<String, List<String>>,
		)
	}

	@Serializable
	data class AssetIndex(
		val id: String,
		val sha1: String,
		val size: Int,
		val totalSize: Int,
		val url: String,
	)

	@Serializable
	data class Downloads(
		val client: Download,
		@SerialName("client_mappings")
		val clientMappings: Download? = null,
		val server: Download,
		@SerialName("server_mappings")
		val serverMappings: Download? = null,
	) {
		@Serializable
		data class Download(
			val sha1: String,
			val size: Int,
			val url: String,
		)
	}

	@Serializable
	data class JavaVersion(
		val component: String,
		val majorVersion: Int,
	)

	@Serializable
	data class Library(
		val downloads: Downloads? = null,
		val url: String? = null,
		val name: String,
		val rule: Rule? = null,
	) {
		@Serializable
		data class Downloads(
			val artifact: Artifact,
		) {
			@Serializable
			data class Artifact(
				val path: String,
				val sha1: String,
				val size: Int,
				val url: String,
			)
		}
	}

	@Serializable
	data class Logging(
		val client: Client,
	) {
		@Serializable
		data class Client(
			val arguments: String? = null,
			val file: File,
			val type: String,
		) {
			@Serializable
			data class File(
				val id: String,
				val sha1: String,
				val size: Int,
				val url: String,
			)
		}
	}

	@Serializable
	data class Rule(
		val action: String,
		val os: Os? = null,
		val features: Map<String, Boolean>? = null,
	) {
		@Serializable
		data class Os(
			val name: String? = null,
			val arch: String? = null,
		)
	}
}