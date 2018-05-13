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
    @FXML
    private TextField inputTextField;

    private final PrintStream out;
    private final InputStream in;

    protected final List<String> history = new ArrayList<>();
    protected int historyPointer = 0;

    private Consumer<String> onMessageReceivedHandler;

    public CommandGUI(TextArea textArea, TextField textField) {
        this.displayTextArea = textArea;
        this.inputTextField = textField;

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

        // handle special key presses
        textField.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER:
                    String text = textField.getText();
                    textArea.appendText(text + System.lineSeparator());
                    history.add(text);
                    historyPointer++;
                    if (onMessageReceivedHandler != null) {
                        onMessageReceivedHandler.accept(text);
                    }
                    textField.clear();
                    break;
                case UP:
                    if (historyPointer == 0) {
                        break;
                    }
                    historyPointer--;
                    runSafe(() -> {
                        textField.setText(history.get(historyPointer));
                        textField.selectAll();
                    });
                    break;
                case DOWN:
                    if (historyPointer == history.size() - 1) {
                        break;
                    }
                    historyPointer++;
                    runSafe(() -> {
                        textField.setText(history.get(historyPointer));
                        textField.selectAll();
                    });
                    break;
                default:
                    break;
            }
        });
        //setBottom(textField);
    }

    public void clear() {
        runSafe(() -> displayTextArea.clear());
    }

    public PrintStream getOut() { return out; }

    public InputStream getIn() { return in; }
}
