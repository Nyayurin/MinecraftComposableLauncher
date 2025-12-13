package cn.yurin.mcl.storage

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cn.yurin.mcl.core.Account
import cn.yurin.mcl.core.Data
import cn.yurin.mcl.core.GameFolder
import cn.yurin.mcl.core.refreshFolders
import kotlinx.serialization.json.Json

private val settings = getSettings("data")

context(data: Data)
fun saveData() {
	runCatching {
		settings.putInt("seedColor", data.seedColor.toArgb())
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
	runCatching {
		data.isDarkMode?.let { settings.putBoolean("isDarkMode", it) }
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
	runCatching {
		settings.putString("folders", data.json.encodeToString(data.folders))
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
	runCatching {
		data.currentFolder?.let { settings.putInt("currentFolder", data.folders.indexOf(it)) }
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
	runCatching {
		data.currentVersion?.let { settings.putInt("currentVersion", data.currentFolder!!.versions.indexOf(it)) }
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
	runCatching {
		settings.putString("accounts", data.json.encodeToString(data.accounts))
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
	runCatching {
		data.currentAccount?.let { settings.putInt("currentAccount", data.accounts.indexOf(it)) }
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
}

fun readData() = Data().apply {
	runCatching {
		settings.getIntOrNull("seedColor")?.let {
			seedColor = Color(it)
		}
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
	runCatching {
		settings.getBooleanOrNull("isDarkMode")?.let {
			isDarkMode = it
		}
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
	runCatching {
		settings.getStringOrNull("folders")?.let {
			folders = Json.decodeFromString<List<GameFolder>>(it)
		}
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
	runCatching {
		settings.getIntOrNull("currentFolder")?.let {
			currentFolder = folders[it]
		}
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
	runCatching {
		settings.getIntOrNull("currentVersion")?.let {
			currentVersion = currentFolder?.versions?.get(it)
		}
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
	runCatching {
		settings.getStringOrNull("accounts")?.let {
			accounts = Json.decodeFromString<List<Account>>(it)
		}
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
	runCatching {
		settings.getIntOrNull("currentAccount")?.let {
			currentAccount = accounts[it]
		}
	}.onFailure { e ->
		println(e)
		e.printStackTrace()
	}
	refreshFolders()
}