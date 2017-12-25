package pw.avvero.crools;

import cucumber.api.SummaryPrinter;

import java.io.PrintStream;
import java.util.List;

/**
 * Created by Avvero on 25.12.2017.
 */
public class ValidationPlugin implements SummaryPrinter {
    private final PrintStream out;

    public ValidationPlugin() {
        this.out = System.out;
    }

    public void print(cucumber.runtime.Runtime runtime) {
        printSnippets(runtime);
    }

    private void printSnippets(cucumber.runtime.Runtime runtime) {
        List<String> snippets = runtime.getSnippets();
        if (!snippets.isEmpty()) {
            StringBuilder message = new StringBuilder("You must implement missing steps with the snippets below:\n");

            for (String snippet : snippets) {
                message.append(snippet);
            }
            throw new RuntimeException(message.toString());
        }
    }
}