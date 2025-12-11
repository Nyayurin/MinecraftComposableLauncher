package cn.yurin.mcl.core

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder

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
		val game: List<Either<String, GameArgument>>,
		val jvm: List<Either<String, JvmArgument>>,
	) {
		@Serializable
		data class GameArgument(
			val rules: List<Rule>,
			val value: Either<String, List<String>>,
		)

		@Serializable
		data class JvmArgument(
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
		val clientMappings: Download,
		val server: Download,
		@SerialName("server_mappings")
		val serverMappings: Download,
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
	) {
		@Serializable
		data class Os(
			val name: String? = null,
		)
	}
}

@Serializable(with = EitherSerializer::class)
sealed class Either<out L, out R> {
	data class Left<T>(val value: T) : Either<T, Nothing>()
	data class Right<T>(val value: T) : Either<Nothing, T>()
}

class EitherSerializer<L, R>(
	private val leftSerializer: KSerializer<L>,
	private val rightSerializer: KSerializer<R>,
) : KSerializer<Either<L, R>> {
	@OptIn(InternalSerializationApi::class)
	override val descriptor = buildSerialDescriptor("Either", SerialKind.CONTEXTUAL)

	override fun serialize(
		encoder: Encoder,
		value: Either<L, R>,
	) {
		when (value) {
			is Either.Left<L> -> encoder.encodeSerializableValue(leftSerializer, value.value)
			is Either.Right<R> -> encoder.encodeSerializableValue(rightSerializer, value.value)
		}
	}

	override fun deserialize(decoder: Decoder): Either<L, R> {
		val jsonDecoder = decoder as? JsonDecoder
			?: error("Unsupported decoder: ${decoder::class}")
		val element = jsonDecoder.decodeJsonElement()
		val leftResult = runCatching {
			return Either.Left(jsonDecoder.json.decodeFromJsonElement(leftSerializer, element))
		}
		val rightResult = runCatching {
			return Either.Right(jsonDecoder.json.decodeFromJsonElement(rightSerializer, element))
		}
		throw RuntimeException("Either deserialize error: $element").apply {
			addSuppressed(leftResult.exceptionOrNull())
			addSuppressed(rightResult.exceptionOrNull())
		}
	}
}