package cn.yurin.minecraft_composable_launcher.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import cn.yurin.minecraft_composable_launcher.network.VersionsManifest
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class Data {
	var seedColor by mutableStateOf(Color(0xFF9B9D95))
	var isDarkMode by mutableStateOf<Boolean?>(null)
	var versionsManifest by mutableStateOf<VersionsManifest?>(null)
	var folders by mutableStateOf<List<Folder>>(emptyList())
	var currentFolder by mutableStateOf<Folder?>(null)
	var currentVersion by mutableStateOf<Version?>(null)

	val json = Json {
		ignoreUnknownKeys = true
	}
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
var versionsManifest
	get() = data.versionsManifest
	set(value) {
		data.versionsManifest = value
	}

context(data: Data)
var folders
	get() = data.folders
	set(value) {
		data.folders = value
	}

context(data: Data)
var currentFolder
	get() = data.currentFolder
	set(value) {
		data.currentFolder = value
	}

context(data: Data)
var currentVersion
	get() = data.currentVersion
	set(value) {
		data.currentVersion = value
	}

context(data: Data)
val json
	get() = data.json

context(data: Data)
val client
	get() = data.client