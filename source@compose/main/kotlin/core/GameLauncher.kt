package cn.yurin.mcl.core

fun buildGameProcess(
	java: String,
	mainClass: String,
	arguments: VersionManifest.Arguments,
	variables: Map<String, String>,
	features: Map<String, Boolean>,
): ProcessBuilder {
	return ProcessBuilder(
		java,
		*buildList {
			fun process(rule: Either<String, VersionManifest.Arguments.Argument>) {
				when (rule) {
					is Either.Left -> add(substitute(rule.value, variables))
					is Either.Right -> if (checkRules(rule.value.rules, features)) {
						when (val value = rule.value.value) {
							is Either.Left -> add(substitute(value.value, variables))
							is Either.Right -> value.value.forEach {
								add(substitute(it, variables))
							}
						}
					}
				}
			}
			arguments.jvm.forEach(::process)
			add(mainClass)
			arguments.game.forEach(::process)
		}.toTypedArray(),
	)
}

private fun checkRules(rules: List<VersionManifest.Rule>, features: Map<String, Boolean>): Boolean {
	if (rules.isEmpty()) return true

	val osName = System.getProperty("os.name").lowercase()
	val osArch = System.getProperty("os.arch").lowercase()
	val match = rules.map { rule ->
		when {
			rule.os?.name != null && rule.os.name !in osName -> false
			rule.os?.arch != null && rule.os.arch != osArch -> false
			rule.features == null -> true
			else -> {
				val featuresMatch = rule.features.map { (feature, expect) ->
					(features[feature] ?: false) == expect
				}
				featuresMatch.all { it }
			}
		} && rule.action == "allow"
	}.all { it }
	return match
}

private fun substitute(input: String, variables: Map<String, String>): String {
	var output = input
	variables.keys.forEach {
		if (input.contains($$"${$$it}")) {
			output = output.replace($$"${$$it}", variables[it]!!)
		}
	}
	return output
}