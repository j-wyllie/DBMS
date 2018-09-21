package odms.controller;

import java.io.IOException;
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
        try {
            if (cell.getIndex() < cell.getTableView().getItems().size()) {
                String value = String.valueOf(cell.getTableView().getItems().get(cell.getIndex()));
                openWebpage("www.twitter.com/statuses/" + value);
            }
        } catch (IndexOutOfBoundsException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Opens up a webpage to a url in the computers default browser.
     *
     * @param url Url to open.
     */
    private static void openWebpage(String url) {
        try {
            new ProcessBuilder("x-www-browser", url).start();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
