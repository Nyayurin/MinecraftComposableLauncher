package cn.yurin.minecraft_composable_launcher.core

fun buildGameProcess(
	java: String,
	launcherBrand: String?,
	launcherVersion: String?,
	classpath: List<String>,
	minecraftJar: String,
	mainClass: String,
	gameDir: String,
	assetDir: String,
	assetIndex: String,
	uuid: String,
	accessToken: String,
	version: String,
): ProcessBuilder {
	return ProcessBuilder(
		java,
		"-Dminecraft.launcher.brand=$launcherBrand",
		"-Dminecraft.launcher.version=$launcherVersion",
		"-cp",
		listOf(*classpath.toTypedArray(), minecraftJar).joinToString(";"),
		mainClass,
		"--gameDir",
		gameDir,
		"--assetsDir",
		assetDir,
		"--assetIndex",
		assetIndex,
		"--uuid",
		uuid,
		"--accessToken",
		accessToken,
		"--version",
		version,
	)
}