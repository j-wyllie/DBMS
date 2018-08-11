package odms.data;

import java.util.Collections;
import java.util.List;

public class GeocodeResponse {
    private List<GeocodeResult> results;
    private String status;

    public GeocodeResponse(List<GeocodeResult> geocodeResults, String status) {
        this.results = geocodeResults;
        this.status = status;
    }

    public List<GeocodeResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    public String getStatus() {
        return status;
    }
}
