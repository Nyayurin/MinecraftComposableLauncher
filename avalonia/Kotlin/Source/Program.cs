using Avalonia;
using Avalonia.Markup.Declarative;
using ReactiveUI.Avalonia;

namespace Yurin.MAL;

class Program {
	[STAThread]
	public static void Main(string[] args) =>
		buildAvaloniaApp()
			.StartWithClassicDesktopLifetime(args);

	private static AppBuilder buildAvaloniaApp()
		=> AppBuilder.Configure<App>()
			.UsePlatformDetect()
			.UseRiderHotReload()
			.UseReactiveUI();
}