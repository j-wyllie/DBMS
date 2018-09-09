package odms.controller.user;

import com.lynden.gmapsfx.javascript.object.*;
import javafx.scene.shape.SVGPath;
import odms.commons.model.locations.Hospital;

import java.util.ArrayList;

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

    /**
     * Creates hospital marker and adds the relevant details to it.
     *
     * @param hospital the hospital to create a marker for
     * @return a marker object for the given hospital
     */
    public Marker createHospitalMarker(Hospital hospital){

        ArrayList<Double> latLong = new ArrayList<>();
        latLong.add(hospital.getLatitude());
        latLong.add(hospital.getLongitude());
        LatLong hospitalLocation = new LatLong(latLong.get(0), latLong.get(1));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(hospitalLocation);
        markerOptions.label(hospital.getId().toString());
        Marker marker = new Marker(markerOptions);

        return marker;
    }

    /**
     * Creates hospital info window containing a hospitals details
     *
     * @param hospital the hospital to create a info window for
     * @return a info window object for the given hospital
     */
    public InfoWindow createHospitalInfoWindow(Hospital hospital) {

        // Hospital tooltip generated and added
        InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
        infoWindowOptions.content(hospital.getName() + "\n" +
                "X kms from your destination \n" +
                "Services offered: " + hospital.getPrograms());
        InfoWindow infoWindow = new InfoWindow(infoWindowOptions);

        return infoWindow;
    }

    public ArrayList<Hospital> getHospitalsWithinDistance(LatLong latLong, int distance) {
        ArrayList<Hospital> hospitals = new ArrayList<>();

        return hospitals;
    }

    /**
     * Calculates the time in seconds between two given hospitals
     *
     * @param hospital1 hospital one
     * @param hospital2 hospital two
     * @return the time in seconds between the hospitals
     */
    public long calculateTimeBetweenHospitalsSec(Hospital hospital1, Hospital hospital2) {

        return 0;
    }

    /**
     * Calculates the distance in km between two given hospitals
     *
     * @param hospital1 hospital one
     * @param hospital2 hospital two
     * @return the distance in km between the hospitals
     */
    public long calculateDistBetweenHospitalsKm(Hospital hospital1, Hospital hospital2) {

        return 0;
    }


}
