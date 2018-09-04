package odms.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * Used within the OrganMap
 */
public class ExpandableListElement {
    private String title;
    private ListView<String> contentsListView;
    // set to true by default
    private boolean hidden = true;

    public ExpandableListElement(String title, ListView<String> contentsListView) {
        this.title = title;
        this.contentsListView = contentsListView;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ListView<String> getContents() {
        return contentsListView;
    }

    public void setContents(ListView<String> contentsListView) {
        this.contentsListView = contentsListView;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }
}
