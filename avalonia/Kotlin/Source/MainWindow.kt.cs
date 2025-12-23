namespace yurin.mal
{
    public sealed class MainWindow : global::Avalonia.Controls.Window
    {
        public MainWindow() : base()
        {
            {
                (this).Width = 1000;
                (this).Height = 600;
                (this).Title = "Minecraft Avalonia Launcher";
                (this).Content = new global::yurin.mal.MALComponent();
            }
        }
    }
}