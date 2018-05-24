package odms.cli;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static odms.cli.GUIUtils.runSafe;

public class CommandGUI {
    @FXML
    private TextArea displayTextArea;

    private final PrintStream out;
    private final InputStream in;

    /**
     * Create a command line with the give text area as input/output
     *
     * @param textArea
     */
    public CommandGUI(TextArea textArea) {
        this.displayTextArea = textArea;

        //displayTextArea.setEditable(false);
        displayTextArea.setWrapText(true);

        // init IO steams
        Charset charset = Charset.defaultCharset();
        final TextInputControlStream stream = new TextInputControlStream(this.displayTextArea, Charset.defaultCharset());
        try {
            this.out = new PrintStream(stream.getOut(), true, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.in = stream.getIn();
    }

    /**
     * Sets key listeners for command line history
     *
     * @param commandLine
     */
    public void initHistory(CommandLine commandLine) {
        displayTextArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            int textLen = 0;
            try {
                switch (event.getCode()) {
                    case UP:
                        textLen = displayTextArea.getText().length();
                        displayTextArea.deleteText(textLen - commandLine.getHistory().current().length(), textLen);
                        commandLine.getHistory().previous();
                        displayTextArea.appendText(commandLine.getHistory().current());
                        break;
                    case DOWN:
                        textLen = displayTextArea.getText().length();
                        displayTextArea.deleteText(textLen - commandLine.getHistory().current().length(), textLen);
                        commandLine.getHistory().next();
                        displayTextArea.appendText(commandLine.getHistory().current());
                        break;
                }
            } catch (IndexOutOfBoundsException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void clear() {
        runSafe(() -> displayTextArea.clear());
    }

    public PrintStream getOut() { return out; }

    public InputStream getIn() { return in; }
}
