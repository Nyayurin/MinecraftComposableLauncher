package cn.yurin.mcl.storage

import com.russhwolf.settings.PropertiesSettings
import com.russhwolf.settings.Settings
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

fun getAppDataDir(path: String): File {
	val os = System.getProperty("os.name").lowercase()

	val file = when {
		os.contains("win") -> {
			File(System.getenv("APPDATA"), "mcl/$path.json")
		}

		os.contains("mac") -> {
			File(System.getProperty("user.home"), "Library/Application Support/mcl/$path.json")
		}
		// Linux
		else -> {
			File(System.getProperty("user.home"), ".config/mcl/$path.json")
		}
	}
	if (!file.parentFile.exists()) {
		file.parentFile.mkdirs()
	}
	if (!file.exists()) {
		file.createNewFile()
	}
	return file
}

actual fun getSettings(path: String): Settings = PropertiesSettings(
	Properties().apply {
		load(FileInputStream(getAppDataDir(path)))
	}
) {
	it.store(FileOutputStream(getAppDataDir(path)), null)
}