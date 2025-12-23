using Avalonia;
using Avalonia.Controls.ApplicationLifetimes;
using Avalonia.Markup.Xaml;
using yurin.mal;

namespace Yurin.MAL;

public class App : Application
{
	public override void Initialize()
	{
		AvaloniaXamlLoader.Load(this);
	}

	public override void OnFrameworkInitializationCompleted()
	{
		if (ApplicationLifetime is IClassicDesktopStyleApplicationLifetime desktop)
		{
			desktop.MainWindow = new MainWindow();
		}

		base.OnFrameworkInitializationCompleted();
	}
}