package cn.yurin.minecraft_composable_launcher.localization

enum class Language {
	Chinese,
	English,
}

context(builder: LocalTextBuilder)
var chinese
	get() = builder.map[Language.Chinese] ?: ""
	set(value) {
		builder.map[Language.Chinese] = value
	}

context(builder: LocalTextBuilder)
var english
	get() = builder.map[Language.English] ?: ""
	set(value) {
		builder.map[Language.English] = value
	}