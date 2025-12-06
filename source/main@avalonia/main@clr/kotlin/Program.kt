package cn.yurin.minecraft_avalonia_launcher

import Avalonia.AppBuilder

fun main(args: Array) = buildAvaloniaApp()

private fun buildAvaloniaApp() = AppBuilder.Configure<App>()