package cn.yurin.mcl.ui.localization

@JvmInline
value class LocalTextProperty(val key: String)

context(_: DestOrBuilderScope, _: Destination.Sign)
fun property(key: String) = LocalTextProperty(key)

context(context: Context, dest: Destination)
val LocalTextProperty.current
	get() = dest.endpoints[key]?.map[context.language]!!

context(dest: Destination)
fun LocalTextProperty.language(language: Language) = dest.endpoints[key]?.map[language]!!

data class LocalText(val map: Map<Language, String>)

class LocalTextBuilder {
	val map = mutableMapOf<Language, String>()
	fun build() = LocalText(map.toMap())
}