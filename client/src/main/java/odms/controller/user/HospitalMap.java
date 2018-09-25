package odms.controller.user;

import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import com.lynden.gmapsfx.util.MarkerImageFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import odms.commons.model.enums.OrganEnum;
import odms.commons.model.locations.Hospital;
import odms.controller.database.DAOFactory;
import odms.controller.database.locations.HospitalDAO;

/**
 * Controller class for the hospital map view.
 */
public class HospitalMap {

    private odms.view.user.HospitalMap view;

    public void setView(odms.view.user.HospitalMap v) {
        view = v;
    }

    /**
     * Creates location marker and adds the relevant details to it.
     *
     * @param location the location to create a marker for
     * @return a marker object for the given location
     */
    public Marker createLocationMarker(Hospital location) {

        ArrayList<Double> latLong = new ArrayList<>();
        latLong.add(location.getLatitude());
        latLong.add(location.getLongitude());
        LatLong hospitalLocation = new LatLong(latLong.get(0), latLong.get(1));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(hospitalLocation);
        markerOptions.title(location.getName());
        markerOptions.label(String.valueOf(location.getId()));

        if (location.getId() < 0) {
            markerOptions.label("X");
        } else {
            markerOptions.label(location.getId().toString());
        }

        return new Marker(markerOptions);
    }

    /**
     * Creates Location info window containing a locations details.
     *
     * @param location The location to create a info window for
     * @return A info window object for the given hospital containing locations details
     */
    public InfoWindow createLocationInfoWindow(Hospital location) {

        // Hospital tooltip generated and added
        InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
        String locationInfo = location.getName() + ". \n";

        if (location.getId() < 0) {
            locationInfo += "Location: (" + Double.valueOf(odms.view.user.HospitalMap.decimalFormat.format(location.getLatitude())) + ", " +
                    Double.valueOf(odms.view.user.HospitalMap.decimalFormat.format(location.getLongitude())) + ")";
        } else {

            if (location.getAddress() != null) {
                locationInfo += "Address: " + location.getAddress() + ". \n";
            }

            List<String> organPrograms = new ArrayList<>();
            int i = 0;
            for (OrganEnum organ : OrganEnum.values()) {
                if (location.getPrograms().get(i)) {
                    organPrograms.add(organ.getNamePlain());
                }
                i++;
            }

            if (location.getPrograms() != null) {
                locationInfo += "Services offered: " + organPrograms + ".";
            }

        }


        infoWindowOptions.content(locationInfo);

        return new InfoWindow(infoWindowOptions);
    }

    /**
     * Creates a marker options object so a marker can change its appearance according to
     * the provided parameters.
     *
     * @param highlighted if the marker is already highlighted or not
     * @param location the location object the marker is for
     * @param selected the ID of the selected marker, can be A - Z
     * @return A marker options object that allows a marker to change its appearance to a
     * specified look
     */
    public MarkerOptions highlightMarker(Boolean highlighted, Hospital location, String selected) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLong(location.getLatitude(), location.getLongitude()));
        markerOptions.label(String.valueOf(location.getId()));

        if (!highlighted) {

            String markerImage = MarkerImageFactory.createMarkerImage(
                    this.getClass().getResource(
                            "/GoogleMapsMarkers/blue_Marker" + selected + ".png")
                            .toString(), "png");
            markerImage = markerImage.replace("(", "");
            markerImage = markerImage.replace(")", "");
            markerOptions.icon(markerImage);
        }

        return markerOptions;

    }

    /**
     * Calculates the distance between two lat long coordinates, using the Haversine method.
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

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance) / 1000;
    }

    /**
     * Creates a line object that can be added to the map, the line goes from the given location 1
     * to the given location 2 represents a route between the locations.
     *
     * @param originLat latitude the route starts from
     * @param originLong latitude the route starts from
     * @param destinationLat latitude the route ends at
     * @param destinationLong longitude the route ends at
     * @return A Polyline object that can be added to a map to represent a line
     */
    public Polyline createHelicopterRoute(Double originLat, Double originLong,
            Double destinationLat, Double destinationLong) {

        LatLong originLatLong = new LatLong(originLat, originLong);
        LatLong destinationLatLong = new LatLong(destinationLat, destinationLong);
        LatLong[] coordinatesList = new LatLong[]{originLatLong, destinationLatLong};

        MVCArray pointsOnMap = new MVCArray(coordinatesList);
        PolylineOptions polyOpts = new PolylineOptions().path(pointsOnMap)
                .strokeColor("blue").strokeWeight(2);

        return new Polyline(polyOpts);
    }

    /**
     * Gets all hospitals from the database.
     * @return list of hospitals
     */
    public List<Hospital> getHospitals() {
        HospitalDAO hospitalDAO = DAOFactory.getHospitalDAO();
        try {
            return hospitalDAO.getAll();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
}
