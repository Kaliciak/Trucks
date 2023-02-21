import View.CustomsViewImpl;
import ViewModel.CustomsViewModelImpl;

public class Main {
    public static void main(String[] args) {
        var viewModel = new CustomsViewModelImpl(5);
        var view = new CustomsViewImpl(viewModel);

        view.printWelcomeMessage();

        // repeat until view.readInput() returns false
        do {} while (view.readInput());

        view.printExitMessage();
    }
}
