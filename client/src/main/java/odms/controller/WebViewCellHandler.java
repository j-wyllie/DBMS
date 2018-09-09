package odms.controller;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.net.URI;

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
