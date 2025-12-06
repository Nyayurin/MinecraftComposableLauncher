import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

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

	jvm("composeJvm")

	sourceSets {
		val dependencies = mapOf<String, KotlinDependencyHandler.() -> Unit>(
			"commonMain" to {
				implementation(compose.components.resources)
			},
			"composeMain" to {
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
			},
			"composeJvmMain" to {
				implementation(compose.desktop.currentOs)
			},
		)

		fun NamedDomainObjectContainer<KotlinSourceSet>.getting(
			dependOn: KotlinSourceSet?,
			name: String?,
			additionKotlin: List<String> = emptyList(),
			additionResources: List<String> = emptyList(),
			dependenciesName: String,
		): NamedDomainObjectCollectionDelegateProvider<KotlinSourceSet> = getting {
			dependOn?.let { dependsOn(it) }
			configureSource(name, additionKotlin, additionResources)
			dependencies(dependencies[dependenciesName] ?: {})
		}

		fun NamedDomainObjectContainer<KotlinSourceSet>.creating(
			dependOn: KotlinSourceSet?,
			name: String?,
			additionKotlin: List<String> = emptyList(),
			additionResources: List<String> = emptyList(),
			dependenciesName: String,
		): NamedDomainObjectContainerCreatingDelegateProvider<KotlinSourceSet> = creating {
			dependOn?.let { dependsOn(it) }
			configureSource(name, additionKotlin, additionResources)
			dependencies(dependencies[dependenciesName] ?: {})
		}

		val commonMain by getting(
			dependOn = null,
			name = "null",
			additionKotlin = listOf("commonResClass", "commonMainResourceCollectors"),
			dependenciesName = "commonMain",
		)
		val composeMain by creating(
			dependOn = commonMain,
			name = "compose",
			additionKotlin = listOf("composeMainResourceAccessors"),
			dependenciesName = "composeMain",
		)
		val composeJvmMain by getting(
			dependOn = composeMain,
			name = "composeJvm",
			additionKotlin = listOf("composeJvmMainResourceCollectors"),
			additionResources = listOf("composeJvmMain"),
			dependenciesName = "composeJvmMain",
		)
		val composeClrMain by creating(
			dependOn = composeMain,
			name = "composeClr",
			dependenciesName = "composeClrMain",
		)
		val avaloniaMain by creating(
			dependOn = commonMain,
			name = "avalonia",
			dependenciesName = "avaloniaMain",
		)
		val avaloniaClrMain by creating(
			dependOn = avaloniaMain,
			name = "avaloniaClr",
			dependenciesName = "avaloniaClrMain",
		)

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

fun KotlinSourceSet.configureSource(
	name: String?,
	additionKotlin: List<String>,
	additionResources: List<String>,
) {
	kotlin.setSrcDirs(
		listOf(
			"src/main${name?.let { "@$it" } ?: ""}/kotlin",
			*additionKotlin.map {
				"build/generated/compose/resourceGenerator/kotlin/$it"
			}.toTypedArray(),
		)
	)
	resources.setSrcDirs(
		listOf(
			"src/main${name?.let { "@$it" } ?: ""}/resources",
			*additionResources.map {
				"build/generated/compose/resourceGenerator/assembledResources/$it"
			}.toTypedArray(),
		)
	)
}