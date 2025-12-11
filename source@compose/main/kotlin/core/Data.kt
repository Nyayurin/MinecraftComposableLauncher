package cn.yurin.mcl.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import cn.yurin.mcl.network.VersionsManifest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.json.Json
import java.io.File

class Data {
	var seedColor by mutableStateOf(Color(0xFF9B9D95))
	var isDarkMode by mutableStateOf<Boolean?>(null)
	var versionsManifest by mutableStateOf<VersionsManifest?>(null)
	var folders by mutableStateOf<List<GameFolder>>(emptyList())
	var currentFolder by mutableStateOf<GameFolder?>(null)
	var currentVersion by mutableStateOf<Version?>(null)

	val json = Json {
		ignoreUnknownKeys = true
	}
	val client = HttpClient(CIO) {
		install(ContentNegotiation) {
			json()
		}
		install(HttpRequestRetry) {
			retryOnException(3, true)
		}
		install(HttpTimeout) {
			socketTimeoutMillis = 1000
		}
	}
	val scope = CoroutineScope(Dispatchers.IO)
}

context(_: Data)
suspend fun downloadManifest(
	version: VersionsManifest.Version,
	onInitDownloadList: (String, Int) -> Unit,
	onDownloaded: (String) -> Unit,
	onDownloadError: (Throwable) -> Unit,
): Result<VersionManifest> {
	val file = File(currentFolder!!.path, "versions/${version.id}/${version.id}.json")
	if (file.exists()) {
		return Result.success(json.decodeFromString(file.readText()))
	}
	onInitDownloadList("manifest", 1)
	file.parentFile.mkdirs()
	val response = client.get(version.url)
	if (response.status == HttpStatusCode.OK) {
		val manifestRaw = response.bodyAsText()
		file.writeText(manifestRaw)
		val manifest = json.decodeFromString<VersionManifest>(manifestRaw)
		onDownloaded("manifest")
		return Result.success(manifest)
	}
	onDownloadError(Exception("Failed to download manifest ${version.url}: ${response.status}"))
	return Result.failure(Exception("Failed to download manifest ${version.url}: ${response.status}"))
}

context(_: Data, scope: CoroutineScope)
suspend fun completeVersion(
	version: VersionsManifest.Version,
	manifest: VersionManifest,
	onInitDownloadList: (Map<String, Int>) -> Unit,
	onDownloaded: (String) -> Unit,
	onDownloadError: (Throwable) -> Unit,
) {
	val semaphore = Semaphore(64)
	val hasClientFile: Boolean
	val libraries = mutableListOf<VersionManifest.Library>()
	val assetIndex: AssetIndex
	val assets = mutableListOf<AssetIndex.Item>()
	File(currentFolder!!.path, "versions/${version.id}/${version.id}.jar").let { file ->
		hasClientFile = file.exists()
	}
	manifest.libraries.forEach { library ->
		val filter = when (library.rule?.os?.name) {
			null -> true
			in System.getProperty("os.name").lowercase() -> true
			else -> false
		}
		if (filter && library.downloads != null) {
			val file = File(currentFolder!!.path, "libraries/${library.downloads.artifact.path}")
			if (!file.exists()) libraries += library
		}
	}
	File(currentFolder!!.path, "assets/indexes/${manifest.assetIndex.id}.json").let { file ->
		if (!file.exists()) {
			file.parentFile.mkdirs()
			val assetIndexRaw = client.get(manifest.assetIndex.url).bodyAsText()
			file.writeText(assetIndexRaw)
		}
		assetIndex = json.decodeFromString(file.readText())
	}
	assetIndex.objects.values.forEach { item ->
		val file = File(currentFolder!!.path, "assets/objects/${item.hash.substring(0, 2)}/${item.hash}")
		if (!file.exists()) assets += item
	}
	onInitDownloadList(
		buildMap {
			if (!hasClientFile) {
				put("client", 1)
			}
			if (libraries.isNotEmpty()) {
				put("libraries", libraries.size)
			}
			if (assets.isNotEmpty()) {
				put("assets", assets.size)
			}
		}
	)
	scope.launch {
		runCatching {
			semaphore.withPermit {
				if (!hasClientFile) {
					val file = File(currentFolder!!.path, "versions/${version.id}/${version.id}.jar")
					val response = client.get(manifest.downloads.client.url)
					if (response.status != HttpStatusCode.OK) {
						onDownloadError(Exception("Failed to download jar ${manifest.downloads.client.url}: ${response.status}"))
						return@runCatching
					}
					file.writeBytes(client.get(manifest.downloads.client.url).bodyAsBytes())
					onDownloaded("client")
				}
			}
		}.onFailure { onDownloadError(it) }
	}

	scope.launch {
		libraries.map { library ->
			scope.launch {
				runCatching {
					semaphore.withPermit {
						val file = File(currentFolder!!.path, "libraries/${library.downloads!!.artifact.path}")
						file.parentFile.mkdirs()
						val response = client.get(library.downloads.artifact.url)
						if (response.status != HttpStatusCode.OK) {
							onDownloadError(Exception("Failed to download library ${response.call.request.url}: ${response.status}"))
							return@launch
						}
						file.writeBytes(response.bodyAsBytes())
						onDownloaded("libraries")
					}
				}.onFailure { onDownloadError(it) }
			}
		}.joinAll()
	}

	scope.launch {
		assets.map { item ->
			scope.launch {
				runCatching {
					semaphore.withPermit {
						val file =
							File(currentFolder!!.path, "assets/objects/${item.hash.substring(0, 2)}/${item.hash}")
						file.parentFile.mkdirs()
						val response =
							client.get(
								"https://resources.download.minecraft.net/${
									item.hash.substring(0, 2)
								}/${item.hash}"
							)
						if (response.status != HttpStatusCode.OK) {
							onDownloadError(Exception("Failed to download asset ${response.call.request.url}: ${response.status}"))
							return@launch
						}
						file.writeBytes(response.bodyAsBytes())
						onDownloaded("assets")
					}
				}.onFailure { onDownloadError(it) }
			}
		}.joinAll()
	}
}

context(_: Data)
suspend fun refreshVersionsManifest() {
	runCatching {
		val response = client.get("https://piston-meta.mojang.com/mc/game/version_manifest.json")
		if (response.status == HttpStatusCode.OK) {
			versionsManifest = response.body<VersionsManifest>()
		} else {
			println("Failed to get version manifest: ${response.status}")
		}
	}.onFailure {
		println("Failed to get version manifest: ${it.message}")
	}
}

context(_: Data)
fun refreshFolders() {
	folders = folders.map { it.refresh() }
	currentFolder = currentFolder?.refresh()
}

context(_: Data)
private fun GameFolder.refresh() = when (this) {
	is GameFolder.DotMinecraft -> copy(
		versions = (File(path, "versions").listFiles() ?: emptyArray()).filter { file ->
			file.isDirectory
		}.filter { version ->
			version.listFiles { file ->
				file.isFile && file.name == "${version.name}.json"
			}.isNotEmpty()
		}.map { version ->
			Version(
				name = version.name,
				path = version.absolutePath,
				manifest = json.decodeFromString(File(version, "${version.name}.json").readText())
			)
		}.sortedByDescending {
			it.manifest.releaseTime
		}
	)

	is GameFolder.MCL -> TODO()
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

context(data: Data)
val scope
	get() = data.scope