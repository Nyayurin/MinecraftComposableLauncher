package cn.yurin.mcl.storage

import cn.yurin.mcl.ui.localization.Context
import cn.yurin.mcl.ui.localization.ContextBuilder
import cn.yurin.mcl.ui.localization.Language
import cn.yurin.mcl.ui.localization.language

private val settings = getSettings("context")

context(context: Context)
fun saveContext() {
	runCatching {
		settings.putString("language", context.language.name)
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
}

context(_: ContextBuilder)
fun readContext() {
	runCatching {
		settings.getStringOrNull("language")?.let {
			language = Language.valueOf(it)
		}
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
}