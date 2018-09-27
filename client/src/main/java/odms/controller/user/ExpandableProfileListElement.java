package odms.controller.user;

import java.lang.reflect.Array;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import odms.commons.model.profile.Profile;

/**
 * Used within the OrganMap
 */
public class ExpandableProfileListElement {
    private String title;
    private ListView<String> namesListView;
    private ArrayList<Profile> profiles;
    // set to true by default
    private boolean hidden = true;

    public ExpandableProfileListElement(String title, ArrayList<Profile> profiles) {
        this.title = title;
        this.profiles = profiles;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ListView<String> getNamesListView() {
        ListView<String> donorsList = new ListView<>();
        ObservableList<String> names = FXCollections.observableArrayList();
        for (Profile profile : profiles) {
            names.add(profile.getFullName());
        }
        donorsList.setItems(names);
        return namesListView;
    }

    public void setContents(ListView<String> contentsListView) {
        this.namesListView = contentsListView;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }
}
