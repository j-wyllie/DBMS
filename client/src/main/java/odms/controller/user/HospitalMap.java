package odms.controller.user;

import javafx.scene.shape.SVGPath;

public class HospitalMap {
    private odms.view.user.HospitalMap view;

    public void setView(odms.view.user.HospitalMap v) {
        view = v;
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

//    public ArrayList<Double> displayPointOnMap(Profile profile){
//
//        return AddressIO.getLongLatRegion(profile.getRegion(), profile.getCountry());
//
//    }

}
