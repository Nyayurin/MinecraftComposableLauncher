package cn.yurin.mcl.ui.localization

interface DestOrBuilderScope

class Destination(val endpoints: Map<String, LocalText>) : DestOrBuilderScope {
	interface Sign
}

context(builder: DestinationBuilder)
operator fun LocalTextProperty.invoke(block: context(LocalTextBuilder) () -> Unit) = builder.put(key, block)

class DestinationBuilder : DestOrBuilderScope {
	private val endpoints = mutableMapOf<String, LocalText>()

	fun put(endpoint: String, block: context(LocalTextBuilder) () -> Unit) {
		endpoints[endpoint] = LocalTextBuilder().apply(block).build()
	}

	fun build() = Destination(endpoints.toMap())
}