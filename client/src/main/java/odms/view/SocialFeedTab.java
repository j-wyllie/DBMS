package odms.view;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import odms.controller.WebViewCell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SocialFeedTab {

    @FXML
    TableView tweetTable;
    @FXML
    Button refreshButton;

    private ObservableList<String> tweetList;

    /**
     * Populates the table with tweets and adds a column that constructs a WebViewCell factory
     */
    private void populateTweetTable() {
        getTweets();

        tweetTable.getColumns().clear();
        TableColumn<String, String> tweetCol = new TableColumn();
        tweetCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue())
        );
        tweetCol.setCellFactory(WebViewCell.forTableColumn()
        );
        tweetTable.getColumns().add(tweetCol);
    }

    /**
     * Handles the action for the refresh button to refresh the table
     */
    @FXML
    private void handleRefreshButtonClicked(ActionEvent event) {
        populateTweetTable();
    }

    /**
     * Calls the twitter API in order to get all tweets with the hashtag humanfarm
     */
    private void getTweets() {
        try {
            URL url = new URL(
                    "https://api.twitter.com/1.1/search/tweets.json?q=%23humanfarm&result_type=recent");
            URL url2 = new URL(
                    "https://api.twitter.com/oauth2/token");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            HttpURLConnection con2 = (HttpURLConnection) url2.openConnection();
            con.setRequestMethod("GET");
            con2.setRequestMethod("POST");
            con2.setDoOutput(true);
            try {
                con2.setRequestProperty("Authorization", "Basic WGI0cEFsdXpKMEtqZk43bnB2a1gwSjB5ZjpOa2o5NXh0UU1icndna3ZXVWlXYVF6WDU5VDBMcmVwQkczaVlaUGJSQmh4ZUFNNmh0TA==");
                con2.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                con2.setRequestProperty("Content-Length", Integer.toString("grant_type=client_credentials".length()));
                con2.getOutputStream().write("grant_type=client_credentials".getBytes("UTF8"));
                con2.getOutputStream().flush();
                con2.getOutputStream().close();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con2.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                String s2 = content.toString();
                s2 = s2.substring(s2.indexOf("token\":\"")+8,s2.indexOf("\"}"));
                con.setRequestProperty("Authorization", "Bearer "+s2);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            for (int i = 0; i <= strings.length - 1; i++) {
                if (strings[i].contains("\"id\":") && !strings[i].contains("user")) {
                    ids.add(strings[i].replace("\"id\":", ""));
                }
            }
            in.close();
            con.disconnect();
            con2.disconnect();
            tweetList = FXCollections.observableArrayList(ids);
            tweetTable.setItems(tweetList);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialise() {
        populateTweetTable();
    }
}
