package cn.yurin.mcl.ui.neo.page.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import cn.yurin.mcl.core.Account
import cn.yurin.mcl.core.Data
import cn.yurin.mcl.core.buildGameProcess
import cn.yurin.mcl.ui.localization.Context
import cn.yurin.mcl.ui.localization.current
import cn.yurin.mcl.ui.localization.dest
import cn.yurin.mcl.ui.localization.destination.*
import com.github.panpf.sketch.PainterState
import com.github.panpf.sketch.SubcomposeAsyncImage
import dev.chrisbanes.haze.hazeEffect
import io.github.iamcalledrob.smoothRoundedCornerShape.SmoothRoundedCornerShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.File

@Composable
context(_: Context, data: Data)
fun Launch(
	onChangeToGamesPage: () -> Unit,
	onChangeToAccountsPage: () -> Unit,
) = dest(LaunchDest) {
	Column(
		modifier = Modifier.fillMaxSize(),
	) {
		Spacer(
			modifier = Modifier.weight(1F),
		)
		Row(
			horizontalArrangement = Arrangement.aligned(
				BiasAlignment.Horizontal(
					animateFloatAsState(
						when (data.windowSize.widthSizeClass) {
							WindowWidthSizeClass.Compact -> 0F
							else -> 1F
						}
					).value
				),
			),
			modifier = Modifier.fillMaxWidth(),
		) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				modifier = Modifier
					.clip(SmoothRoundedCornerShape(radius = 16.dp))
					.hazeEffect(data.hazeState, data.hazeStyle)
					.padding(20.dp),
			) {
				Row(
//					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(12.dp),
					modifier = Modifier
						.clip(SmoothRoundedCornerShape(radius = 12.dp))
						.clickable(onClick = onChangeToGamesPage)
						.padding(8.dp),
				) {
					data.currentAccount?.let { account ->
						when (account) {
							is Account.Online -> ShadowedFace(account.uuid)
							else -> null
						}
					} ?: run {
						ShadowedFace(
							subject = "X-Steve",
						)
					}
					Column(
						horizontalAlignment = Alignment.Start,
						verticalArrangement = Arrangement.spacedBy(8.dp),
					) {
						Text(
							text = data.currentAccount?.name ?: loginAccount.current,
							color = MaterialTheme.colorScheme.onSurface,
							style = MaterialTheme.typography.headlineSmall,
						)
						AnimatedVisibility(data.currentAccount != null) {
							Text(
								text = data.currentAccount?.let { account ->
									when (account) {
										is Account.Online -> onlineAccount.current
										is Account.Offline -> offlineAccount.current
									}
								} ?: "",
								color = MaterialTheme.colorScheme.onSurfaceVariant,
								fontSize = 18.sp,
								style = MaterialTheme.typography.titleMedium,
							)
						}
					}
				}
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(8.dp),
					modifier = Modifier
						.clip(SmoothRoundedCornerShape(radius = 12.dp))
						.background(MaterialTheme.colorScheme.primary)
						.clickable {
							val osName = System.getProperty("os.name").lowercase()
							val classpathSeparator = when {
								"win" in osName -> ";"
								else -> ":"
							}
							when {
								data.currentVersion == null -> onChangeToGamesPage()
								data.currentAccount == null -> onChangeToAccountsPage()
								else -> data.scope.launch(Dispatchers.IO) {
									val currentFolder = data.currentFolder!!
									val currentVersion = data.currentVersion!!
									val currentAccount = data.currentAccount!!
									val libraries = listOf(
										*currentVersion.manifest.libraries.filter { library ->
											when (library.rule?.os?.name) {
												null -> true
												in osName -> true
												else -> false
											}
										}.map { library ->
											library.downloads?.artifact?.path?.let { path ->
												File("${currentFolder.path}/libraries", path).absolutePath
											} ?: run {
												val (groupId, artifactId, version) = library.name.split(":")
												val groups = groupId.split(".")
												File("${currentFolder.path}/libraries", "${groups.joinToString("/")}/$artifactId/$version/$artifactId-$version.jar").absolutePath
											}
										}.toTypedArray(),
										File(currentVersion.path, "${currentVersion.name}.jar").absolutePath
									)
									val process = buildGameProcess(
										java = "java",
										mainClass = currentVersion.manifest.mainClass,
										arguments = currentVersion.manifest.arguments,
										variables = mapOf(
											"auth_player_name" to currentAccount.name,
											"version_name" to currentVersion.manifest.id,
											"game_directory" to currentFolder.path,
											"assets_root" to "${currentFolder.path}/assets",
											"assets_index_name" to currentVersion.manifest.assetIndex.id,
											"auth_uuid" to currentAccount.uuid,
											"auth_access_token" to currentAccount.token,
											"user_type" to "msa",
											"version_type" to "MCL 1.0.0",
											"natives_directory" to File(currentVersion.path, "natives").absolutePath,
											"launcher_name" to "Minecraft Composable Launcher",
											"launcher_version" to "1.0.0",
											"classpath" to listOf(*libraries.toTypedArray(), File(currentVersion.path, "${currentVersion.name}.jar").absolutePath).joinToString(classpathSeparator),
											"classpath_separator" to classpathSeparator,
											"library_directory" to File(currentFolder.path, "libraries").absolutePath,
										),
										features = mapOf(),
									).start()
									val error = async(Dispatchers.IO) {
										while (process.isAlive) {
											process.errorReader().readLine()?.let(System.err::println)
										}
									}
									val input = async(Dispatchers.IO) {
										while (process.isAlive) {
											process.inputReader().readLine()?.let(::println)
										}
									}
									awaitAll(input, error)
									println("Process done")
								}
							}
						}
						.padding(
							horizontal = 16.dp,
							vertical = 8.dp,
						),
				) {
					Text(
						text = launch.current,
						color = MaterialTheme.colorScheme.onPrimary,
						overflow = TextOverflow.Ellipsis,
						maxLines = 1,
						style = MaterialTheme.typography.headlineSmall,
					)
					Text(
						text = data.currentVersion?.manifest?.id ?: selectVersion.current,
						color = MaterialTheme.colorScheme.outlineVariant,
						fontSize = 18.sp,
						overflow = TextOverflow.Ellipsis,
						maxLines = 1,
						style = MaterialTheme.typography.titleMedium,
					)
				}
			}
		}
	}
}

