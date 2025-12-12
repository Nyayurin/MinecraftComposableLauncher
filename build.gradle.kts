import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
	kotlin("multiplatform") version "2.2.21"
	kotlin("plugin.serialization") version "2.2.21"
	id("org.jetbrains.kotlin.plugin.compose") version "2.2.21"
	id("org.jetbrains.compose") version "1.10.0-rc02"
}

group = "cn.yurin"
version = "1.0-SNAPSHOT"

repositories {
	google()
	mavenCentral()
}

kotlin {
	jvmToolchain(21)

	jvm("composeJvm")

	sourceSets {
		fun NamedDomainObjectContainer<KotlinSourceSet>.getting(
			dependOn: KotlinSourceSet?,
			path: String,
			additionKotlin: List<String> = emptyList(),
			additionResources: List<String> = emptyList(),
		): NamedDomainObjectCollectionDelegateProvider<KotlinSourceSet> = getting {
			dependOn?.let { dependsOn(it) }
			configureSource(path, additionKotlin, additionResources)
		}

		fun NamedDomainObjectContainer<KotlinSourceSet>.creating(
			dependOn: KotlinSourceSet?,
			path: String,
			additionKotlin: List<String> = emptyList(),
			additionResources: List<String> = emptyList(),
		): NamedDomainObjectContainerCreatingDelegateProvider<KotlinSourceSet> = creating {
			dependOn?.let { dependsOn(it) }
			configureSource(path, additionKotlin, additionResources)
		}

		val commonMain by getting(
			dependOn = null,
			path = "source/main",
			additionKotlin = listOf("commonResClass", "commonMainResourceCollectors"),
		)
		val composeMain by creating(
			dependOn = commonMain,
			path = "source@compose/main",
			additionKotlin = listOf("composeMainResourceAccessors"),
		)
		val composeJvmMain by getting(
			dependOn = composeMain,
			path = "source@compose/main@jvm",
			additionKotlin = listOf("composeJvmMainResourceCollectors"),
			additionResources = listOf("composeJvmMain"),
		)
		val composeClrMain by creating(
			dependOn = composeMain,
			path = "source@compose/main@clr",
		)
		val avaloniaMain by creating(
			dependOn = commonMain,
			path = "source@avalonia/main",
		)
		val avaloniaMixMain by creating(
			dependOn = avaloniaMain,
			path = "source@avalonia/main@mix"
		)
		val avaloniaClrMain by creating(
			dependOn = avaloniaMain,
			path = "source@avalonia/main@clr",
		)

		commonMain.dependencies {
			implementation("org.jetbrains.compose.components:components-resources:1.10.0-rc02")
		}

		composeMain.dependencies {
			implementation("org.jetbrains.compose.runtime:runtime:1.10.0-rc02")
			implementation("org.jetbrains.compose.ui:ui:1.10.0-rc02")
			implementation("org.jetbrains.compose.foundation:foundation:1.10.0-rc02")
			implementation("org.jetbrains.compose.material3:material3:1.10.0-alpha05")

			implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
			implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
			implementation("com.materialkolor:material-kolor:4.0.0")
			implementation("com.github.skydoves:colorpicker-compose:1.1.2")
			implementation("io.ktor:ktor-client-core-jvm:3.3.3")
			implementation("io.ktor:ktor-client-cio-jvm:3.3.3")
			implementation("io.ktor:ktor-client-content-negotiation-jvm:3.3.3")
			implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.3.3")
			implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
			implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
			implementation("ch.qos.logback:logback-classic:1.5.21")
			implementation("io.github.vinceglb:filekit-dialogs-compose:0.12.0")
		}

		composeJvmMain.dependencies {
			implementation(compose.desktop.currentOs) {
				exclude("org.jetbrains.compose.material", "material")
			}
			implementation("net.java.dev.jna:jna:5.14.0")
			implementation("net.java.dev.jna:jna-platform:5.14.0")
		}

		all {
			languageSettings.enableLanguageFeature("ContextParameters")
		}
	}
}

compose.resources {
	customDirectory(
		sourceSetName = "composeMain",
		directoryProvider = provider { layout.projectDirectory.dir("source@compose/main/composeResources") }
	)
}

compose.desktop {
	application {
		mainClass = "cn.yurin.mcl.MainKt"

		nativeDistributions {
			val os = System.getProperty("os.name")
			when {
				os.contains("Windows") -> targetFormats(TargetFormat.Msi, TargetFormat.Exe, TargetFormat.AppImage)
				os.contains("Linux") -> targetFormats(TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.AppImage)
				os.contains("Mac OS") -> targetFormats(TargetFormat.Dmg, TargetFormat.Pkg)
				else -> error("Unsupported OS: $os")
			}
			packageName = "Minecraft Composable Launcher"
			packageVersion = "1.0.0"
			jvmArgs("-Dfile.encoding=UTF-8")

			linux {
				modules("jdk.security.auth")
			}
		}
	}
}

fun KotlinSourceSet.configureSource(
	path: String,
	additionKotlin: List<String>,
	additionResources: List<String>,
) {
	kotlin.setSrcDirs(
		listOf(
			"$path/kotlin",
			*additionKotlin.map {
				"build/generated/compose/resourceGenerator/kotlin/$it"
			}.toTypedArray(),
		)
	)
	resources.setSrcDirs(
		listOf(
			"$path/res",
			*additionResources.map {
				"build/generated/compose/resourceGenerator/assembledResources/$it"
			}.toTypedArray(),
		)
	)
}