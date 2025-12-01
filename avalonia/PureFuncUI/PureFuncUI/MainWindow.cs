using Avalonia.Controls;
using Avalonia.Media;
using Avalonia.Platform;

namespace Yurin.MCL;

public class MainWindow : Window {
	public MainWindow() {
		Width = 1000;
		Height = 600;
		Title = "Minecraft Avalonia Launcher";
		Content = new CounterComponent();
		
		ExtendClientAreaToDecorationsHint = true;
		ExtendClientAreaTitleBarHeightHint = -1;
		SystemDecorations = SystemDecorations.None;
		TransparencyLevelHint = [WindowTransparencyLevel.Transparent];
		Background = Brushes.Transparent;
	}
}