import View.CustomsViewImpl;
import ViewModel.CustomsViewModelImpl;

public class Main {
    public static void main(String[] args) {
        var viewModel = new CustomsViewModelImpl(5);
        var view = new CustomsViewImpl(viewModel);

        view.printWelcomeMessage();

        var readResult = true;
        while (readResult) {
            readResult = view.readInput();
        }
    }
}
