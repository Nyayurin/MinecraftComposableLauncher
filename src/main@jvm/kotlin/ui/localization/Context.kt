package cn.yurin.minecraft_composable_launcher.ui.localization

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

fun buildContext(block: context(ContextBuilder) () -> Unit) = ContextBuilder().apply(block).build()

class Context(
	private val destinations: Map<Destination.Sign, Destination>,
	language: Language,
) {
	var language: Language by mutableStateOf(language)
	operator fun <S : Destination.Sign> get(value: S) = destinations[value] as Destination?
}

context(context: Context)
inline fun <S: Destination.Sign> dest(sign: S, block: context(Context, Destination, S) () -> Unit) {
	context(context, context[sign]!!, sign) {
		block()
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