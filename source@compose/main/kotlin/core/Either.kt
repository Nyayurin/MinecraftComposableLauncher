package cn.yurin.mcl.core

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder


@Serializable(with = EitherSerializer::class)
sealed class Either<out L, out R> {
	data class Left<T>(val value: T) : Either<T, Nothing>()
	data class Right<R>(val value: R) : Either<Nothing, R>()
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
		val jsonDecoder = decoder as? JsonDecoder ?: error("Unsupported decoder: ${decoder::class}")
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