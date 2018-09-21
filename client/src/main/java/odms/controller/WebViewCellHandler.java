package odms.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles the event when a twitter feed is clicked on.
 */
@Slf4j
public class WebViewCellHandler implements EventHandler<MouseEvent> {

    @Override
    public void handle(MouseEvent t) {
        WebViewCell cell = (WebViewCell) t.getSource();
        String value = String.valueOf(cell.getTableView().getItems().get(cell.getIndex()));
        try {
            Desktop.getDesktop().browse(new URI("www.twitter.com/statuses/" + value));
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