@Composable
fun ShadowedFace(subject: String) {
	SubcomposeAsyncImage(
		uri = "https://vzge.me/face/512/$subject",
		contentDescription = null,
	) {
		val state = painter.state
		val painterState = state.painterState

		if (painterState is PainterState.Success) {
			val painter = painterState.painter
			val shape = AlphaImageShape(
				bitmap = painterToImageBitmap(
					painter = painterState.painter,
					size = IntSize(64, 64)
				)
			)

			Image(
				painter = painter,
				contentDescription = null,
				modifier = Modifier
					.size(64.dp)
					.dropShadow(
						shape = shape,
						shadow = Shadow(
							radius = 6.dp,
						),
					),
			)
		}
	}
}

@Composable
fun painterToImageBitmap(
	painter: Painter,
	size: IntSize,
): ImageBitmap {
	val density = LocalDensity.current

	return remember(painter, size) {
		val imageBitmap = ImageBitmap(size.width, size.height)
		val canvas = Canvas(imageBitmap)

		val drawScope = CanvasDrawScope()
		drawScope.draw(
			density = density,
			layoutDirection = LayoutDirection.Ltr,
			canvas = canvas,
			size = Size(size.width.toFloat(), size.height.toFloat())
		) {
			with(painter) {
				draw(
					size = Size(size.width.toFloat(), size.height.toFloat())
				)
			}
		}
		imageBitmap
	}
}

fun alphaBitmapToPath(
	bitmap: ImageBitmap,
	alphaThreshold: Int = 10,
): Path {
	val path = Path()
	val width = bitmap.width
	val height = bitmap.height

	val pixelMap = bitmap.toPixelMap()

	for (y in 0 until height) {
		var startX = -1

		for (x in 0 until width) {
			val alpha = pixelMap[x, y].alpha * 255

			if (alpha > alphaThreshold) {
				if (startX == -1) startX = x
			} else {
				if (startX != -1) {
					path.addRect(
						Rect(
							startX.toFloat(),
							y.toFloat(),
							x.toFloat(),
							(y + 1).toFloat()
						)
					)
					startX = -1
				}
			}
		}

		if (startX != -1) {
			path.addRect(
				Rect(
					startX.toFloat(),
					y.toFloat(),
					width.toFloat(),
					(y + 1).toFloat()
				)
			)
		}
	}
	return path
}

class AlphaImageShape(
	private val bitmap: ImageBitmap,
	alphaThreshold: Int = 10,
) : Shape {

	private val originalPath = alphaBitmapToPath(bitmap, alphaThreshold)

	override fun createOutline(
		size: Size,
		layoutDirection: LayoutDirection,
		density: Density,
	): Outline {
		val scaleX = size.width / bitmap.width
		val scaleY = size.height / bitmap.height

		val scaledPath = Path().apply {
			addPath(originalPath)
			transform(
				Matrix().apply {
					scale(scaleX, scaleY)
				}
			)
		}

		return Outline.Generic(scaledPath)
	}
}