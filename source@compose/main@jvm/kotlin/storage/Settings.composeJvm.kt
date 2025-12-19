package cn.yurin.mcl.storage

import cn.yurin.mcl.core.OperationSystem
import cn.yurin.mcl.core.PreConfiguration
import com.russhwolf.settings.PropertiesSettings
import com.russhwolf.settings.Settings
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

fun getAppDataDir(path: String): File {
	val file = when (PreConfiguration.system) {
		OperationSystem.Windows -> {
			File(System.getenv("APPDATA"), "mcl/$path.json")
		}

		OperationSystem.Macos -> {
			File(System.getProperty("user.home"), "Library/Application Support/mcl/$path.json")
		}

		OperationSystem.Linux -> {
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