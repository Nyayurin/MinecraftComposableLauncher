import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	kotlin("multiplatform") version "2.2.21"
	kotlin("plugin.serialization") version "2.2.21"
	id("org.jetbrains.kotlin.plugin.compose") version "2.2.21"
	id("org.jetbrains.compose") version "1.9.3"
}

group = "cn.yurin"
version = "1.0-SNAPSHOT"

repositories {
	google()
	mavenCentral()
}

kotlin {
	jvmToolchain(21)

	jvm("composeJvm") {
	}

	sourceSets {
		commonMain {
			kotlin.setSrcDirs(
				listOf(
					"src/main/kotlin",
					"build/generated/compose/resourceGenerator/kotlin/commonResClass",
					"build/generated/compose/resourceGenerator/kotlin/commonMainResourceCollectors"
				)
			)
			resources.setSrcDirs(listOf("src/main/resources"))
			dependencies {
				implementation(compose.components.resources)
			}
		}

		val composeMain by creating {
			dependsOn(commonMain.get())
			kotlin.setSrcDirs(
				listOf(
					"src/main@compose/kotlin",
					"build/generated/compose/resourceGenerator/kotlin/composeMainResourceAccessors",
				)
			)
			resources.setSrcDirs(
				listOf(
					"src/main@compose/resources",
					"build/generated/compose/resourceGenerator/assembledResources/composeMain",
				)
			)
			dependencies {
				implementation(compose.runtime)
				implementation(compose.ui)
				implementation(compose.foundation)
				implementation(compose.material3)
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
			}
		}

		val composeJvmMain by getting {
			dependsOn(composeMain)
			kotlin.setSrcDirs(
				listOf(
					"src/main@composeJvm/kotlin",
					"build/generated/compose/resourceGenerator/kotlin/composeJvmMainResourceCollectors",
				)
			)
			resources.setSrcDirs(
				listOf(
					"src/main@composeJvm/resources",
					"build/generated/compose/resourceGenerator/assembledResources/composeJvmMain",
				)
			)
			dependencies {
				implementation(compose.desktop.currentOs)
			}
		}

		val composeClrMain by creating {
			dependsOn(composeMain)
			kotlin.setSrcDirs(listOf("src/main@composeClr/kotlin"))
			resources.setSrcDirs(listOf("src/main@composeClr/resources"))
			dependencies {

			}
		}

		val avaloniaMain by creating {
			dependsOn(commonMain.get())
			kotlin.setSrcDirs(listOf("src/main@avalonia/kotlin"))
			resources.setSrcDirs(listOf("src/main@avalonia/resources"))
			dependencies {

			}
		}

		val avaloniaClrMain by creating {
			dependsOn(avaloniaMain)
			kotlin.setSrcDirs(listOf("src/main@avaloniaClr/kotlin"))
			resources.setSrcDirs(listOf("src/main@avaloniaClr/resources"))
			dependencies {

			}
		}

		all {
			languageSettings.enableLanguageFeature("ContextParameters")
		}
	}
}

compose.resources {
	customDirectory(
		sourceSetName = "composeMain",
		directoryProvider = provider { layout.projectDirectory.dir("src/main@compose/composeResources") }
	)
	customDirectory(
		sourceSetName = "composeJvmMain",
		directoryProvider = provider { layout.projectDirectory.dir("src/main@composeJvm/composeResources") }
	)
	customDirectory(
		sourceSetName = "composeClrMain",
		directoryProvider = provider { layout.projectDirectory.dir("src/main@composeClr/composeResources") }
	)
}

compose.desktop {
	application {
		mainClass = "cn.yurin.minecraft_composable_launcher.MainKt"

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