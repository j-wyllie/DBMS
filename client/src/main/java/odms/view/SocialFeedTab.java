package odms.view;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class SocialFeedTab {
    @FXML
    WebView socialFeedWebView;

    public void initialise() {
        socialFeedWebView.getEngine().load("http://www.google.com");
    }
}
