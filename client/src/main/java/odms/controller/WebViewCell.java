package odms.controller;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.util.Callback;

/**
 * We view cell to be added to a table.
 *
 * @param <S> The twitter cell string.
 */
public class WebViewCell<S> extends TableCell<S, String> {

    private static final int PREFWIDTH = 500;
    private static final int PREFHEIGHT = 180;
    private final WebView webView;

    /**
     * Creates a new web view cell for each new item.
     *
     * @param <S> Cell to be created.
     * @return A new cell.
     */
    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
        return param -> new WebViewCell<>();
    }

    /**
     * Constructor when creating a new web view cell.
     */
    private WebViewCell() {
        this.webView = new WebView();
        webView.setMinWidth(999);
        webView.setDisable(true);
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, new WebViewCellHandler());
    }

    @Override
    public void updateItem(String item, boolean empty) {
        if (!empty) {
            webView.setPrefSize(PREFWIDTH, PREFHEIGHT);
            String html = "<html lang=\"en\">\n" +
                    "<div id=\"tweet\" tweetID=\"" + item + "\"></div>\n" +
                    "\n" +
                    "\n" +
                    "<script sync src=\"https://platform.twitter.com/widgets.js\"></script>\n" +
                    "\n" +
                    "<script>\n" +
                    "\n" +
                    "    window.onload = (function(){\n" +
                    "\n" +
                    "        var tweet = document.getElementById(\"tweet\");\n" +
                    "        var id = tweet.getAttribute(\"tweetID\");\n" +
                    "\n" +
                    "        twttr.widgets.createTweet(\n" +
                    "            id, tweet,\n" +
                    "            {\n" +
                    "                conversation : 'none',\n" +
                    "                cards        : 'hidden',\n" +
                    "                linkColor    : '#cc0000',\n" +
                    "                theme        : 'light',\n" +
                    "                align        : 'center'\n" +
                    "            })\n" +
                    "        .then (function (el) {\n" +
                    "            el.contentDocument.querySelector(\".footer\").style.display = \"none\";\n" +
                    "        });\n" +
                    "\n" +
                    "    });\n" +
                    "\n" +
                    "</script>\n" +
                    "</html>";
            webView.getEngine().loadContent(html);
            setGraphic(webView);
        }
    }

}
