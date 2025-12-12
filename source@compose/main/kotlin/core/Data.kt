package cn.yurin.mcl.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import cn.yurin.mcl.network.VersionsManifest
import cn.yurin.mcl.ui.localization.*
import cn.yurin.mcl.ui.localization.destination.DownloadsDest
import cn.yurin.mcl.ui.localization.destination.assetIndex
import cn.yurin.mcl.ui.localization.destination.assets
import cn.yurin.mcl.ui.localization.destination.client
import cn.yurin.mcl.ui.localization.destination.libraries
import cn.yurin.mcl.ui.localization.destination.manifest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
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
	var accounts by mutableStateOf<List<Account>>(emptyList())
	var currentAccount: Account? by mutableStateOf(null)

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

context(data: Data, _: Context)
suspend fun login(
	onLoginRequest: (String, String) -> Unit,
): Account? {
	runCatching {
		// 获取设备码并申请用户授权
		val response = getDeviceCode()
		onLoginRequest(response.userCode, response.verificationUri)
		var status: AuthorizationStatus.Success? = null
		// 每 5 秒查询授权状态, 直到用户同意或拒绝授权
		while (true) {
			delay(response.interval * 1000L)
			when (val tempStatus = checkAuthorizationStatus(response.deviceCode)) {
				is AuthorizationStatus.Success -> {
					status = tempStatus
					break
				}

				is AuthorizationStatus.Failure -> if (tempStatus.error !in listOf("authorization_pending", "slow_down")) break
			}
		}
		// XboxLive 登录
		val xblResponse = xblAuthorization(status!!.accessToken)
		// XSTS 登录
		val xstsResponse = xstsAuthorization(xblResponse.token)
		// Minecraft 登录
		val minecraftResponse = getMinecraftAccessToken(xstsResponse.token, xstsResponse.displayClaims.xui.first().uhs)
		// 查询用户是否购买 Minecraft
		if (checkHasMinecraft(minecraftResponse.accessToken).items.isNotEmpty()) {
			// 获取用户 Minecraft 档案信息
			val profile = getMinecraftProfile(minecraftResponse.accessToken)
			// 现在你终于可以完成一次 Minecraft 正版登录了
			return Account.Online(
				name = profile.name,
				token = minecraftResponse.accessToken,
				uuid = profile.id,
				accessToken = status.accessToken,
				refreshToken = status.refreshToken,
			)
		}
	}
	return null
}

context(data: Data, _: Context)
suspend fun downloadManifest(
	version: VersionsManifest.Version,
	onInitDownloadList: (String, Int) -> Unit,
	onDownloaded: (String) -> Unit,
	onDownloadError: (Throwable) -> Unit,
): VersionManifest? = dest(DownloadsDest.DownloadDialog) {
	runCatching {
		val file = File(data.currentFolder!!.path, "versions/${version.id}/${version.id}.json")
		if (file.exists()) {
			return data.json.decodeFromString(file.readText())
		}
		onInitDownloadList(manifest.current, 1)
		file.parentFile.mkdirs()
		val response = data.client.get(version.url)
		if (response.status == HttpStatusCode.OK) {
			val manifestRaw = response.bodyAsText()
			file.writeText(manifestRaw)
			val versionManifest = data.json.decodeFromString<VersionManifest>(manifestRaw)
			onDownloaded(manifest.current)
			return versionManifest
		}
		onDownloadError(Exception("Failed to download manifest ${version.url}: ${response.status}"))
	}.onFailure { onDownloadError(it) }
	return null
}

context(data: Data, scope: CoroutineScope, _: Context)
suspend fun completeVersion(
	version: VersionsManifest.Version,
	manifest: VersionManifest,
	onInitDownloadList: (String, Int) -> Unit,
	onDownloaded: (String) -> Unit,
	onDownloadError: (Throwable) -> Unit,
) = dest(DownloadsDest.DownloadDialog) {
	val semaphore = Semaphore(Runtime.getRuntime().availableProcessors())
	val clientJob = scope.launch {
		runCatching {
			semaphore.withPermit {
				val file = File(data.currentFolder!!.path, "versions/${version.id}/${version.id}.jar")
				if (!file.exists()) {
					onInitDownloadList(client.current, 1)
					val response = data.client.get(manifest.downloads.client.url)
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
			true -> !File(data.currentFolder!!.path, "libraries/${library.downloads.artifact.path}").exists()
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
					val file = File(data.currentFolder!!.path, "libraries/${library.downloads!!.artifact.path}")
					file.parentFile.mkdirs()
					val response = data.client.get(library.downloads.artifact.url)
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
				val file = File(data.currentFolder!!.path, "assets/indexes/${manifest.assetIndex.id}.json")
				if (!file.exists()) {
					onInitDownloadList(assetIndex.current, 1)
					file.parentFile.mkdirs()
					val assetIndexRaw = data.client.get(manifest.assetIndex.url).bodyAsText()
					file.writeText(assetIndexRaw)
					onDownloaded(assetIndex.current)
				}
				data.json.decodeFromString<AssetIndex>(file.readText())
			}
		}.onFailure { onDownloadError(it) }
	}.await().getOrNull()

	val assesJobs = assetIndex?.objects?.values?.filter { item ->
		val file = File(data.currentFolder!!.path, "assets/objects/${item.hash.substring(0, 2)}/${item.hash}")
		!file.exists()
	}?.also { items ->
		if (items.isNotEmpty()) {
			onInitDownloadList(assets.current, items.size)
		}
	}?.map { item ->
		scope.launch {
			runCatching {
				semaphore.withPermit {
					val file = File(data.currentFolder!!.path, "assets/objects/${item.hash.substring(0, 2)}/${item.hash}")
					file.parentFile.mkdirs()
					val response = data.client.get("https://resources.download.minecraft.net/${item.hash.substring(0, 2)}/${item.hash}")
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

context(data: Data)
suspend fun refreshVersionsManifest() {
	runCatching {
		val response = data.client.get("https://piston-meta.mojang.com/mc/game/version_manifest.json")
		if (response.status == HttpStatusCode.OK) {
			data.versionsManifest = response.body<VersionsManifest>()
		} else {
			println("Failed to get version manifest: ${response.status}")
		}
	}.onFailure {
		println("Failed to get version manifest: ${it.message}")
	}
}

context(data: Data)
fun refreshFolders() {
	data.folders = data.folders.map { it.refresh() }
	data.currentFolder = data.currentFolder?.refresh()
}

context(data: Data)
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
				manifest = data.json.decodeFromString(File(version, "${version.name}.json").readText())
			)
		}.sortedByDescending {
			it.manifest.releaseTime
		}
	)

	is GameFolder.MCL -> TODO()
}