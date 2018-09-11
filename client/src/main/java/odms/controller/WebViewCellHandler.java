package odms.controller;

import java.awt.Desktop;
import java.net.URI;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class WebViewCellHandler implements EventHandler<MouseEvent> {
    @Override
    public void handle(MouseEvent t) {
        WebViewCell cell = (WebViewCell) t.getSource();
        String value = String.valueOf(cell.getTableView().getItems().get(cell.getIndex()));
        try {
            Desktop.getDesktop().browse(new URI("www.twitter.com/statuses/"+value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
