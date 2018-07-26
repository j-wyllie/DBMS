package odms.model.data;

import java.util.Collections;
import java.util.List;

public class GeocodeResult {
    private List<GeocodeAddressComponent> addressComponents;
    private String formattedAddress;

    public GeocodeResult(List<GeocodeAddressComponent> addressComponents, String formattedAddress) {
        this. addressComponents = addressComponents;
        this.formattedAddress = formattedAddress;
    }

    public List<GeocodeAddressComponent> getAddressComponents() {
        return Collections.unmodifiableList(addressComponents);
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

}
