package odms.controller;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.web.WebView;
import javafx.util.Callback;

public class WebViewCell<S> extends TableCell<S, String> {
    private final WebView webView;

    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
        return param -> new WebViewCell();
    }

    public WebViewCell() {
        this.webView = new WebView();

    }

    @Override
    public void updateItem(String item, boolean empty) {
        if(!empty && !item.equals(null)) {
            webView.setPrefSize(500,200);
            System.out.println(item);
            String html = "<html lang=\"en\">\n"
                    + "<div id=\"tweet\" tweetID=\""+item+"\"></div>\n"
                    + "\n"
                    + "\n"
                    + "<script sync src=\"https://platform.twitter.com/widgets.js\"></script>\n"
                    + "\n"
                    + "<script>\n"
                    + "\n"
                    + "    window.onload = (function(){\n"
                    + "\n"
                    + "        var tweet = document.getElementById(\"tweet\");\n"
                    + "        var id = tweet.getAttribute(\"tweetID\");\n"
                    + "\n"
                    + "        twttr.widgets.createTweet(\n"
                    + "            id, tweet,\n"
                    + "            {\n"
                    + "                conversation : 'none',\n"
                    + "                cards        : 'hidden',\n"
                    + "                linkColor    : '#cc0000',\n"
                    + "                theme        : 'light'\n"
                    + "            })\n"
                    + "        .then (function (el) {\n"
                    + "            el.contentDocument.querySelector(\".footer\").style.display = \"none\";\n"
                    + "        });\n"
                    + "\n"
                    + "    });\n"
                    + "\n"
                    + "</script>\n"
                    + "</html>";
            webView.getEngine().loadContent(html);
            setGraphic(webView);
        }
    }

}
