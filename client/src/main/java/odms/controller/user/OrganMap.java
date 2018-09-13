package odms.controller.user;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Callback;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.profile.Organ;
import odms.commons.model.profile.Profile;
import odms.controller.CommonController;
import odms.controller.data.AddressIO;
import odms.controller.database.DAOFactory;
import odms.controller.database.profile.ProfileDAO;

public class OrganMap extends CommonController{

    private odms.view.user.OrganMap view;

    public void setView(odms.view.user.OrganMap v) {
        view = v;
    }

    public ObservableList<Profile> getDeadDonors() {
        ArrayList<Profile> deadDonors = new ArrayList<>();
        ProfileDAO database = DAOFactory.getProfileDao();
        List<Profile> allDonors = null;
        try {
            allDonors = database.getDead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Profile profile : allDonors) {
            // If the profile has organs to donate
            if (!profile.getOrgansDonatingNotExpired().isEmpty()) {
                deadDonors.add(profile);
            }
        }
        return FXCollections.observableArrayList(deadDonors);
    }

    public ObservableList<Profile> getReceivers(Profile donatingProfile) {
        List<Profile> receivingProfiles = new ArrayList<>();

        Set<OrganEnum> donatingOrgans = donatingProfile.getOrgansDonatingNotExpired();

        for (OrganEnum organ : donatingOrgans) {
            receivingProfiles.addAll(AvailableOrgans.getSuitableRecipientsSorted(organ, donatingProfile, organ));
        }

        return FXCollections.observableArrayList(receivingProfiles);
    }

    public Callback<ListView<ExpandableListElement>, ListCell<ExpandableListElement>> getListViewCallback() {
         return new Callback<ListView<ExpandableListElement>, ListCell<ExpandableListElement>>() {
            @Override
            public ListCell<ExpandableListElement> call(final ListView<ExpandableListElement> lv) {
                ListCell<ExpandableListElement> cell = new ListCell<ExpandableListElement>() {
                    @Override
                    protected void updateItem(final ExpandableListElement item, boolean empty) {
                        super.updateItem(item, empty);
                        final VBox vbox = new VBox();
                        setGraphic(vbox);
                        int HEIGHT = 10;

                        if (item != null && getIndex() > -1) {
                            final Label labelHeader = new Label(item.getTitle());
                            labelHeader.setGraphic(createArrowPath(HEIGHT, false));
                            labelHeader.setGraphicTextGap(10);
                            labelHeader.setId("tableview-columnheader-default-bg");
                            labelHeader.setPrefWidth(view.getMatchesListViewWidth() - 10);
                            labelHeader.setPrefHeight(HEIGHT);

                            labelHeader.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent me) {
                                    item.setHidden(item.isHidden() ? false : true);
                                    if (item.isHidden()) {
                                        labelHeader.setGraphic(createArrowPath(HEIGHT, false));
                                        vbox.getChildren().remove(vbox.getChildren().size() - 1);
                                    } else {
                                        labelHeader.setGraphic(createArrowPath(HEIGHT, true));
                                        vbox.getChildren().add(item.getContents());
                                    }
                                }
                            });

                            vbox.getChildren().add(labelHeader);
                        }
                    }

                };
                return cell;
            }
        };
    }

    private SVGPath createArrowPath(int height, boolean up) {
        SVGPath svg = new SVGPath();
        int width = height / 4;

        if (up) {
            svg.setContent(
                    "M" + width + " 0 L" + (width * 2) + " " + width + " L0 " + width + " Z");
        } else {
            svg.setContent("M0 0 L" + (width * 2) + " 0 L" + width + " " + width + " Z");
        }

        return svg;
    }

    public ArrayList<Double> displayPointOnMap(Profile profile){

        return AddressIO.getLongLatRegion(profile.getRegion(), profile.getCountry());

    }
}