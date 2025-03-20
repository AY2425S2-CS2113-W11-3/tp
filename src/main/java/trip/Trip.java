package trip;

import album.Album;
import exception.TravelDiaryException;
import java.util.logging.Logger;

public class Trip {
    private static final Logger logger = Logger.getLogger(Trip.class.getName());

    public String name;
    public String description;
    public String location;
    public Album album;

    public Trip(String name, String description, String location) throws TravelDiaryException {
        logger.info("Initializing Trip object");

        // Ensure the name, description, and location attributes are provided
        if (name == null || description == null || location == null) {
            logger.severe("Missing required tag(s) for add_trip");
            throw new TravelDiaryException("Missing required tag(s) for add_trip. Required: n# (name), " +
                    "d# (description), l# (location). ");
        }

        this.name = name;
        this.description = description;
        this.location = location;
        this.album = new Album();

        // Assertions to validate non-null attributes
        assert this.name != null : "Trip name should not be null";
        assert this.description != null : "Trip description should not be null";
        assert this.location != null : "Trip location should not be null";
        assert this.album != null : "Trip album should not be null";

        logger.info("Trip object successfully created: " + this);
    }

    @Override
    public String toString() {
        return name + " - " + description + " (" + location + ")";
    }
}

