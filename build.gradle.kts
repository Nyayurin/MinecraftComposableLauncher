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

	jvm {
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
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
			}
		}

		jvmMain {
			kotlin.setSrcDirs(
				listOf(
					"src/main@jvm/kotlin",
					"build/generated/compose/resourceGenerator/kotlin/jvmMainResourceAccessors",
					"build/generated/compose/resourceGenerator/kotlin/jvmMainResourceCollectors"
				)
			)
			resources.setSrcDirs(
				listOf(
					"src/main@jvm/resources",
					"build/generated/compose/resourceGenerator/assembledResources/jvmMain"
				)
			)
			dependencies {
				implementation(compose.runtime)
				implementation(compose.ui)
				implementation(compose.foundation)
				implementation(compose.material3)
				implementation(compose.desktop.currentOs)
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
				implementation("com.materialkolor:material-kolor:4.0.0")
				implementation("com.github.skydoves:colorpicker-compose:1.1.2")
				implementation("io.ktor:ktor-client-core-jvm:3.3.3")
				implementation("io.ktor:ktor-client-cio-jvm:3.3.3")
				implementation("io.ktor:ktor-client-content-negotiation-jvm:3.3.3")
				implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.3.3")
				implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
				implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
			}
		}

		val clrMain by creating {
			dependsOn(commonMain.get())
			kotlin.setSrcDirs(listOf("src/main@clr/kotlin"))
			resources.setSrcDirs(listOf("src/main@clr/resources"))
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
		sourceSetName = "jvmMain",
		directoryProvider = provider { layout.projectDirectory.dir("src/main@jvm/composeResources") }
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
			packageVersion = "0.0.0"
			jvmArgs("-Dfile.encoding=UTF-8")

			linux {
				modules("jdk.security.auth")
			}
		}
	}
}