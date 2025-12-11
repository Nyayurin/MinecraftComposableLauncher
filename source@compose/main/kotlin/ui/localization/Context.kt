package cn.yurin.mcl.ui.localization

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun buildContext(block: context(ContextBuilder) () -> Unit) = ContextBuilder().apply(block).build()

class Context(
	private val destinations: Map<Destination.Sign, Destination>,
	language: Language,
) {
	var language: Language by mutableStateOf(language)
	operator fun <S : Destination.Sign> get(value: S) = destinations[value]
}

@OptIn(ExperimentalContracts::class)
context(context: Context)
inline fun <S : Destination.Sign, R> dest(sign: S, block: context(Context, Destination, S) () -> R): R {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}
	context(context, context[sign]!!, sign) {
		return block()
	}
}

context(builder: ContextBuilder)
var language
	get() = builder.language
	set(value) {
		builder.language = value
	}

context(builder: ContextBuilder)
operator fun <S : Destination.Sign> S.invoke(block: context(DestinationBuilder, S) () -> Unit) =
	builder.put(this, block)

class ContextBuilder {
	private val dest = mutableMapOf<Destination.Sign, Destination>()

	var language = Language.Chinese

	fun <S : Destination.Sign> put(sign: S, block: context(DestinationBuilder, S) () -> Unit) {
		dest[sign] = DestinationBuilder().apply { context(this, sign) { block() } }.build()
	}

	fun build() = Context(dest.toMap(), language)
}