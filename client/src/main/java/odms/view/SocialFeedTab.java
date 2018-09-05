package odms.view;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;

public class SocialFeedTab {
    @FXML
    WebView socialFeedWebView;

    public void initialise() {
        URL url = getClass().getResource("/SocialFeed/SocialFeed.html");
        System.out.println(url);
        socialFeedWebView.getEngine().load(url.toExternalForm());
    }
}
