import Geocode from "react-geocode";

Geocode.setApiKey(process.env.REACT_APP_GOOGLE_MAPS_API_KEY);

class LocationService {
    async getLatLong(country, town, address) {
        let location = null;

        if (country || town || address) {
            location = {};
            try {
                let response = await Geocode.fromAddress(country + " " + town + " " + address);

                if (response.status === "ZERO_RESULTS") {
                    throw "Nemoguće je pronaći danu lokaciju. Molimo provjerite upisano."
                }

                const { lat, lng } = response.results[0].geometry.location;
                console.log(lat, lng);
                location.longitude = lng;
                location.latitude = lat;

            } catch (err) {
                throw "Nemoguće je pronaći danu lokaciju. Molimo provjerite upisano."
            }

            location.state = country;
            location.town = town;
            location.adress = address;

        }
        return location;
    }
}

export default new LocationService();