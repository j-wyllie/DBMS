package odms.view;


import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import odms.controller.WebViewCell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@Slf4j
public class SocialFeedTab {

    @FXML
    private TableView tweetTable;
    @FXML
    private Button refreshButton;

    /**
     * Populates the table with tweets and adds a column that constructs a WebViewCell factory.
     */
    private void populateTweetTable() {
        getTweets();

        tweetTable.getColumns().clear();
        TableColumn<String, String> tweetCol = new TableColumn<>();
        tweetCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue())
        );
        tweetCol.setCellFactory(WebViewCell.forTableColumn()
        );
        tweetTable.getColumns().add(tweetCol);
    }

    /**
     * Handles the action for the refresh button to refresh the table.
     */
    @FXML
    private void handleRefreshButtonClicked() {
        populateTweetTable();
    }

    /**
     * Calls the twitter API in order to get all tweets with the hashtag humanfarm.
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

            setupConAuthorization(con, con2);

            List<String> ids = handleRequest(con);

            ObservableList<String> tweetList = FXCollections.observableArrayList(ids);
            tweetTable.setItems(tweetList);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Handles the request and gets a list of ids of twitter users. Closes the connection.
     * @param con connection to the twitter api.
     * @return a list of twitter ids.
     * @throws IOException thrown if the connection can not be established.
     */
    private List<String> handleRequest(HttpURLConnection con)
            throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
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
        return ids;
    }

    /**
     * Sends a request to the twitter api to get the token.
     * @param con Connection being setup with an authorization token.
     * @param con2 Connection to the twitter api to retrieve a token.
     */
    private void setupConAuthorization(HttpURLConnection con, HttpURLConnection con2) {
        try {
            con2.setRequestProperty("Authorization",
                    "Basic WGI0cEFsdXpKMEtqZk43bnB2a1gwSjB5ZjpOa2o5NXh0UU1icndna3ZXVWlXYVF6WDU5VDBMcmVwQkczaVlaUGJSQmh4ZUFNNmh0TA==");
            con2.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            con2.setRequestProperty("Content-Length",
                    Integer.toString("grant_type=client_credentials".length()));
            con2.getOutputStream().write("grant_type=client_credentials".getBytes("UTF8"));
            con2.getOutputStream().flush();
            con2.getOutputStream().close();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con2.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            String s2 = content.toString();
            s2 = s2.substring(s2.indexOf("token\":\"") + 8, s2.indexOf("\"}"));
            con.setRequestProperty("Authorization", "Bearer " + s2);

            con2.disconnect();

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * initializes the tab by calling populateTweetTable().
     */
    public void initialise() {
        populateTweetTable();
    }

    public Button getRefreshButton() {
        return refreshButton;
    }

    public void setRefreshButton(Button refreshButton) {
        this.refreshButton = refreshButton;
    }
}
