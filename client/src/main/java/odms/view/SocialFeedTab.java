package odms.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import odms.controller.WebViewCell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SocialFeedTab {
    @FXML
    TableView tweetTable;

    private ObservableList<String> tweetList;

    private void populateTweetTable() {
        getTweets();

        tweetTable.getColumns().clear();
        TableColumn<String, String> tweetCol = new TableColumn();
        tweetCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue())
        );
        tweetCol.setCellFactory(WebViewCell.forTableColumn()
        );
        System.out.println(tweetCol.getWidth());
        tweetTable.getColumns().add(tweetCol);
    }

    private void getTweets() {
        try {
            URL url = new URL(
                    "https://api.twitter.com/1.1/search/tweets.json?q=humanfarm&result_type=recent");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization","OAuth oauth_consumer_key=\"Xb4pAluzJ0KjfN7npvkX0J0yf\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"1536440321\",oauth_nonce=\"oPXGuVQFN90\",oauth_version=\"1.0\",oauth_signature=\"iBiwE07i%2BQYkJR6rFdciXGLCJPA%3D\"");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            ArrayList<String> ids = new ArrayList<>();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            String s = content.toString();
            String[] strings = s.split(",");
            for(int i=0; i <= strings.length - 1; i++) {
                if(strings[i].contains("\"id\":") && !strings[i].contains("user")) {
                    ids.add(strings[i].replace("\"id\":",""));
                }
            }
            in.close();
            con.disconnect();
            tweetList = FXCollections.observableArrayList(ids);
            tweetTable.setItems(tweetList);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void initialise() {
        populateTweetTable();
    }
}
