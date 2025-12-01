using Avalonia.Controls;
using Avalonia.Layout;
using Avalonia.Markup.Declarative;
using Material.Styles.Controls;
using ReactiveUI;

namespace Yurin.MCL;

public class CounterViewModel : ReactiveObject {
	private int backingCount;

	public int count {
		get => backingCount;
		set => this.RaiseAndSetIfChanged(ref backingCount, value);
	}
}

public class CounterComponent : ComponentBase {
	private readonly CounterViewModel viewModel = new();

	protected override object Build() => new Card()
		.Background(Colors.background)
		.Content(
			new StackPanel()
				.Orientation(Orientation.Vertical)
				.Children(
				)
		);

	public CounterComponent() {
		viewModel.WhenAnyValue(x => x.count)
			.Subscribe(_ => StateHasChanged());
	}
}