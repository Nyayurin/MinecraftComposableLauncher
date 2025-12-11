package cn.yurin.mcl.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import cn.yurin.mcl.network.VersionsManifest
import cn.yurin.mcl.ui.localization.Context
import cn.yurin.mcl.ui.localization.DownloadsPageDest
import cn.yurin.mcl.ui.localization.assetIndex
import cn.yurin.mcl.ui.localization.assets
import cn.yurin.mcl.ui.localization.client
import cn.yurin.mcl.ui.localization.current
import cn.yurin.mcl.ui.localization.dest
import cn.yurin.mcl.ui.localization.libraries
import cn.yurin.mcl.ui.localization.manifest
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
import kotlinx.coroutines.async
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
	val httpClient = HttpClient(CIO) {
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

context(_: Data, _: Context)
suspend fun downloadManifest(
	version: VersionsManifest.Version,
	onInitDownloadList: (String, Int) -> Unit,
	onDownloaded: (String) -> Unit,
	onDownloadError: (Throwable) -> Unit,
): VersionManifest? = dest(DownloadsPageDest.DownloadAlert) {
	runCatching {
		val file = File(currentFolder!!.path, "versions/${version.id}/${version.id}.json")
		if (file.exists()) {
			return json.decodeFromString(file.readText())
		}
		onInitDownloadList(manifest.current, 1)
		file.parentFile.mkdirs()
		val response = httpClient.get(version.url)
		if (response.status == HttpStatusCode.OK) {
			val manifestRaw = response.bodyAsText()
			file.writeText(manifestRaw)
			val versionManifest = json.decodeFromString<VersionManifest>(manifestRaw)
			onDownloaded(manifest.current)
			return versionManifest
		}
		onDownloadError(Exception("Failed to download manifest ${version.url}: ${response.status}"))
	}.onFailure { onDownloadError(it) }
	return null
}

context(_: Data, scope: CoroutineScope, _: Context)
suspend fun completeVersion(
	version: VersionsManifest.Version,
	manifest: VersionManifest,
	onInitDownloadList: (String, Int) -> Unit,
	onDownloaded: (String) -> Unit,
	onDownloadError: (Throwable) -> Unit,
) = dest(DownloadsPageDest.DownloadAlert) {
	val semaphore = Semaphore(Runtime.getRuntime().availableProcessors())
	val clientJob = scope.launch {
		runCatching {
			semaphore.withPermit {
				val file = File(currentFolder!!.path, "versions/${version.id}/${version.id}.jar")
				if (!file.exists()) {
					onInitDownloadList(client.current, 1)
					val response = httpClient.get(manifest.downloads.client.url)
					if (response.status != HttpStatusCode.OK) {
						onDownloadError(Exception("Failed to download jar ${manifest.downloads.client.url}: ${response.status}"))
						return@runCatching
					}
					file.writeBytes(response.bodyAsBytes())
					onDownloaded(client.current)
				}
			}
		}.onFailure { onDownloadError(it) }
	}

	val librariesJobs = manifest.libraries.filter { library ->
		val filter = when (library.rule?.os?.name) {
			null -> true
			in System.getProperty("os.name").lowercase() -> true
			else -> false
		}
		when (filter && library.downloads != null) {
			true -> !File(currentFolder!!.path, "libraries/${library.downloads.artifact.path}").exists()
			else -> false
		}
	}.also {
		if (it.isNotEmpty()) {
			onInitDownloadList(libraries.current, it.size)
		}
	}.map { library ->
		scope.launch {
			runCatching {
				semaphore.withPermit {
					val file = File(currentFolder!!.path, "libraries/${library.downloads!!.artifact.path}")
					file.parentFile.mkdirs()
					val response = httpClient.get(library.downloads.artifact.url)
					if (response.status != HttpStatusCode.OK) {
						onDownloadError(Exception("Failed to download library ${response.call.request.url}: ${response.status}"))
						return@launch
					}
					file.writeBytes(response.bodyAsBytes())
					onDownloaded(libraries.current)
				}
			}.onFailure { onDownloadError(it) }
		}
	}

	val assetIndex = scope.async {
		runCatching {
			semaphore.withPermit {
				val file = File(currentFolder!!.path, "assets/indexes/${manifest.assetIndex.id}.json")
				if (!file.exists()) {
					onInitDownloadList(assetIndex.current, 1)
					file.parentFile.mkdirs()
					val assetIndexRaw = httpClient.get(manifest.assetIndex.url).bodyAsText()
					file.writeText(assetIndexRaw)
					onDownloaded(assetIndex.current)
				}
				json.decodeFromString<AssetIndex>(file.readText())
			}
		}.onFailure { onDownloadError(it) }
	}.await().getOrNull()

	val assesJobs = assetIndex?.objects?.values?.filter { item ->
		val file = File(currentFolder!!.path, "assets/objects/${item.hash.substring(0, 2)}/${item.hash}")
		!file.exists()
	}?.also { items ->
		if (items.isNotEmpty()) {
			onInitDownloadList(assets.current, items.size)
		}
	}?.map { item ->
		scope.launch {
			runCatching {
				semaphore.withPermit {
					val file = File(currentFolder!!.path, "assets/objects/${item.hash.substring(0, 2)}/${item.hash}")
					file.parentFile.mkdirs()
					val response = httpClient.get("https://resources.download.minecraft.net/${item.hash.substring(0, 2)}/${item.hash}")
					if (response.status != HttpStatusCode.OK) {
						onDownloadError(Exception("Failed to download asset ${response.call.request.url}: ${response.status}"))
						return@launch
					}
					file.writeBytes(response.bodyAsBytes())
					onDownloaded(assets.current)
				}
			}.onFailure { onDownloadError(it) }
		}
	}

	clientJob.join()
	librariesJobs.joinAll()
	assesJobs?.joinAll()
}

context(_: Data)
suspend fun refreshVersionsManifest() {
	runCatching {
		val response = httpClient.get("https://piston-meta.mojang.com/mc/game/version_manifest.json")
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
val httpClient
	get() = data.httpClient

context(data: Data)
val scope
	get() = data.scope