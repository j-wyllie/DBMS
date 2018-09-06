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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SocialFeedTab {
    @FXML
    TableView tweetTable;

    private ObservableList<String> tweetList;

    private void populateTweetTable() {
        List<String> list = new ArrayList<>();
        list.add("1037191843019214848");
        tweetList = FXCollections.observableArrayList(list);
        tweetTable.setItems(tweetList);
        tweetTable.getColumns().clear();
        TableColumn<String, String> tweetCol = new TableColumn(
                "Tweets");
        tweetCol.setCellValueFactory(
                cdf -> new SimpleStringProperty(cdf.getValue())
        );
        tweetCol.setCellFactory(WebViewCell.forTableColumn()
        );
        tweetTable.getColumns().add(tweetCol);
    }

    public void initialise() {
        populateTweetTable();
    }
}
