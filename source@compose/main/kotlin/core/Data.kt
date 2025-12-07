package cn.yurin.minecraft_composable_launcher.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import cn.yurin.minecraft_composable_launcher.network.VersionsManifest
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

class Data {
	var seedColor by mutableStateOf(Color(0xFF9B9D95))
	var isDarkMode by mutableStateOf<Boolean?>(null)
	var manifest by mutableStateOf<VersionsManifest?>(null)
	var gameStructure by mutableStateOf<GameStructure?>(null)

	val client = HttpClient(CIO) {
		install(ContentNegotiation) {
			json()
		}
	}
}

context(data: Data)
var seedColor
	get() = data.seedColor
	set(value) {
		data.seedColor = value
	}

context(data: Data)
var isDarkMode
	get() = data.isDarkMode
	set(value) {
		data.isDarkMode = value
	}

context(data: Data)
var manifest
	get() = data.manifest
	set(value) {
		data.manifest = value
	}

context(data: Data)
var gameStructure
	get() = data.gameStructure
	set(value) {
		data.gameStructure = value
	}

context(data: Data)
val client
	get() = data.client