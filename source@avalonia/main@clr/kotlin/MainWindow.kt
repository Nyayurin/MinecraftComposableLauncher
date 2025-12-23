package yurin.mal

import Avalonia.Controls.Window

class MainWindow : Window {
	constructor() : super() {
		Width = 1000
		Height = 600
		Title = "Minecraft Avalonia Launcher"
		Content = MALComponent()
	}
}