package odms.controller.user;

import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import javafx.scene.shape.SVGPath;
import odms.commons.model.locations.Hospital;

import java.util.ArrayList;

public class HospitalMap {
    private odms.view.user.HospitalMap view;

    public void setView(odms.view.user.HospitalMap v) {
        view = v;
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

    /**
     * Calculates the distance between two lat long coordinates, using the Haversine method
     *
     * @param lat1 Latitude of first coordinate
     * @param lon1 Longitude of first coordinate
     * @param lat2 Latitude of second coordinate
     * @param lon2 Longitude of second coordinate
     * @return Distance between the two coordinates in km
     */
    public double calcDistanceHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance) / 1000;
    }


    public Polyline createHelicopterRoute(Hospital hospitalSelected1, Hospital hospitalSelected2) {

        LatLong originLatLong = new LatLong(hospitalSelected1.getLatitude(), hospitalSelected1.getLongitude());
        LatLong destinationLatLong = new LatLong(hospitalSelected2.getLatitude(), hospitalSelected2.getLongitude());
        LatLong[] coordinatesList = new LatLong[]{originLatLong, destinationLatLong};

        MVCArray pointsOnMap = new MVCArray(coordinatesList);
        PolylineOptions polyOpts = new PolylineOptions().path(pointsOnMap).strokeColor("blue").strokeWeight(2);

        Polyline helicopterRoute = new Polyline(polyOpts);

        return helicopterRoute;
    }
}
